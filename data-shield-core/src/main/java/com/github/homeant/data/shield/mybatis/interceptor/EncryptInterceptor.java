package com.github.homeant.data.shield.mybatis.interceptor;


import com.github.homeant.data.shield.annotation.TableField;
import com.github.homeant.data.shield.asserting.IAssert;
import com.github.homeant.data.shield.process.IDataProcess;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;

@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "queryCursor", args = {MappedStatement.class, Object.class, RowBounds.class})
})
@Slf4j
public class EncryptInterceptor implements Interceptor, ApplicationContextAware {

    private final IDataProcess dataProcess;

    private ApplicationContext applicationContext;

    private final Gson gson;


    public EncryptInterceptor(IDataProcess dataProcess) {
        this.dataProcess = dataProcess;
        gson = new Gson();
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        Executor executor = (Executor) invocation.getTarget();
        MappedStatement ms = (MappedStatement) args[0];
        Object source = args[1];
        if (source != null) {
            Class<?> clazz = ms.getParameterMap().getType();
            source = deepClone(source, clazz);
            args[1] = source;
            List<Field> fieldList = new ArrayList<>();
            ReflectionUtils.doWithFields(clazz, fieldList::add);
            for (Field field : fieldList) {
                setFieldValue(clazz, source, field);
            }
        }
        RowBounds rowBounds = (RowBounds) args[2];
        if (args.length == 3) {
            return executor.queryCursor(ms, source, rowBounds);
        }
        CacheKey cacheKey;
        BoundSql boundSql;
        ResultHandler<?> resultHandler = (ResultHandler<?>) args[3];
        if (args.length == 4) {
            //4 个参数时
            boundSql = ms.getBoundSql(source);
            cacheKey = executor.createCacheKey(ms, source, rowBounds, boundSql);
            return executor.query(ms, source, rowBounds, resultHandler, cacheKey, boundSql);
        } else {
            //6 个参数时
            cacheKey = (CacheKey) args[4];
            boundSql = (BoundSql) args[5];
        }
        return executor.query(ms, source, rowBounds, resultHandler, cacheKey, boundSql);
    }

    @SuppressWarnings("all")
    private Object deepClone(Object source, Class<?> clazz) {
        if (source instanceof Map) {
            Map<?, ?> newSource = new HashMap<>(((Map<?, ?>) source).size());
            newSource.putAll((Map) source);
            return newSource;
        } else {
            return gson.fromJson(gson.toJson(source), clazz);
        }
    }

    private boolean checkEncrypt(Object source, Field field) throws InstantiationException, IllegalAccessException {
        TableField annotation = field.getAnnotation(TableField.class);
        boolean check = annotation != null && annotation.encrypt() && field.getGenericType() == String.class;
        if (!check) {
            return false;
        }
        String value = (String) getFieldValue(source, field);
        for (Class<? extends IAssert> aClass : annotation.asserts()) {
            IAssert instance = getInstance(aClass);
            if (!instance.encrypt(value, source)) {
                return false;
            }
        }

        return true;
    }

    private Object getFieldValue(Object source, Field field) throws IllegalAccessException {
        if (source instanceof Map) {
            return ((Map<?, ?>) source).get(field.getName());
        } else {
            return field.get(source);
        }
    }

    @SuppressWarnings("all")
    private void setFieldValue(Class<?> clazz, Object target, Field field) throws IllegalAccessException, InstantiationException {
        field.setAccessible(true);
        if (checkEncrypt(target, field)) {
            String value = (String) getFieldValue(target, field);
            try {
                value = dataProcess.encrypt(value);
            } catch (Exception e) {
                log.error("{}.{} encrypt error,value:{}", clazz.getName(), field.getName(), value, e);
                throw new IllegalArgumentException(clazz.getName() + "." + field.getName() + " encrypt error,value: " + value, e);
            }
            if (target instanceof Map) {
                ((Map<String, Object>) target).put(field.getName(), value);
            } else {
                ReflectionUtils.setField(field, target, value);
            }
        }
    }

    @Override
    public void setProperties(Properties properties) {
        log.debug("properties:{}", properties);
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }


    private <T> T getInstance(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        try {
            return applicationContext.getBean(clazz);
        } catch (NoSuchBeanDefinitionException e) {
            return clazz.newInstance();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
