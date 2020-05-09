package fun.vyse.cloud.data.shield.asserting;

import java.util.regex.Pattern;

public class BankCardNumberAssert implements IAssert{
    private static String PHONE_PATTERN = "^\\d{19}$";

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
