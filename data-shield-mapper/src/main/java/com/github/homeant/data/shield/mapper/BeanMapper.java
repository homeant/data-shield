package com.github.homeant.data.shield.mapper;

public interface BeanMapper {

    <R,T> T map(R source,Class<T> targetClazz);
}
