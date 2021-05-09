package com.github.homeant.data.shield.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapping {

    String value() default "";

    boolean lazy() default false;

    String format() default "";
}
