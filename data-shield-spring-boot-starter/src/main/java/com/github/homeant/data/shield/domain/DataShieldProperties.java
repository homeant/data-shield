package com.github.homeant.data.shield.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

@Data
@ConfigurationProperties(DataShieldProperties.PREFIX)
public class DataShieldProperties {

    public static final String PREFIX = "app.data.shield";

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
        if(strategy == Strategy.AES || strategy == Strategy.DES){
            Assert.notNull(key,"The properties key for "+strategy+" strategy can't be empty");
            int length = key.length();
            if(!(length==16 || length==24 || length==32)){
                throw new IllegalArgumentException("The properties for "+strategy+" strategy must be (16/24/32) bits");
            }
        }else if(strategy == Strategy.RSA){
            Assert.notNull(publicKey,"The properties publicKey for "+strategy+" strategy can't be empty");
            Assert.notNull(privateKey,"The properties privateKey for "+strategy+" strategy can't be empty");
        }
    }
}
