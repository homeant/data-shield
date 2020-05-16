# data-shield

> 针对mybatis对业务敏感数据进行加密

## pom.xml配置


```xml
<dependency>
   <groupId>fun.vyse.cloud</groupId>
   <artifactId>data-shield</artifactId>
   <version>${data-shield.version}</version>
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
