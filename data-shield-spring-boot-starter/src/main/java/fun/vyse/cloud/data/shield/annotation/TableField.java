package fun.vyse.cloud.data.shield.annotation;

import fun.vyse.cloud.data.shield.asserting.DefaultAssert;
import fun.vyse.cloud.data.shield.asserting.IAssert;

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

    Class<? extends IAssert> assertion() default DefaultAssert.class;
}
