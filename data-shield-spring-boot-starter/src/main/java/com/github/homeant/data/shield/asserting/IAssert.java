package com.github.homeant.data.shield.asserting;

import java.util.Objects;

/**
 * 断言接口
 *
 * @author tianhui
 */
public interface IAssert {
    default boolean encrypt(String value, Object object) {
        if (value == null || value == "") {
            return false;
        }
        return true;
    }

    default boolean decode(String value, Object object) {
        if (value == null || value == "") {
            return false;
        }
        return true;
    }
}
