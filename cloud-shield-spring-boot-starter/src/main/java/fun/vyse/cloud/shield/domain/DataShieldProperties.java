package fun.vyse.cloud.shield.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

@Data
@ConfigurationProperties("app.data.shield")
public class DataShieldProperties {
    private Boolean enable;

    private Strategy strategy = Strategy.AES;

    /**
     * AES 模式 长度必须为16、24、32位，即128bit、192bit、256bit
     */
    private String key;

    private String publicKey;

    private String privateKey;

    public enum Strategy{
        AES,
        DES,
        RSA;
    }

    public void check(){
        if(strategy == Strategy.AES){
            Assert.notNull(key,"The password for AES strategy can't be empty");
            int length = key.length();
            if(!(length==16 || length==24 || length==32)){
                throw new IllegalArgumentException("The password for AES strategy must be (16/24/32) bits");
            }
        }
    }
}
