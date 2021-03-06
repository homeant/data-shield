package com.github.homeant.data.shield.config;

import com.github.homeant.data.shield.domain.DataShieldProperties;
import com.github.homeant.data.shield.mybatis.interceptor.EncryptUpdateInterceptor;
import com.github.homeant.data.shield.process.AESProcess;
import com.github.homeant.data.shield.process.DESProcess;
import com.github.homeant.data.shield.process.IDataProcess;
import com.github.homeant.data.shield.process.RSAProcess;
import com.github.homeant.data.shield.mybatis.interceptor.DecodeInterceptor;
import com.github.homeant.data.shield.mybatis.interceptor.EncryptInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;


@Slf4j
@EnableConfigurationProperties(DataShieldProperties.class)
@AutoConfigureBefore(MybatisAutoConfiguration.class)
@ConditionalOnProperty(prefix = DataShieldProperties.PREFIX, value = "enable", havingValue = "true")
public class DataShieldAutoConfiguration {

    private final DataShieldProperties dataShieldProperties;

    public DataShieldAutoConfiguration(DataShieldProperties dataShieldProperties) {
        this.dataShieldProperties = dataShieldProperties;
        this.dataShieldProperties.check();
    }

    @Bean
    @ConditionalOnProperty(prefix = DataShieldProperties.PREFIX, value = "strategy", havingValue = "des")
    public DESProcess desProcess() {
        return new DESProcess(dataShieldProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = DataShieldProperties.PREFIX, value = "strategy", havingValue = "aes")
    public AESProcess aesProcess() {
        return new AESProcess(dataShieldProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = DataShieldProperties.PREFIX, value = "strategy", havingValue = "rsa")
    public RSAProcess rsaProcess() {
        return new RSAProcess(dataShieldProperties);
    }

    /**
     * 加密
     *
     * @return
     */
    @Bean
    public Interceptor encryptInterceptor(IDataProcess dataProcess) {
        return new EncryptInterceptor(dataProcess);
    }

    @Bean
    public Interceptor encryptUpdateInterceptor(IDataProcess dataProcess){
        return new EncryptUpdateInterceptor(dataProcess);
    }

    /**
     * 解密
     *
     * @return
     */
    @Bean
    public Interceptor decodeInterceptor(IDataProcess dataProcess) {
        return new DecodeInterceptor(dataProcess);
    }
}
