package com.github.homeant.data.shield.proxy;

import ma.glasnost.orika.MapperFactory;

import java.util.List;

public interface ProxyFactory {

    <S, T> List<T> createProxy(List<S> sourceList, Class<S> sourceClass, Class<T> targetClass);

    <S, T> T createProxy(S source, Class<S> sourceClass, Class<T> targetClass);

    <S, T> T createProxy(S source, Class<S> sourceClass, Class<T> targetClass, MapperFactory mapperFactory);
}
