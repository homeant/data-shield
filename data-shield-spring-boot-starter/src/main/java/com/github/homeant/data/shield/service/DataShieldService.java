package com.github.homeant.data.shield.service;

/**
 * 数据处理业务API
 */
public interface DataShieldService {
    /**
     * 加密
     *
     * @param value
     * @param <T>
     * @return
     */
    <T> T encrypt(T value) throws Exception;

    /**
     * 递归加密
     * @param value
     * @param doWith
     * @param <T>
     * @return
     * @throws Exception
     */
    <T> T encrypt(T value, boolean doWith) throws Exception;


    /**
     * 单一字段加密
     *
     * @param value
     * @return
     */
    String encrypt(String value) throws Exception;

    /**
     * 解密
     *
     * @param value
     * @param <T>
     * @return
     */
    <T> T decode(T value) throws Exception;

    /**
     * 递归解密
     * @param value
     * @param doWith
     * @param <T>
     * @return
     * @throws Exception
     */
    <T> T decode(T value, boolean doWith) throws Exception;

    /**
     * 单一字段解密
     *
     * @param value
     * @return
     */
    String decode(String value) throws Exception;
}
