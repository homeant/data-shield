package com.github.homeant.data.shield.mapper;

import com.github.homeant.data.shield.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserMapper {

    @Insert({
            "insert into t_user ",
            "(username,password) ",
            "values (",
            "#{username},",
            "#{password})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Boolean insert(User user);

    @Select({
            "select id,username,password from t_user where id = #{id}"
    })
    Optional<User> selectOn(Integer id);

    @Select({
            "select id,username,password from t_user"
    })
    Cursor<User> selectList();
}
