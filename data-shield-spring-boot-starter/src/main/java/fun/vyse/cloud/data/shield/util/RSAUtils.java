package fun.vyse.cloud.data.shield.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RSAUtils {

    public static final String ALGORITHM = "RSA";

    private static final String PUBLIC_KEY = "publicKey";
    private static final String PRIVATE_KEY = "privateKey";

    public static Map<String,String> init(int keySize) throws Exception {
        Map<String,String> keyMap = new HashMap<>(2);
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGen.initialize(keySize);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();// 公钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();// 私钥
        keyMap.put(PUBLIC_KEY, Base64Utils.encrypt(publicKey.getEncoded()));
        keyMap.put(PRIVATE_KEY, Base64Utils.encrypt(privateKey.getEncoded()));
        return keyMap;
    }

    /**
     * 公钥加密
     * @param content
     * @param key
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(String content, String key) throws Exception {
        byte[] keyBytes = Base64Utils.decrypt(key);// 对公钥解密
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return Base64Utils.encrypt(cipher.doFinal(content.getBytes()));
    }

    /**
     * 用公钥解密
     * @param content
     * @param key
     * @return
     * @throws Exception
     */
    public static String decryptByPublicKey(String content, String key) throws Exception {
        byte[] keyBytes = Base64Utils.decrypt(key);// 对密钥解密
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);
        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return new String(cipher.doFinal(Base64Utils.decrypt(content)));
    }

    /**
     * 私钥加密
     * @param content
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(String content, String key) throws Exception {
        byte[] keyBytes = Base64Utils.decrypt(key);// 对密钥解密
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(content.getBytes());
    }

    /**
     * 私钥解密
     * @param content
     * @param key
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(String content, String key) throws Exception {
        byte[] keyBytes = Base64Utils.decrypt(key);// 对密钥解密
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(Base64Utils.decrypt(content)));
    }
}
