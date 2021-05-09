package com.github.homeant.data.shield.mapper;

import com.github.homeant.data.shield.domain.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.mapping.FetchType;
import org.springframework.stereotype.Repository;

import java.util.List;
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
            "select id,username,password from t_user "
    })
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "username", property = "username"),
            @Result(column = "password", property = "password"),
            @Result(column = "id", property = "bookList", many = @Many(
                    fetchType = FetchType.LAZY, select = "com.github.homeant.data.shield.mapper.BookMapper.selectByUserId"
            )),
            @Result(column = "id", property = "userInfo", one = @One(
                    fetchType = FetchType.LAZY, select = "com.github.homeant.data.shield.mapper.UserInfoMapper.selectByUserId"
            ))
    })
    List<User> select();

    @Select({
            "select id,username,password from t_user"
    })
    Cursor<User> selectList();


}
