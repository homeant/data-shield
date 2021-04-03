package com.github.homeant.data.shield.mybatis.interceptor;

import com.github.homeant.data.shield.annotation.TableField;
import com.github.homeant.data.shield.asserting.IAssert;
import com.github.homeant.data.shield.mybatis.domain.DefaultCursor;
import com.github.homeant.data.shield.process.IDataProcess;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

@Intercepts({
        @Signature(
                type = ResultSetHandler.class,
                method = "handleResultSets",
                args = {Statement.class}
        ),
        @Signature(
                type = ResultSetHandler.class,
                method = "handleCursorResultSets",
                args = {Statement.class}
        )
})
@Slf4j
public class DecodeInterceptor implements Interceptor, ApplicationContextAware {

    private final IDataProcess dataProcess;

    private ApplicationContext applicationContext;

    public DecodeInterceptor(IDataProcess dataProcess) {
        this.dataProcess = dataProcess;
    }
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object returnValue = invocation.proceed();
        // 对结果进行处理
        if (returnValue instanceof List<?>) {
            List<?> list = (List<?>) returnValue;
            for (Object returnItem : list) {
                if (returnItem != null) {
                    Class<?> clazz = returnItem.getClass();
                    List<Field> fieldList = new ArrayList<>();
                    ReflectionUtils.doWithFields(clazz, fieldList::add);
                    for (Field field : fieldList) {
                        setFieldValue(returnItem, field);
                    }
                }
            }
        } else if (returnValue instanceof Cursor) {
            return new DefaultCursor(((Cursor)returnValue)){
                @Override
                public Iterator iterator() {
                    Iterator iterator = ((Cursor) returnValue).iterator();
                    return new Iterator() {
                        @Override
                        public boolean hasNext() {
                            return iterator.hasNext();
                        }

                        @Override
                        public Object next() {
                            Object next = iterator.next();
                            Class<?> clazz = next.getClass();
                            List<Field> fieldList = new ArrayList<>();
                            ReflectionUtils.doWithFields(clazz, fieldList::add);
                            for (Field field : fieldList) {
                                try {
                                    setFieldValue(next, field);
                                } catch (Exception e) {
                                    throw new IllegalArgumentException(e);
                                }
                            }
                            return next;
                        }
                    };
                }
            };
        }
        return returnValue;
    }

    private void setFieldValue(Object source, Field field) throws IllegalAccessException, InstantiationException {
        Class<?> clazz = source.getClass();
        field.setAccessible(true);
        if (checkEncode(source, field)) {
            String value = (String) field.get(source);
            try {
                value = dataProcess.decode(value);
            } catch (Exception e) {
                log.error("{}.{} decode error,value:{}", clazz.getName(), field.getName(), value, e);
                throw new IllegalArgumentException(clazz.getName() + "." + field.getName() + " decode error,value: " + value, e);
            }
            ReflectionUtils.setField(field, source, value);
        }

    }

    private boolean checkEncode(Object source, Field field) throws InstantiationException, IllegalAccessException {
        TableField annotation = field.getAnnotation(TableField.class);
        boolean check = annotation != null && annotation.decode() && field.getGenericType() == String.class;
        if (!check) {
            return false;
        }
        String value = (String) field.get(source);
        Class<? extends IAssert>[] assertList = annotation.asserts();
        for (Class<? extends IAssert> aClass : assertList) {
            IAssert instance = getInstance(aClass);
            if (!instance.decode(value, source)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        log.debug("properties:{}",properties);
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
