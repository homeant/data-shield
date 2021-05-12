package com.github.homeant.data.shield.proxy;

import lombok.Data;

import java.lang.reflect.Field;

@Data
public class ResultMapping {
    private String actualMapping;

    private Field field;

    private String format;

    private boolean lazy;
}
