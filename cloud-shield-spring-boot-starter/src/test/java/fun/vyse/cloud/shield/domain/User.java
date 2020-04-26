package fun.vyse.cloud.shield.domain;

import fun.vyse.cloud.shield.interceptor.EncryptField;
import lombok.Data;

@Data
public class User {
    private Integer id;

    @EncryptField
    private String username;

    private String password;
}
