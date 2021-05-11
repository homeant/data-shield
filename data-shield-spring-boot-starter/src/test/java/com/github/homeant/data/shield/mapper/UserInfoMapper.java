package com.github.homeant.data.shield.mapper;

import com.github.homeant.data.shield.domain.UserInfo;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.FetchType;

public interface UserInfoMapper {
    @Select({
            "select id,user_id,birthday from t_user_info where user_id = #{userId}"
    })
    @Results({
            @Result(column = "user_id", property = "userInfoExt",
                    one = @One(select = "com.github.homeant.data.shield.mapper.UserInfoExtMapper.selectByUserId", fetchType = FetchType.LAZY))
    })
    UserInfo selectByUserId(Integer userId);
}
