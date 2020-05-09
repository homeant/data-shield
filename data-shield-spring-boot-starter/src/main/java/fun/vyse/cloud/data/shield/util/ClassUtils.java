package fun.vyse.cloud.data.shield.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Objects;

/**
 * class 工具累
 */
public class ClassUtils {
    public static boolean isPrimitive(Class clazz) {
        if(clazz.isPrimitive()){
            return true;
        }
        if(clazz.isEnum()){
            return true;
        }
        if (Objects.equals(clazz,Integer.class)  || Objects.equals(clazz , Long.class) || Objects.equals(clazz , Short.class)
                || Objects.equals(clazz , Boolean.class) || Objects.equals(clazz , Byte.class) || Objects.equals(clazz , Character.class) || Objects.equals(clazz , Double.class)
                || Objects.equals(clazz , Float.class) || Objects.equals(clazz , String.class)) {
            return true;
        } else if (Objects.equals(clazz , BigDecimal.class)) {
            return true;
        } else if (Objects.equals(clazz , Date.class) || Objects.equals(clazz , LocalDate.class) || Objects.equals(clazz , LocalTime.class) || Objects.equals(clazz , LocalDateTime.class)) {
            return true;
        }
        return false;
    }
}
