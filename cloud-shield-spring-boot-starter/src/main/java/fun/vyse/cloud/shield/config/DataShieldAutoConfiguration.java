package fun.vyse.cloud.shield.config;

import fun.vyse.cloud.shield.domain.DataShieldProperties;
import fun.vyse.cloud.shield.interceptor.EncryptInterceptor;
import fun.vyse.cloud.shield.interceptor.DecodeInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@Slf4j
@EnableConfigurationProperties(DataShieldProperties.class)
@ConditionalOnProperty(prefix = "app.data.shield", value = "enable",havingValue = "true")
public class DataShieldAutoConfiguration {

    private final DataShieldProperties dataShieldProperties;

    public DataShieldAutoConfiguration(DataShieldProperties dataShieldProperties) {
        this.dataShieldProperties = dataShieldProperties;
    }

    @Bean
    public Interceptor encryptFieldInterceptor(){
        return new EncryptInterceptor();
    }

    @Bean
    public Interceptor encryptResultInterceptor() {
        DecodeInterceptor interceptor = new DecodeInterceptor();
        return interceptor;
    }
}
