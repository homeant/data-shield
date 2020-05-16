package fun.vyse.cloud.data.shield.domain;

import fun.vyse.cloud.data.shield.annotation.TableField;
import fun.vyse.cloud.data.shield.asserting.DefaultAssert;
import lombok.Data;

@Data
public class User {
    private Integer id;

    private String username;

    @TableField(encrypt = true,decode = true,asserts = {DefaultAssert.class})
    private String password;
}
