package com.github.homeant.data.shield.proxy;

import ma.glasnost.orika.MapperFactory;

public interface ProxyFactory {

    /**
     *
     * @param target 顶级对象
     * @param source mybatis返回的延迟对象
     * @param mapperFactory
     * @return
     */
    Object createProxy(Object target,Object source, MapperFactory mapperFactory);
}
