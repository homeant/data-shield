package com.github.homeant.data.shield.mapper;

import com.github.homeant.data.shield.domain.UserInfo;
import org.apache.ibatis.annotations.Select;

public interface UserInfoMapper {
    @Select({
      "select id,birthday from t_user_info where user_id = #{userId}"
    })
    UserInfo selectByUserId(Integer userId);
}
