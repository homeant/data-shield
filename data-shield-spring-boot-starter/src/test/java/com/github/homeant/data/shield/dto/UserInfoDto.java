package com.github.homeant.data.shield.dto;

import com.github.homeant.data.shield.annotation.Mapping;
import lombok.Data;

import java.util.Date;

@Data
public class UserInfoDto {
    private Date birthday;

    @Mapping(value = "userInfoExt")
    private UserInfoExtDto userInfoExt1;
}
