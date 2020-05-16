package fun.vyse.cloud.data.shield.service.impl;


import fun.vyse.cloud.data.shield.annotation.TableField;
import fun.vyse.cloud.data.shield.asserting.IAssert;
import fun.vyse.cloud.data.shield.process.IDataProcess;
import fun.vyse.cloud.data.shield.service.DataShieldService;
import fun.vyse.cloud.data.shield.util.ClassUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tianhui
 */
@RequiredArgsConstructor
public class DataShieldServiceImpl implements DataShieldService, ApplicationContextAware {

    private final IDataProcess dataProcess;

    private ApplicationContext applicationContext;

    /**
     * 加密
     *
     * @param value
     * @return
     */
    @Override
    public <T> T encrypt(T value) throws Exception {
        return encrypt(value, false);
    }

    /**
     * 递归加密
     *
     * @param value
     * @param doWith
     * @return
     * @throws Exception
     */
    @Override
    public <T> T encrypt(T value, boolean doWith) throws Exception {
        if (value != null) {
            try {
                Class<?> clazz = value.getClass();
                Type superType = clazz.getGenericSuperclass();
                if (superType.getClass().isInstance(Object.class)) {
                    List<Field> fieldList = new ArrayList<>();
                    ReflectionUtils.doWithFields(clazz, fieldList::add);
                    for (Field field : fieldList) {
                        field.setAccessible(true);
                        Type type = field.getGenericType();
                        TableField annotation = field.getAnnotation(TableField.class);
                        if (annotation != null && annotation.decode()) {
                            if (type == String.class) {
                                String strValue = (String) ReflectionUtils.getField(field, value);
                                Class<? extends IAssert>[] asserts = annotation.asserts();
                                boolean result = true;
                                for (int i = 0; i < asserts.length; i++) {
                                    IAssert instance = getInstance(asserts[i]);
                                    if (!instance.encrypt(strValue, value)) {
                                        result = false;
                                        break;
                                    }
                                }
                                if (result) {
                                    ReflectionUtils.setField(field, value, this.encrypt(strValue));
                                }
                            }
                        }
                        Class<? extends Type> superClass = type.getClass().getGenericSuperclass().getClass();
                        if (doWith && superClass.isInstance(Object.class) && !ClassUtils.isPrimitive(superClass)) {
                            ReflectionUtils.setField(field, value, this.encrypt(ReflectionUtils.getField(field, value), true));
                        }
                    }
                }
            } catch (Exception e) {
                throw e;
            }
        }
        return value;
    }

    /**
     * 单一字段加密
     *
     * @param value
     * @return
     */
    @Override
    public String encrypt(String value) throws Exception {
        if (value == null) {
            return null;
        }
        return dataProcess.encrypt(value);
    }

    /**
     * 解密
     *
     * @param value
     * @return
     */
    @Override
    public <T> T decode(T value) throws Exception {
        return this.decode(value, false);
    }

    /**
     * 递归解密
     *
     * @param value
     * @param doWith
     * @return
     */
    @Override
    public <T> T decode(T value, boolean doWith) throws Exception {
        if (value != null) {
            try {
                Class<?> clazz = value.getClass();
                Type superType = clazz.getGenericSuperclass();
                if (superType.getClass().isInstance(Object.class)) {
                    List<Field> fieldList = new ArrayList<>();
                    ReflectionUtils.doWithFields(clazz, fieldList::add);
                    for (Field field : fieldList) {
                        field.setAccessible(true);
                        Type type = field.getGenericType();
                        TableField annotation = field.getAnnotation(TableField.class);
                        if (annotation != null && annotation.decode()) {
                            if (type == String.class) {
                                String strValue = (String) ReflectionUtils.getField(field, value);
                                Class<? extends IAssert>[] asserts = annotation.asserts();
                                boolean result = true;
                                for (int i = 0; i < asserts.length; i++) {
                                    IAssert instance = getInstance(asserts[i]);
                                    if (!instance.decode(strValue, value)) {
                                        result = false;
                                        break;
                                    }
                                }
                                if (result) {
                                    ReflectionUtils.setField(field, value, this.decode(strValue));
                                }
                            }
                        }
                        Class<? extends Type> superClass = type.getClass().getGenericSuperclass().getClass();
                        if (doWith && superClass.isInstance(Object.class) && !ClassUtils.isPrimitive(superClass)) {
                            ReflectionUtils.setField(field, value, this.decode(ReflectionUtils.getField(field, value), true));
                        }
                    }
                }
            } catch (Exception e) {
                throw e;
            }
        }
        return value;
    }

    /**
     * 单一字段解密
     *
     * @param value
     * @return
     */
    @Override
    public String decode(String value) throws Exception {
        if (value == null) {
            return null;
        }
        return dataProcess.decode(value);
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
