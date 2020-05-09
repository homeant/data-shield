package fun.vyse.cloud.data.shield.mybatis.interceptor;


import fun.vyse.cloud.data.shield.annotation.TableField;
import fun.vyse.cloud.data.shield.asserting.IAssert;
import fun.vyse.cloud.data.shield.process.IDataProcess;
import lombok.Data;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class,
                Object.class})
})
@Data
public class EncryptInterceptor implements Interceptor, ApplicationContextAware {

    private final IDataProcess dataProcess;

    private ApplicationContext applicationContext;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object parameter = invocation.getArgs()[1];
        Class<?> clazz = parameter.getClass();
        Type superType = clazz.getGenericSuperclass();
        if (superType.getClass().isInstance(Object.class)) {
            List<Field> fieldList = new ArrayList<>();
            ReflectionUtils.doWithFields(clazz, fieldList::add);
            for (Field field : fieldList) {
                TableField annotation = field.getAnnotation(TableField.class);
                if (annotation != null && annotation.encrypt()) {
                    if (field.getGenericType() == String.class) {
                        field.setAccessible(true);
                        IAssert instance = getInstance(annotation.assertion());
                        String value = (String) field.get(parameter);
                        if (instance.encrypt(value, parameter)) {
                            ReflectionUtils.setField(field, parameter, dataProcess.encrypt(value));
                        }
                    }
                }
            }
        }
        return invocation.proceed();
    }

    @Override
    public void setProperties(Properties properties) {

    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }


    private <T> T getInstance(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        try {
            T bean = applicationContext.getBean(clazz);
            return bean;
        } catch (NoSuchBeanDefinitionException e) {
            return clazz.newInstance();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
