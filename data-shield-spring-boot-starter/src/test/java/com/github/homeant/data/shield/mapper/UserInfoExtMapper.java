package com.github.homeant.data.shield.mapper;

import com.github.homeant.data.shield.domain.UserInfo;
import com.github.homeant.data.shield.domain.UserInfoExt;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.FetchType;

public interface UserInfoExtMapper {
    @Select({
      "select id,user_id from t_user_info_ext where user_id = #{userId}"
    })
    UserInfoExt selectByUserId(Integer userId);
}
