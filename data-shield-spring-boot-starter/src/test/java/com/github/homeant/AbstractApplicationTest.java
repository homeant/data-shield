package com.github.homeant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

@Slf4j
@SpringBootTest
public class AbstractApplicationTest extends AbstractTestNGSpringContextTests {
    @Configuration
    @EnableAutoConfiguration
    public static class Config {

    }
}
