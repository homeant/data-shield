package fun.vyse.cloud.data.shield;

import com.fasterxml.jackson.databind.ObjectMapper;
import fun.vyse.cloud.AbstractApplicationTest;
import fun.vyse.cloud.data.shield.domain.User;
import fun.vyse.cloud.data.shield.mapper.UserMapper;
import fun.vyse.cloud.data.shield.util.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

import java.util.Optional;


@Slf4j
@MapperScan("fun.vyse.cloud.data.shield.mapper")
public class DataTest extends AbstractApplicationTest {

    @Autowired
    private UserMapper userMapper;

    /**
     * RSA 加密模式会导致密文过长，不推荐使用
     */
    @Test
    public void test(){
        User user = new User();
        user.setUsername("tom");
        user.setPassword("p@ssw0rd1234567");
        userMapper.insert(user);
        Optional<User> optional = userMapper.selectOn(user.getId());
        optional.ifPresent(r->{
            log.debug("user:{}",r);
        });
    }

    @Test
    public void resInit() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        log.debug(objectMapper.writeValueAsString(RSAUtils.init(512)));
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config {

    }
}
