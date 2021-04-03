package com.github.homeant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

@Slf4j
@SpringBootTest(classes = {ApplicationConfig.class})
public class AbstractApplicationTest extends AbstractTestNGSpringContextTests {

}
