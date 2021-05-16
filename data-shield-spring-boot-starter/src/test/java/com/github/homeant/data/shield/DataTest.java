package com.github.homeant.data.shield;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.homeant.AbstractApplicationTest;
import com.github.homeant.data.shield.domain.User;
import com.github.homeant.data.shield.dto.UserDto;
import com.github.homeant.data.shield.helper.DataShieldHelper;
import com.github.homeant.data.shield.mapper.UserMapper;
import com.github.homeant.data.shield.mapper.orika.OrikaBeanMapper;
import com.github.homeant.data.shield.proxy.cglib.CglibProxyFactory;
import com.github.homeant.data.shield.util.RSAUtils;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;


@Slf4j
@MapperScan("com.github.homeant.data.shield.mapper")
public class DataTest extends AbstractApplicationTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

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
    public void queryOneTest() {
        User user = new User();
        user.setId(74);
        Optional<User> optional = userMapper.selectOn(user.getId());
        log.info("user:{}", optional);
    }

    @Test
    public void pageTest() {
        PageInfo<User> pageInfo = PageMethod.startPage(1, 10).doSelectPageInfo(() -> userMapper.select());
        pageInfo.getList().forEach(user -> {
            log.info("user:{}", user);
        });
    }

    @Test
    public void queryTest() {
        List<User> list = userMapper.select();
        list.forEach(user -> {
            UserDto userDto = mapperFactory.getMapperFacade().map(user, UserDto.class);
            log.info("user:{}", userDto);
        });

    }

    @Test
    public void cursorTest() throws IOException {
        try (SqlSession sqlSession = sqlSessionFactory.openSession();
             Cursor<User> userCursor = sqlSession.getMapper(UserMapper.class).selectList()) {
            Iterator<User> iterator = userCursor.iterator();
            while (iterator.hasNext()) {
                log.info("user:{}", iterator.next());
            }
        }

    }

    @Test
    public void resInit() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        log.debug(objectMapper.writeValueAsString(RSAUtils.init(512)));
    }

    @Test
    public void mapperTest() {
        List<User> list = userMapper.select();
        list.forEach(user -> {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start("init");
            OrikaBeanMapper orikaBeanMapper = new OrikaBeanMapper(new CglibProxyFactory());
            UserDto userDto = orikaBeanMapper.map(user, UserDto.class);
            stopWatch.stop();
            stopWatch.start("userInfo");
            log.info("getUserInfo() 会去查询数据 | {}", userDto.getUserInfo());
            stopWatch.stop();
            log.info("二次调用 getUserInfo(),不进行lazy load | {}", userDto.getUserInfo());
            log.info("getBookList(),查询book数据 | {}", userDto.getBookList1());
            userDto.setBookList1(new ArrayList<>());
            log.info("setBookList(emptyList),会删除lazy标记 | {}", userDto.getBookList1());
            log.info("toString | {}", userDto);
            try {
                log.info("json打印对象,看是否还会触发lazy | {}", new ObjectMapper().writeValueAsString(userDto));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            log.info("watch:{}", stopWatch);
        });

    }

    @Test
    public void mapperListTest(){
        List<User> list = userMapper.select();
        OrikaBeanMapper orikaBeanMapper = new OrikaBeanMapper(new CglibProxyFactory());
        List<UserDto> userDtoList = orikaBeanMapper.mapList(list, UserDto.class);
        userDtoList.forEach(userDto -> {
            //log.info("userInfo:{}",userDto.getUserInfo());
        });
    }
}
