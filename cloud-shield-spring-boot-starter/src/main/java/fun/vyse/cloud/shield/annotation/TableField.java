package fun.vyse.cloud.shield.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface TableField {
    boolean encrypt() default false;

    boolean decode() default false;
}
