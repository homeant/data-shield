package fun.vyse.cloud.shield.interceptor;

import fun.vyse.cloud.shield.annotation.TableField;
import fun.vyse.cloud.shield.encrypt.AES;
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
        TableField annotation = getEncryptResultAnnotation(mappedStatement);
        Object returnValue = invocation.proceed();
        if (annotation != null && returnValue != null) {
            // 对结果进行处理
            try {
                if (returnValue instanceof ArrayList<?>) {
                    List<?> list = (ArrayList<?>) returnValue;
                    for (int index = 0; index < list.size(); index++) {
                        Object returnItem = list.get(index);
                        if (returnItem instanceof String) {
                            List<String> stringList = (List<String>) list;
                            stringList.set(index, AES.encrypt((String) returnItem));
                        }
                    }
                }
            } catch (Exception e) {
                // ignore
            }


        }
        return returnValue;

    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * EncryptResult
     *
     * @param mappedStatement MappedStatement
     * @return EncryptResult
     */
    private TableField getEncryptResultAnnotation(MappedStatement mappedStatement) {
        TableField tableField = null;
        try {
            String id = mappedStatement.getId();
            String className = id.substring(0, id.lastIndexOf("."));
            String methodName = id.substring(id.lastIndexOf(".") + 1);
            final Method[] method = Class.forName(className).getMethods();
            for (Method me : method) {
                if (me.getName().equals(methodName) && me.isAnnotationPresent(TableField.class)) {
                    tableField = me.getAnnotation(TableField.class);
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return tableField;
    }
}
