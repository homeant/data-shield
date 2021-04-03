package com.github.homeant.data.shield.dataMasking;

public interface DataMasking {
    default String apply(String value){
        return value;
    }
}
