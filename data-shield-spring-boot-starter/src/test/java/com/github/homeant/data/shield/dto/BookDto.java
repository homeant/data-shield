package com.github.homeant.data.shield.dto;

import com.github.homeant.data.shield.annotation.Mapping;
import lombok.Data;

@Data
public class BookDto {
    private Integer id;

    @Mapping("name")
    private String bookName;
}
