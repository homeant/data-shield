package fun.vyse.cloud.shield.encrypt;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class AES {


    private static String CHARSET = "utf-8";
    // 偏移量
    private static int OFFSET = 16;
    private static String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static String ALGORITHM = "AES";

    public static byte[] initKey() throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
        kg.init(128);
        SecretKey secretKey = kg.generateKey();
        return secretKey.getEncoded();
    }

    public static String encrypt(String content, String publicKey) throws Exception {
        Key key = new SecretKeySpec(publicKey.getBytes(), ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(publicKey.getBytes(), 0, OFFSET);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);// 初始化
        byte[] byteContent = content.getBytes(CHARSET);
        byte[] result = cipher.doFinal(byteContent);
        return Base64.getEncoder().encodeToString(result);
    }

    public static String decode(String content, String privateKey) throws Exception{
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        Key key = new SecretKeySpec(privateKey.getBytes(), ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(privateKey.getBytes(), 0, OFFSET);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);// 初始化
        byte[] byteContent = content.getBytes(CHARSET);
        byte[] result = cipher.doFinal(byteContent);
        return new String(Base64.getDecoder().decode(result));
    }

    public static void main(String[] args) throws Exception {
        System.out.println(AES.initKey());
    }
}
