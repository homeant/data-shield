package fun.vyse.cloud.data.shield.mapper;

import fun.vyse.cloud.data.shield.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

public interface UserMapper {

    @Insert({
            "insert into t_user ",
            "(username,password) ",
            "values (",
            "#{username},",
            "#{password})"
    })
    @Options(useGeneratedKeys = true,keyProperty = "id")
    Boolean insert(User user);

    @Select({
            "select id,username,password from t_user where id = #{id}"
    })
    Optional<User> selectOn(Integer id);
}
