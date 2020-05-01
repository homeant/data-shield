package fun.vyse.cloud.data.shield.mybatis.interceptor;

import fun.vyse.cloud.data.shield.annotation.TableField;
import fun.vyse.cloud.data.shield.process.IDataProcess;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Intercepts({
        @Signature(
                type = ResultSetHandler.class,
                method = "handleResultSets",
                args = {Statement.class}
        )
})
@Slf4j
@Data
public class DecodeInterceptor implements Interceptor {

    private final IDataProcess dataProcess;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取到返回结果
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
                                if (annotation != null && annotation.decode()) {
                                    if (field.getGenericType() == String.class) {
                                        field.setAccessible(true);
                                        String value = (String) field.get(returnItem);
                                        field.set(returnItem, dataProcess.decode(value));
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("decode error", e);
            }
        }
        return returnValue;

    }
}
