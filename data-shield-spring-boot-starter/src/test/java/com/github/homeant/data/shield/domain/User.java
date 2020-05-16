package com.github.homeant.data.shield.domain;

import com.github.homeant.data.shield.asserting.DefaultAssert;
import com.github.homeant.data.shield.annotation.TableField;
import lombok.Data;

@Data
public class User {
    private Integer id;

    private String username;

    @TableField(encrypt = true,decode = true,asserts = {DefaultAssert.class})
    private String password;
}
