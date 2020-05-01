package fun.vyse.cloud.data.shield.util;

import java.util.Base64;

public class Base64Utils {

    public static String encrypt(String content) {
        return Base64.getEncoder().encodeToString(content.getBytes());
    }

    public static String encrypt(byte[] content) {
        return Base64.getEncoder().encodeToString(content);
    }

    public static byte[] decrypt(String content){
        return Base64.getDecoder().decode(content);
    }
}
