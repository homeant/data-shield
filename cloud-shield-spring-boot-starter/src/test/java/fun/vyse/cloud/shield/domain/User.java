package fun.vyse.cloud.shield.domain;

import fun.vyse.cloud.shield.annotation.TableField;
import lombok.Data;

@Data
public class User {
    private Integer id;

    private String username;

    @TableField(encrypt = true,decode = true)
    private String password;
}
