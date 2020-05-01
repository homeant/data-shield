package fun.vyse.cloud.data.shield.mybatis.interceptor;

import fun.vyse.cloud.data.shield.annotation.TableField;
import fun.vyse.cloud.data.shield.process.IDataProcess;
import lombok.Data;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import java.lang.reflect.Field;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class,
                Object.class})
})
@Data
public class EncryptInterceptor implements Interceptor {

    private final IDataProcess dataProcess;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object parameter = invocation.getArgs()[1];
        Class<?> clazz = parameter.getClass();
        if (clazz.getSuperclass().isInstance(Object.class)) {
            Field[] declaredFields = parameter.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                TableField annotation = field.getAnnotation(TableField.class);
                if (annotation != null && annotation.encrypt()) {
                    if (field.getGenericType() == String.class) {
                        field.setAccessible(true);
                        String value = (String) field.get(parameter);
                        field.set(parameter, dataProcess.encrypt(value));
                    }
                }
            }
        }
        return invocation.proceed();
    }
}
