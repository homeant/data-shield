package com.github.homeant.data.shield;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.homeant.AbstractApplicationTest;
import com.github.homeant.data.shield.domain.User;
import com.github.homeant.data.shield.helper.DataShieldHelper;
import com.github.homeant.data.shield.mapper.UserMapper;
import com.github.homeant.data.shield.util.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;


@Slf4j
@MapperScan("com.github.homeant.data.shield.mapper")
public class DataTest extends AbstractApplicationTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    /**
     * RSA 加密模式会导致密文过长，不推荐使用
     */
    @Test
    public void test() {
        User user = new User();
        user.setUsername("tom");
        user.setPassword("p@ssw0rd1234567");
        userMapper.insert(user);
        DataShieldHelper.dataMasking();
        Optional<User> optional = userMapper.selectOn(user.getId());
        DataShieldHelper.clearDataMasking();
        optional.ifPresent(r -> {
            log.debug("user:{}", r);
        });
    }

    @Test
    public void cursorTest() throws IOException {
        try (SqlSession sqlSession = sqlSessionFactory.openSession();
             Cursor<User> userCursor = sqlSession.getMapper(UserMapper.class).selectList()) {
            Iterator<User> iterator = userCursor.iterator();
            while (iterator.hasNext()){
                log.info("user:{}",iterator.next());
            }
        }

    }

    @Test
    public void resInit() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        log.debug(objectMapper.writeValueAsString(RSAUtils.init(512)));
    }
}
