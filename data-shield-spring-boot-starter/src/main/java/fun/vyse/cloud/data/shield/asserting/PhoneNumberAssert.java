package fun.vyse.cloud.data.shield.asserting;

import java.util.regex.Pattern;

/**
 * 手机号断言
 */
public class PhoneNumberAssert implements IAssert {

    private static String PHONE_PATTERN = "^[1]\\d{10}$";

    @Override
    public boolean encrypt(String value, Object object) {
        if(value==null){
            return false;
        }
        Pattern pattern = Pattern.compile(PHONE_PATTERN);
        return pattern.matcher(value).matches();
    }

    @Override
    public boolean decode(String value, Object object) {
        if(value==null){
            return false;
        }
        Pattern pattern = Pattern.compile(PHONE_PATTERN);
        return !pattern.matcher(value).matches();
    }
}
