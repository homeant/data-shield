package com.github.homeant.data.shield.proxy.cglib;

import com.github.homeant.data.shield.annotation.Mapping;
import com.github.homeant.data.shield.proxy.ProxyFactory;
import com.github.homeant.data.shield.proxy.ResultMapping;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.ibatis.executor.loader.WriteReplaceInterface;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.apache.ibatis.reflection.property.PropertyCopier;
import org.apache.ibatis.reflection.property.PropertyNamer;

import java.io.File;
import java.lang.reflect.*;
import java.util.*;

@Slf4j
public class CglibProxyFactory implements ProxyFactory {

    @Override
    public Object createProxy(Object target, Object source, MapperFactory mapperFactory) {
        return EnhancedBeanProxyImpl.createProxy(target, source, mapperFactory);
    }

    static class EnhancedBeanProxyImpl implements MethodInterceptor {

        private final Object source;

        private final Class<?> type;

        private final MapperFactory mapperFactory;

        private final MapperFacade mapperFacade;

        private Map<String, ResultMapping> cacheMap = new HashMap<>();

        private ReflectorFactory reflectorFactory = new DefaultReflectorFactory();

        public EnhancedBeanProxyImpl(Object source, Class<?> type, MapperFactory mapperFactory) {
            this.source = source;
            this.type = type;
            this.mapperFactory = mapperFactory;
            this.mapperFacade = mapperFactory.getMapperFacade();
            for (Field field : type.getDeclaredFields()) {
                Mapping mapping = field.getAnnotation(Mapping.class);
                if (mapping != null) {
                    ResultMapping resultMapping = new ResultMapping();
                    resultMapping.setField(field);
                    resultMapping.setFormat(mapping.format());
                    resultMapping.setLazy(mapping.lazy());
                    resultMapping.setValue(mapping.value());
                    cacheMap.put(field.getName(), resultMapping);
                }
            }
        }

        public static Object createProxy(Object target, Object source, MapperFactory mapperFactory) {
            log.info("create proxy:{}|{}", target.getClass().getSimpleName(), source.getClass().getSimpleName());
            Class<?> type = target.getClass();
            Enhancer enhancer = new Enhancer();
            EnhancedBeanProxyImpl callback = new EnhancedBeanProxyImpl(source, type, mapperFactory);
            enhancer.setCallback(callback);
            enhancer.setSuperclass(type);
            Object result = enhancer.create();
            PropertyCopier.copyBeanProperties(type, target, result);
            return result;
        }

        @Override
        public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            String methodName = method.getName();
            if (PropertyNamer.isProperty(methodName)) {
                String property = PropertyNamer.methodToProperty(methodName);
                if (cacheMap.containsKey(property)) {
                    ResultMapping resultMapping = cacheMap.get(property);
                    if(PropertyNamer.isSetter(methodName)){
                        resultMapping.setLazy(false);
                    }
                    if (resultMapping.isLazy()) {
                        log.debug("lazy handler {}", methodName);
                        Field targetFile = type.getDeclaredField(property);
                        Class<?> targetFileType = targetFile.getType();
                        String sourceFileProperty = resultMapping.getValue();
                        if ("".equals(sourceFileProperty)) {
                            sourceFileProperty = property;
                        }
                        Reflector reflector = reflectorFactory.findForClass(source.getClass());
                        Invoker invoker = reflector.getGetInvoker(sourceFileProperty);
                        Object sourceFileResult = invoker.invoke(source, null);
                        if (targetFileType == List.class) {
                            Type genericType = targetFile.getGenericType();
                            if (genericType != null && genericType instanceof ParameterizedType) {
                                Type actualType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
                                targetFileType = Class.forName(actualType.getTypeName());
                            }
                            List list = (List) sourceFileResult;
                            if (list != null && list.size() > 0) {
                                Reflector listReflector = reflectorFactory.findForClass(list.get(0).getClass());
                                mappingRegister(listReflector.getType(), targetFileType, mapperFactory);
                                List resultList = new ArrayList();
                                for (Object o : list) {
                                    resultList.add(createProxy(o, sourceFileResult, mapperFactory));
                                }
                                targetFile.setAccessible(true);
                                targetFile.set(object, resultList);
                            }
                        } else {
                            Class<?> sourceFileType = reflector.hasGetter("handler") ? reflector.getType().getSuperclass() : reflector.getType();
                            mappingRegister(sourceFileType, targetFileType, mapperFactory);
                            Object result = mapperFacade.map(sourceFileResult, targetFileType);
                            targetFile.setAccessible(true);
                            targetFile.set(object, createProxy(result, sourceFileResult, mapperFactory));
                        }
                        resultMapping.setLazy(false);
                    }
                }
            }
            return methodProxy.invokeSuper(object, args);
        }
    }

    private static void mappingRegister(Class<?> sourceFileType, Class<?> targetFileType, MapperFactory mapperFactory) {
        Field[] fields = targetFileType.getDeclaredFields();
        ClassMapBuilder<?, ?> classMapBuilder = mapperFactory.classMap(sourceFileType, targetFileType);
        for (Field field : fields) {
            Mapping annotation = field.getAnnotation(Mapping.class);
            if (annotation != null) {
                String value = annotation.value();
                if (value != null && !"".equals(value)) {
                    classMapBuilder.field(value, field.getName());
                }
            }
        }
        classMapBuilder.byDefault().register();
    }
}
