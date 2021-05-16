package com.github.homeant.data.shield.mapper.orika;

import com.github.homeant.data.shield.mapper.BeanMapper;
import com.github.homeant.data.shield.proxy.ProxyFactory;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        return (T) proxyFactory.createProxy(source, sourceType, targetClazz,mapperFactory);
    }

    public <R, T> List<T> mapList(List<R> sourceList, Class<T> targetClazz) {
        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        }
        Class<R> sourceType = (Class<R>) sourceList.get(0).getClass();
        try {
            Class.forName(sourceType.getSimpleName());
        } catch (ClassNotFoundException e) {
            sourceType = (Class<R>) sourceType.getSuperclass();
        }
        List list = new ArrayList();
        for (R source : sourceList) {
            list.add(proxyFactory.createProxy(source,sourceType,targetClazz,mapperFactory));
        }
        return list;
    }

    public ProxyFactory getProxyFactory() {
        return proxyFactory;
    }

}
