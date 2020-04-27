package fun.vyse.cloud.shield.domain;

import fun.vyse.cloud.shield.annotation.TableField;
import lombok.Data;

@Data
public class User {
    private Integer id;

    @TableField(encrypt = true,decode = true)
    private String username;

    private String password;
}
