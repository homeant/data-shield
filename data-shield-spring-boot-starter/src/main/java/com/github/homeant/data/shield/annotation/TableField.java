package com.github.homeant.data.shield.annotation;

import com.github.homeant.data.shield.asserting.DefaultAssert;
import com.github.homeant.data.shield.asserting.IAssert;
import com.github.homeant.data.shield.dataMasking.DataMasking;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface TableField {
    /**
     * 是否需要加密
     * @return
     */
    boolean encrypt() default false;

    /**
     * 是否需要解密
     * @return
     */
    boolean decode() default false;

    Class<? extends DataMasking> dataMasking() default DataMasking.class;

    Class<? extends IAssert>[] asserts() default DefaultAssert.class;
}
