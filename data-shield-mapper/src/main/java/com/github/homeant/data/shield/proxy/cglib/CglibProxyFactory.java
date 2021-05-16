package com.github.homeant.data.shield.proxy.cglib;

import com.github.homeant.data.shield.annotation.Mapping;
import com.github.homeant.data.shield.proxy.ProxyFactory;
import com.github.homeant.data.shield.proxy.ResultMapping;
import lombok.extern.slf4j.Slf4j;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import ma.glasnost.orika.metadata.ClassMapBuilder;
import ma.glasnost.orika.metadata.TypeFactory;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.*;
import java.util.*;

@Slf4j
public class CglibProxyFactory implements ProxyFactory {

    @Override
    public <S, T> List<T> createProxy(List<S> sourceList, Class<S> sourceClass, Class<T> targetClass) {
        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>(sourceList.size());
        for (S source : sourceList) {
            list.add(this.createProxy(source, sourceClass, targetClass, new DefaultMapperFactory.Builder().build()));
        }
        return list;
    }

    @Override
    public <S, T> T createProxy(S source, Class<S> sourceClass, Class<T> targetClass) {
        return createProxy(source, sourceClass, targetClass, new DefaultMapperFactory.Builder().build());
    }

    @Override
    @SuppressWarnings("all")
    public <S, T> T createProxy(S source, Class<S> sourceClass, Class<T> targetClass, MapperFactory mapperFactory) {
        if (source == null) {
            return null;
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetClass);
        MethodInterceptor callback = new EnhancedBeanProxy(source, sourceClass, targetClass, mapperFactory);
        enhancer.setCallback((Callback) callback);
        Object result = enhancer.create();
        T sourceResult = mapperFactory.getMapperFacade().map(source, targetClass);
        copyBeanProperties(targetClass, sourceResult, result);
        return (T) result;
    }

    public static void copyBeanProperties(Class<?> type, Object sourceBean, Object destinationBean) {
        for (Class parent = type; parent != null; parent = parent.getSuperclass()) {
            Field[] fields = parent.getDeclaredFields();
            Field[] var5 = fields;
            int var6 = fields.length;

            for (int var7 = 0; var7 < var6; ++var7) {
                Field field = var5[var7];
                try {
                    try {
                        field.set(destinationBean, field.get(sourceBean));
                    } catch (IllegalAccessException var10) {
                        field.setAccessible(true);
                        field.set(destinationBean, field.get(sourceBean));
                    }
                } catch (Exception var11) {
                }
            }
        }
    }


    static class EnhancedBeanProxy<S, T> implements MethodInterceptor {

        private final Class<S> sourceType;

        private final Class<T> targetType;

        private final Object source;

        private final MapperFactory mapperFactory;

        private final Map<String, ResultMapping> mappingMap = new HashMap<>();

        private final ProxyFactory proxyFactory = new CglibProxyFactory();

        public EnhancedBeanProxy(Object source, Class<S> sourceType, Class<T> targetType, MapperFactory mapperFactory) {
            this.sourceType = sourceType;
            this.targetType = targetType;
            this.source = source;
            this.mapperFactory = mapperFactory;
            ClassMapBuilder<?, ?> classMapBuilder = mapperFactory.classMap(sourceType, targetType);
            boolean exists = mapperFactory.existsRegisteredMapper(TypeFactory.valueOf(sourceType), TypeFactory.valueOf(targetType), false);
            ReflectionUtils.doWithFields(targetType, field -> {
                Mapping mapping = field.getAnnotation(Mapping.class);
                if (mapping != null) {
                    String actualMapping = field.getName();
                    ResultMapping resultMapping = new ResultMapping();
                    resultMapping.setField(field);
                    resultMapping.setLazy(mapping.lazy());
                    resultMapping.setActualMapping(field.getName());
                    if (isNotBlank(mapping.value())) {
                        actualMapping = mapping.value();
                    }
                    resultMapping.setActualMapping(actualMapping);
                    Field sourceField = ReflectionUtils.findField(sourceType, actualMapping);
                    if (!exists) {
                        if (mapping.lazy() && sourceField != null) {
                            classMapBuilder.exclude(actualMapping);
                        } else if (isNotBlank(mapping.value()) && sourceField != null) {
                            classMapBuilder.field(actualMapping, field.getName());
                        }
                    }
                    mappingMap.put(field.getName(), resultMapping);
                }
            });
            if (!exists) {
                classMapBuilder.byDefault().register();
            } else {
                log.debug("{},{},The mapper already exists and does not need to be registered", sourceType, targetType);
            }
        }


        @Override
        @SuppressWarnings("all")
        public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            String methodName = method.getName();
            String property = methodToProperty(methodName);
            if (isGetMethod(methodName) && mappingMap.containsKey(property)) {
                ResultMapping resultMapping = mappingMap.get(property);
                if (resultMapping.isLazy()) {
                    Field sourceField = ReflectionUtils.findField(sourceType, resultMapping.getActualMapping());
                    log.debug("merthodName:{}", propertyToGetMethod(resultMapping.getActualMapping()));
                    Method sourceMethod = ReflectionUtils.findMethod(sourceType, propertyToGetMethod(resultMapping.getActualMapping()));
                    Field field = ReflectionUtils.findField(targetType, property);
                    if (field != null && sourceField != null && sourceMethod != null) {
                        Object result = ReflectionUtils.invokeMethod(sourceMethod, source);
                        ReflectionUtils.makeAccessible(field);
                        if (sourceField.getType() == List.class && field.getType() == List.class) {
                            List resultList = (List) result;
                            List list = new ArrayList(((List) result).size());
                            Class<?> sourceArgsType = getArgumentType(sourceField);
                            Class<?> targetType = getArgumentType(field);
                            for (Object o : resultList) {
                                list.add(proxyFactory.createProxy((S) o, (Class<S>) sourceArgsType, (Class<T>) targetType, mapperFactory));
                            }
                            ReflectionUtils.setField(field, object, list);
                        } else {
                            ReflectionUtils.setField(field, object, proxyFactory.createProxy((S) result, (Class<S>) sourceField.getType(), (Class<T>) field.getType(), mapperFactory));
                        }
                    }
                    resultMapping.setLazy(false);
                }
            }
            if (isSetMethod(methodName) && mappingMap.containsKey(property)) {
                if (mappingMap.get(property).isLazy()) {
                    mappingMap.get(property).setLazy(false);
                }
            }
            return methodProxy.invokeSuper(object, args);
        }

        private String methodToProperty(String name) {
            if (isGetMethod(name) || isSetMethod(name)) {
                name = name.substring(3);
            }
            if (name.length() == 1 || name.length() > 1 && !Character.isUpperCase(name.charAt(1))) {
                name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
            }
            return name;
        }

        private String propertyToGetMethod(String property) {
            return "get" + property.substring(0, 1).toUpperCase(Locale.ENGLISH) + property.substring(1);
        }

        private boolean isGetMethod(String name) {
            return name.startsWith("get");
        }

        private boolean isSetMethod(String name) {
            return name.startsWith("set");
        }

        private boolean isNotBlank(String value) {
            return value != null && !"".equals(value) && !value.trim().equals("");
        }

        private Class<?> getArgumentType(Field field) throws ClassNotFoundException {
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                Type actualType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
                return Class.forName(actualType.getTypeName());
            }
            return null;
        }

    }
}
