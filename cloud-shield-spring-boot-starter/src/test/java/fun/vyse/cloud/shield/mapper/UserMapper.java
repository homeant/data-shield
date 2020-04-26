package fun.vyse.cloud.shield.mapper;

import fun.vyse.cloud.shield.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

public interface UserMapper {

    @Insert({
            "insert into t_user (username,password) values (#{username},#{password})"
    })
    @Options(useGeneratedKeys = true,keyProperty = "id")
    Boolean insert(User user);
}
