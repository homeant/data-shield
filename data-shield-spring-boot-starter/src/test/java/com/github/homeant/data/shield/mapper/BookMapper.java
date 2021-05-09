package com.github.homeant.data.shield.mapper;

import com.github.homeant.data.shield.domain.Book;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BookMapper {
    @Select({
      "select id,name from t_book where user_id = #{userId}"
    })
    List<Book> selectByUserId(Integer userId);
}
