package com.github.homeant.data.shield.mybatis.interceptor;


import com.github.homeant.data.shield.asserting.IAssert;
import com.github.homeant.data.shield.dataMasking.DataMasking;
import com.github.homeant.data.shield.process.IDataProcess;
import com.github.homeant.data.shield.annotation.TableField;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
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
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class,Object.class})
})
@Data
@Slf4j
public class EncryptInterceptor implements Interceptor, ApplicationContextAware {

    private final IDataProcess dataProcess;

    private ApplicationContext applicationContext;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];
        if(parameter==null){
            return invocation.proceed();
        }
        Class<?> clazz = parameter.getClass();
        Type superType = clazz.getGenericSuperclass();
        if (superType.getClass().isInstance(Object.class)) {
            List<Field> fieldList = new ArrayList<>();
            ReflectionUtils.doWithFields(clazz, fieldList::add);
            for (Field field : fieldList) {
                TableField annotation = field.getAnnotation(TableField.class);
                if (annotation != null) {
                    if (annotation.encrypt()) {
                        if (field.getGenericType() == String.class) {
                            field.setAccessible(true);
                            String value = (String) field.get(parameter);
                            Class<? extends IAssert>[] asserts = annotation.asserts();
                            boolean result = true;
                            for (int i = 0; i < asserts.length; i++) {
                                IAssert instance = getInstance(asserts[i]);
                                if (!instance.encrypt(value, parameter)) {
                                    result = false;
                                    break;
                                }
                            }
                            if (result) {
                                try {
                                    String encrypt = dataProcess.encrypt(value);
                                    ReflectionUtils.setField(field, parameter, encrypt);
                                } catch (Exception e) {
                                    log.error("encrypt {}.{} value:{} fail", value, clazz.getName(), field.getName());
                                    throw e;
                                }
                            }
                        }
                    }
                }
            }
        }
        if(ms.getSqlCommandType()== SqlCommandType.SELECT){
            RowBounds rowBounds = (RowBounds) args[2];
            ResultHandler resultHandler = (ResultHandler) args[3];
            Executor executor = (Executor) invocation.getTarget();
            CacheKey cacheKey;
            BoundSql boundSql;
            //由于逻辑关系，只会进入一次
            if(args.length == 4){
                //4 个参数时
                boundSql = ms.getBoundSql(parameter);
                cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
            } else {
                //6 个参数时
                cacheKey = (CacheKey) args[4];
                boundSql = (BoundSql) args[5];
            }
            //注：下面的方法可以根据自己的逻辑调用多次，在分页插件中，count 和 page 各调用了一次
            return executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
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
