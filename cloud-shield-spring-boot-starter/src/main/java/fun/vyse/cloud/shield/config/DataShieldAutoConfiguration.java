package fun.vyse.cloud.shield.config;

import fun.vyse.cloud.shield.domain.DataShieldProperties;
import fun.vyse.cloud.shield.interceptor.EncryptInterceptor;
import fun.vyse.cloud.shield.interceptor.DecodeInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Optional;
import java.util.Properties;

@Slf4j
@EnableConfigurationProperties(DataShieldProperties.class)
@ConditionalOnProperty(prefix = "app.data.shield", value = "enable",havingValue = "true")
public class DataShieldAutoConfiguration {

    private final DataShieldProperties dataShieldProperties;

    public DataShieldAutoConfiguration(DataShieldProperties dataShieldProperties) {
        this.dataShieldProperties = dataShieldProperties;
        this.dataShieldProperties.check();
    }

    /**
     * 加密
     * @return
     */
    @Bean
    public Interceptor encryptInterceptor(){
        EncryptInterceptor encryptInterceptor = new EncryptInterceptor();
        encryptInterceptor.setProperties(getProperties());
        return encryptInterceptor;
    }


    /**
     * 解密
     * @return
     */
    @Bean
    public Interceptor decodeInterceptor() {
        DecodeInterceptor interceptor = new DecodeInterceptor();
        interceptor.setProperties(getProperties());
        return interceptor;
    }

    private Properties getProperties(){
        Properties properties = new Properties();
        properties.put("strategy",dataShieldProperties.getStrategy());
        Optional.ofNullable(dataShieldProperties.getKey()).ifPresent(r->{
            properties.put("key", r);
        });
        Optional.ofNullable(dataShieldProperties.getPublicKey()).ifPresent(r->{
            properties.put("publicKey", r);
        });
        Optional.ofNullable(dataShieldProperties.getPrivateKey()).ifPresent(r->{
            properties.put("privateKey", r);
        });
        return properties;
    }
}
