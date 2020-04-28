package fun.vyse.cloud.shield;

import fun.vyse.cloud.shield.domain.User;
import fun.vyse.cloud.shield.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.testng.annotations.Test;

import java.util.Optional;


@Slf4j
@MapperScan("fun.vyse.cloud.shield.mapper")
public class AbstractDataTest extends AbstractDataShieldTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void test(){
        User user = new User();
        user.setUsername("tom");
        user.setPassword("p@ssw0rd");
        userMapper.insert(user);
        Optional<User> optional = userMapper.selectOn(user.getId());
        optional.ifPresent(r->{
            log.debug("user:{}",r);
        });
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config {

    }
}
