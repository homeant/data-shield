package com.github.homeant.data.shield.util;


import org.apache.commons.lang3.StringUtils;

public class DataMaskingUtils {
    /**
     * 只显示第一个汉字，其他隐藏为2个星号<例子：李**>
     *
     * @param fullName
     * @param index    1 为第index位开始脱敏
     * @return
     */
    public static String left(String fullName, int index) {
        return left(fullName, index, "*");
    }

    public static String left(String fullName, int index, String symbol) {
        if (StringUtils.isBlank(fullName)) {
            return "";
        }
        String name = StringUtils.left(fullName, index);
        return StringUtils.rightPad(name, StringUtils.length(fullName), symbol);
    }

    /**
     * 110****58，前面保留3位明文，后面保留2位明文
     *
     * @param value
     * @param index 3
     * @param end   2
     * @return
     */
    public static String around(String value, int index, int end, String symbol) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        String leftValue = StringUtils.left(value, index);
        String rightValue = StringUtils.right(value, end);
        return leftValue.concat(StringUtils.leftPad(rightValue, StringUtils.length(value) - index, symbol));
    }

    public static String around(String value, int index, int end) {
        return around(value, index, end, "*");
    }


    /**
     * 后四位，其他隐藏<例子：****1234>
     *
     * @param num
     * @return
     */
    public static String right(String num, int end) {
        return right(num, end, "*");
    }

    public static String right(String num, int end, String symbol) {
        if (StringUtils.isBlank(num)) {
            return "";
        }
        return StringUtils.leftPad(StringUtils.right(num, end), StringUtils.length(num), symbol);
    }
}
