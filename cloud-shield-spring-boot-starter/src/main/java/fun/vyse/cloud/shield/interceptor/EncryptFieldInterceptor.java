package fun.vyse.cloud.shield.interceptor;

import fun.vyse.cloud.shield.encrypt.AES;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import java.lang.reflect.Field;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class,
                Object.class})
})
public class EncryptFieldInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement)invocation.getArgs()[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Object parameter = invocation.getArgs()[1];
        Class<?> clazz = parameter.getClass();
        String clazzName = clazz.getName();
        if (!clazz.getSuperclass().isInstance(Object.class)){

        }else{
            Field[] declaredFields = parameter.getClass().getDeclaredFields();
            for (Field field: declaredFields) {
                EncryptField annotation = field.getAnnotation(EncryptField.class);
                if(annotation!=null){
                    if(field.getGenericType() == String.class){
                        field.setAccessible(true);
                        String value = (String)field.get(parameter);
                        field.set(parameter,AES.encrypt(value));
                    }

                }
            }
        }
        return invocation.proceed();
    }
}
