# data-shield

> 针对mybatis对业务敏感数据进行加密，为企业数据保驾护航


```bash
2021-04-03 16:23:16.604 DEBUG 3020 --- [           main] c.g.h.d.shield.mapper.UserMapper.insert  : ==>  Preparing: insert into t_user (username,password) values ( ?, ?) 
2021-04-03 16:23:16.629 DEBUG 3020 --- [           main] c.g.h.d.shield.mapper.UserMapper.insert  : ==> Parameters: tom(String), L0wfhbKDAELRnj03GtjKoQ==(String)
2021-04-03 16:23:16.651 DEBUG 3020 --- [           main] c.g.h.d.shield.mapper.UserMapper.insert  : <==    Updates: 1
2021-04-03 16:23:16.675 DEBUG 3020 --- [           main] c.g.h.d.s.mapper.UserMapper.selectOn     : ==>  Preparing: select id,username,password from t_user where id = ? 
2021-04-03 16:23:16.675 DEBUG 3020 --- [           main] c.g.h.d.s.mapper.UserMapper.selectOn     : ==> Parameters: 75(Integer)
2021-04-03 16:23:23.313 DEBUG 3020 --- [           main] c.g.h.d.s.mapper.UserMapper.selectOn     : <==      Total: 1
2021-04-03 16:23:26.166 DEBUG 3020 --- [           main] com.github.homeant.data.shield.DataTest  : user:User(id=75, username=tom, password=p@ssw0rd1234567)
```

```bash
mysql> select * from t_user;
+----+----------+--------------------------+
| id | username | password                 |
+----+----------+--------------------------+
| 74 | tom      | L0wfhbKDAELRnj03GtjKoQ== |
| 75 | tom      | L0wfhbKDAELRnj03GtjKoQ== |
+----+----------+--------------------------+
2 rows in set (0.03 sec)
```

## pom.xml配置


```xml
<dependency>
   <groupId>com.github.homeant</groupId>
   <artifactId>data-shield</artifactId>
   <version>1.0-BATE</version>
</dependency>
```

## yaml配置


```yaml
app:
  data:
    shield:
      enable: true
      strategy: aes #支持AES/DES模式 
      key: AD42F6697B035B7580E4FEF93BE20BAD
```

## 业务字段配置

为需要处理的业务字段添加@TableField注解,只支持String类型

encrypt: 修改过程是否需要加密,默认为false

decode: 查询过程是否需要解密,默认为false

asserts: decode为true时，针对某些数据(历史数据)提供断言,默认值DefaultAssert.class(encrypt=true,decode=true)

```java
@Data
public class User {
    private Integer id;

    private String username;

    @TableField(encrypt = true,decode = true,assertion=Base64Assert.class)
    private String phone;
}
```

## 更新日志

2020-05-17

1. `data-shield`问世，针对`mybatis`对业务敏感数据进行加密，包含`query`、`update`操作；加密模式有`AES`和`DES`
2. 新增`DataShieldService`可单独对业务对象进行加解密
3. 新增`DataShieldHelper`可对查询结果进行打码操作

2021-04-03 

1. 添加`mybatis`的`Cursor`查询
2. 修改`query`模式不污染原始查询对象



