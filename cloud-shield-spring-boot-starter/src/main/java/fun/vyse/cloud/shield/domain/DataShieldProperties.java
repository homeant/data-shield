package fun.vyse.cloud.shield.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("app.data.shield")
public class DataShieldProperties {
    private Boolean enable;

    private Strategy strategy = Strategy.AES;

    private String key;

    private String publicKey;

    private String privateKey;

    public enum Strategy{
        AES;
    }
}
