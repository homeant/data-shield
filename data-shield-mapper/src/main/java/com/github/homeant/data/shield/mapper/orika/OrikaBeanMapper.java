package com.github.homeant.data.shield.mapper.orika;

import com.github.homeant.data.shield.annotation.Mapping;
import com.github.homeant.data.shield.mapper.BeanMapper;
import com.github.homeant.data.shield.proxy.ProxyFactory;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;

import java.lang.reflect.Field;

public class OrikaBeanMapper implements BeanMapper {

    private final ProxyFactory proxyFactory;

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    public OrikaBeanMapper(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }


    @Override
    public <R, T> T map(R source, Class<T> targetClazz) {
        Class sourceType = source.getClass();
        try {
            Class.forName(source.getClass().getSimpleName());
        } catch (ClassNotFoundException e) {
            sourceType = source.getClass().getSuperclass();
        }
        Field[] fields = targetClazz.getDeclaredFields();
        ClassMapBuilder<?, T> classMapBuilder = mapperFactory.classMap(sourceType, targetClazz);
        for (Field field : fields) {
            Mapping mapping = field.getAnnotation(Mapping.class);
            if (mapping != null) {
                String fieldName = field.getName();
                String value = mapping.value();
                if (value != null && !"".equals(value)) {
                    fieldName = value;
                }
                if (mapping.lazy()) {
                    classMapBuilder.exclude(fieldName);
                }
            }
        }
        classMapBuilder.byDefault().register();
        Object result = mapperFactory.getMapperFacade(sourceType, targetClazz).map(source);
        return (T) proxyFactory.createProxy(result, source, mapperFactory);
    }

    public ProxyFactory getProxyFactory() {
        return proxyFactory;
    }
}
