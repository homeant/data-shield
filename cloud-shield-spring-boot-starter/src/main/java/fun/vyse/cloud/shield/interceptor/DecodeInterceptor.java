package fun.vyse.cloud.shield.interceptor;

import fun.vyse.cloud.shield.annotation.TableField;
import fun.vyse.cloud.shield.encrypt.AES;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Intercepts({
        @Signature(
                type = ResultSetHandler.class,
                method = "handleResultSets",
                args = {Statement.class}
        )
})
@Slf4j
public class DecodeInterceptor implements Interceptor {

    private Properties properties;

    private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    private static final ReflectorFactory REFLECTOR_FACTORY = new DefaultReflectorFactory();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取到返回结果
        ResultSetHandler resultSetHandler = (ResultSetHandler) invocation.getTarget();
        MetaObject metaResultSetHandler = MetaObject.forObject(resultSetHandler, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, REFLECTOR_FACTORY);
        MappedStatement mappedStatement = (MappedStatement) metaResultSetHandler.getValue("mappedStatement");
        Object returnValue = invocation.proceed();
        if (returnValue != null) {
            // 对结果进行处理
            try {
                if (returnValue instanceof ArrayList<?>) {
                    List<?> list = (ArrayList<?>) returnValue;
                    for (int index = 0; index < list.size(); index++) {
                        Object returnItem = list.get(index);
                        Class<?> clazz = returnItem.getClass();
                        if (clazz.getSuperclass().isInstance(Object.class)) {
                            Field[] declaredFields = clazz.getDeclaredFields();
                            for (Field field : declaredFields) {
                                TableField annotation = field.getAnnotation(TableField.class);
                                if(annotation!=null && annotation.decode()){
                                    if (field.getGenericType() == String.class) {
                                        field.setAccessible(true);
                                        String value = (String) field.get(returnItem);
                                        field.set(returnItem, AES.decode(value,getPrivateKey()));
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("decode error",e);
            }


        }
        return returnValue;

    }

    private String getPrivateKey(){
        return properties.getProperty("privateKey");
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
