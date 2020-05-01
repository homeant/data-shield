package fun.vyse.cloud.data.shield.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class DESUtils {
    private static String CHARSET = "utf-8";
    // 偏移量
    private static int OFFSET = 16;
    private static String TRANSFORMATION = "DES/CBC/PKCS5Padding";
    private static String ALGORITHM = "DES";


    public static String encrypt(String content, String password) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(password.getBytes(), ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(password.getBytes(), 0, OFFSET);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);// 初始化
        byte[] byteContent = content.getBytes(CHARSET);
        byte[] result = cipher.doFinal(byteContent);
        return Base64.getEncoder().encodeToString(result);
    }

    public static String decode(String content, String password) throws Exception{
        SecretKeySpec keySpec = new SecretKeySpec(password.getBytes(), ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(password.getBytes(), 0, OFFSET);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);// 初始化
        byte[] byteContent = content.getBytes(CHARSET);
        byte[] result = cipher.doFinal(Base64.getDecoder().decode(byteContent));
        return new String(result);
    }
}
