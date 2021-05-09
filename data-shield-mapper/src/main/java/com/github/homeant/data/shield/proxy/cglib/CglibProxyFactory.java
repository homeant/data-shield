package com.github.homeant.data.shield.proxy.cglib;

import com.github.homeant.data.shield.annotation.Mapping;
import com.github.homeant.data.shield.proxy.ProxyFactory;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.ibatis.executor.loader.WriteReplaceInterface;
import org.apache.ibatis.reflection.property.PropertyCopier;
import org.apache.ibatis.reflection.property.PropertyNamer;

import java.io.File;
import java.lang.reflect.*;
import java.util.*;

@Slf4j
public class CglibProxyFactory implements ProxyFactory {

    private static final String FINALIZE_METHOD = "finalize";
    private static final String WRITE_REPLACE_METHOD = "writeReplace";


    @Override
    public Object createProxy(Object target, Object source, MapperFactory mapperFactory) {
        return EnhancedBeanProxyImpl.createProxy(target, source, mapperFactory);
    }

    static class EnhancedBeanProxyImpl implements MethodInterceptor {

        private final Object source;

        private final Class<?> type;

        private final MapperFactory mapperFactory;

        public EnhancedBeanProxyImpl(Object source, Class<?> type, MapperFactory mapperFactory) {
            this.source = source;
            this.type = type;
            this.mapperFactory = mapperFactory;
        }

        public static Object createProxy(Object target, Object source, MapperFactory mapperFactory) {
            log.info("create proxy:{}|{}", target.getClass().getSimpleName(), source.getClass().getSimpleName());
            Class<?> type = target.getClass();
            Enhancer enhancer = new Enhancer();
            EnhancedBeanProxyImpl callback = new EnhancedBeanProxyImpl(source, type, mapperFactory);
            enhancer.setCallback(callback);
            enhancer.setSuperclass(type);
            try {
                //获取目标类型的 writeReplace 方法，如果没有，异常中代理类设置enhancer.setInterfaces(new Class[]{WriteReplaceInterface.class});
                type.getDeclaredMethod(CglibProxyFactory.WRITE_REPLACE_METHOD);
                // ObjectOutputStream will call writeReplace of objects returned by writeReplace
                log.debug(WRITE_REPLACE_METHOD + " method was found on bean " + type + ", make sure it returns this");
            } catch (NoSuchMethodException e) {
                enhancer.setInterfaces(new Class[]{WriteReplaceInterface.class});
            } catch (SecurityException e) {
                // nothing to do here
            }
            Object result = enhancer.create();
            PropertyCopier.copyBeanProperties(type, target, result);
            return result;
        }

        @Override
        public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            String methodName = method.getName();
            log.info("proxy class {}.{}", object.getClass().getSimpleName(), methodName);
            if (!FINALIZE_METHOD.equals(methodName)) {
                if (PropertyNamer.isProperty(methodName)) {
                    String property = PropertyNamer.methodToProperty(methodName);
                    Field targetField = type.getDeclaredField(property);
                    Mapping annotation = targetField.getAnnotation(Mapping.class);
                    if (annotation != null && annotation.lazy()) {
                        Class<?> targetType = targetField.getType();
                        if (targetType == List.class || targetType == Set.class) {
                            Type genericType = targetField.getGenericType();
                            if (genericType != null && genericType instanceof ParameterizedType) {
                                Type actualType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
                                targetType = Class.forName(actualType.getTypeName());
                            }
                        }
                        Class parentSourceType = source.getClass();
                        Class sourceType = null;
                        try {
                            Class.forName(source.getClass().getSimpleName());
                        } catch (ClassNotFoundException e) {
                            parentSourceType = source.getClass().getSuperclass();
                        }
                        Field sourceFile = null;
                        if (annotation.value() != null && !"".equals(annotation.value())) {
                            sourceType = getType(parentSourceType, annotation.value());
                        } else {
                            sourceType = getType(parentSourceType, property);
                        }
                        log.info("sourceType:{}", sourceType);
                        ClassMapBuilder<?, ?> classMapBuilder = mapperFactory.classMap(sourceType, targetType);
                        for (Field field : targetType.getDeclaredFields()) {
                            Mapping mapping = field.getAnnotation(Mapping.class);
                            if (mapping != null) {
                                String fieldName = field.getName();
                                String value = mapping.value();
                                if (value != null && !"".equals(value)) {
                                    classMapBuilder.field(value, fieldName);
                                    fieldName = value;
                                }
                                if (mapping.lazy()) {
                                    classMapBuilder.exclude(fieldName);
                                }

                            }
                        }
                        String value = annotation.value();
                        if (value != null && !"".equals(value)) {
                            methodName = getMethodName(value);
                        }
                        classMapBuilder.byDefault().register();
                        Method getMethod = parentSourceType.getMethod(methodName);
                        Object sourceResult = getMethod.invoke(source, null);
                        if (sourceResult != null) {
                            targetField.setAccessible(true);
                            if (sourceResult instanceof List) {
                                Iterator<?> iterator = ((List<?>) sourceResult).iterator();
                                List list = new ArrayList();
                                while (iterator.hasNext()) {
                                    Object next = iterator.next();
                                    Object map = mapperFactory.getMapperFacade().map(next, targetType);
                                    list.add(createProxy(map, next, mapperFactory));
                                }
                                targetField.set(object, list);
                            } else {
                                Object result = mapperFactory.getMapperFacade().map(sourceResult, targetType);
                                targetField.set(object, createProxy(result, sourceResult, mapperFactory));
                            }
                        }
                    }
                }
            }
            return methodProxy.invokeSuper(object, args);
        }
    }

    private static Class getType(Class clazz, String property) {
        try {
            Field field = clazz.getDeclaredField(property);
            return field.getType();
        } catch (NoSuchFieldException e) {

        }
        return null;
    }

    private static String getMethodName(String field) {
        return "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
    }
}
