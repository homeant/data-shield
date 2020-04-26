package fun.vyse.cloud.shield.config;

import fun.vyse.cloud.shield.domain.DataShieldProperties;
import fun.vyse.cloud.shield.mybatis.AESEncryptHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@Slf4j
@ConditionalOnProperty(prefix = "app.data.shield", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(DataShieldProperties.class)
public class DataShieldAutoConfiguration {

    private DataShieldAutoConfiguration() {
        log.debug("cloud shield init ...");
        log.debug("cloud shield init ....");
        log.debug("cloud shield init .....");
    }

    @Bean
    public TypeHandler aesEncryptHandler() {
        return new AESEncryptHandler();
    }
}
