package com.github.homeant.data.shield.asserting;

import java.util.regex.Pattern;

/**
 * base64 断言
 * @author tianhui
 */
public class Base64Assert extends AbstractAssert {

    private static final String BASE64_PATTERN = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";

    @Override
    public boolean encrypt(String value, Object object) {
        return nonEmpty(value) && !isBase64String(value);
    }

    @Override
    public boolean decode(String value, Object object) {
        return nonEmpty(value) && isBase64String(value);
    }

    public boolean isBase64String(String value){
        Pattern pattern = Pattern.compile(BASE64_PATTERN);
        return pattern.matcher(value).matches();
    }
}
