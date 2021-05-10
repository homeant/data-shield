package com.github.homeant.data.shield.dto;

import com.github.homeant.data.shield.annotation.Mapping;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private String username;

    private String password;

    @Mapping(lazy = true)
    private UserInfoDto userInfo;

    @Mapping(lazy = true)
    private List<BookDto> bookList;
}
