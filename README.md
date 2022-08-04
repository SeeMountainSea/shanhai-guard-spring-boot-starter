<div align="center">
  <p>
    <img src="logo.jpg"  height="200px" />
  </p>
  <p>山海Guard - 基于SpringBoot 的通用Web安全组件</p>
  <p>ShanHaiGuard-based SpringBoot Web Security components</p>
  <p>
    <a href="https://github.com/SeeMountainSea/shanhai-guard-spring-boot-starter/releases/latest"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SeeMountainSea/shanhai-guard-spring-boot-starter"/></a>
    <a href="https://github.com/SeeMountainSea/shanhai-guard-spring-boot-starter/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SeeMountainSea/shanhai-guard-spring-boot-starter?color=009688"/></a>
    <a href="https://github.com/topics/java"><img alt="GitHub top language" src="https://img.shields.io/github/languages/top/SeeMountainSea/shanhai-guard-spring-boot-starter?color=eb8031"/></a>
    <br>
    <a href="https://github.com/SeeMountainSea/shanhai-guard-spring-boot-starter/find/master"><img alt="GitHub Code Size" src="https://img.shields.io/github/languages/code-size/SeeMountainSea/shanhai-guard-spring-boot-starter?color=795548"/></a>
    <a href="https://github.com/SeeMountainSea/shanhai-guard-spring-boot-starter/find/master"><img alt="GitHub Code Lines" src="https://img.shields.io/tokei/lines/github/SeeMountainSea/shanhai-guard-spring-boot-starter?color=37474F"/></a>
    <a href="https://github.com/SeeMountainSea/shanhai-guard-spring-boot-starter/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SeeMountainSea/shanhai-guard-spring-boot-starter?color=534BAE"/></a>
  </p>
</div>

ShanHaiGuard 主要提供以下能力：

- 支持全局文件上传安全检测 @Ver1.0.0+支持

- 支持单个方法文件上传安全检测 @Ver1.0.0+支持

- 支持单个方法文件上传自定义安全校验规则 @Ver1.0.1+支持

- 支持基于Mybatis-Plus在进行数据字段级加密与脱敏（支持自行扩展实现相关算法）@Ver1.0.7+支持

- 支持SQL注入&XSS注入安全检测 @Ver1.0.0+支持

- 支持Mybatis SQL查询安全审核（1.x仅支持mysql）@Ver1.0.0+支持

- 支持密码复杂度检验 @Ver1.0.0+支持

- 支持拓展SpringBoot POST数据定制化解析，从而实现全局自动数据解析能力(例如数据解码,动态修改数据) @Ver1.0.0+支持

- 支持对SpringBoot配置文件任意参数进行参数加密。默认算法为PBE，同时支持自己扩展解密算法（尚未适配SpringCloud和其他配置中心)  @Ver1.0.7+支持

  注：由于ShanHaiGuard是基于SpringBoot 2.x的，因此可能有部分组件不支持SpringBoot1.x版本的。鉴于官方已经不再更新SpringBoot 1.X，因此不再考虑兼容SpringBoot1.x版本。
  
  

# 1.引入依赖

```xml
        <dependency>
            <groupId>com.wangshanhai.guard</groupId>
            <artifactId>shanhai-guard-spring-boot-starter</artifactId>
            <version>${shanhaiguard.last.version}</version>
        </dependency>
```

# 2.启用ShanHaiGuard安全防护组件

```java
@Configuration
@EnableShanHaiGuard
public class ShanHaiGuardConfig {
}
```

# 3.组件说明

## 3.1 文件上传检测

配置参数如下

```yaml
shanhai:
  fileguard:
    enable: true  #启用组件
    pathPatterns: #检测范围
      - '/**'
    suffix: jpg,gif,png,ico,bmp,jpeg #文件上传白名单
    logTarce: true  #启用跟踪日志
```

此处的配置为全局性配置，对于单个方法的校验，可以使用@FileGuard添加校验，参考下面样例的使用方式：

1）跳过检验

```java
@FileGuard(skip  = true)
```

2）实现自定义规则检验

```java
@FileGuard(checkByRule = true)
```

 注：如果启用该参数，需要实现FileGuardRuleDefService，否则将无法上传文件。

```java
public class FileGuardRuleDefServiceImpl implements FileGuardRuleDefService {
    /**
     * 表单key及对应的文件
     * @param files 文件清单
     * @return
     */
    @Override
    public boolean isSafe(Map<String, MultipartFile> files) {
        return true;
    }
}
```

3）通过文件后缀校验(GuardType.SUFFIX)

```java
/**
 * 文件上传测试样例
 * @param request
 * @return
 */
@RequestMapping(value = "/file/suffix/upload")
@FileGuard(message = "只能上传图片文件",type = FileGuard.GuardType.SUFFIX,supportedSuffixes = {"png", "jpg", "jpeg"})
@ResponseBody
public String suffix(HttpServletRequest request, MultipartFile file){
    Logger.info("[file-upload-api]-name:{},contentType:{},size:{}",file.getName(),file.getContentType(),file.getSize());
    return "success";
}
```

4)通过二进制文件头检验(GuardType.BYTES)

```java
/**
 * 文件上传测试样例
 * @param request
 * @return
 */
@RequestMapping(value = "/file/bytes/upload")
@FileGuard(message = "只能上传图片文件",type = FileGuard.GuardType.BYTES,supportedFileTypes = {FileType.JPEG,FileType.PNG})
@ResponseBody
public String bytes(HttpServletRequest request, MultipartFile file){
    Logger.info("[file-upload-api]-name:{},contentType:{},size:{}",file.getName(),file.getContentType(),file.getSize());
    return "success";
}
```

**注：单个方法的白名单必须为全局性白名单的子集**

## 3.2 密码复杂度检测

配置参数如下

```yaml
shanhai:
  passwdguard:
    enable: true #启用组件
    minLength: 4 #最小长度
    maxLength: 10 #最大长度
    characterExist: true #包含大小写字母
    numberExist: true #包含数字
    symbolExist: true #包含符号
    keyboardNotExist: true #不包含键盘排序
```

在业务模块可以直接使用密码复杂度检测服务PasswdService，示例如下：

```java
//注入服务
@Autowired
private PasswdService passwdService;
//调用服务方法
passwdService.checkPasswd(passwd)
```

## 3.3 SQL&XSS注入检测

配置参数如下

```yaml
shanhai:
  webguard:
    enable: true #启用组件
    path-patterns: #检测范围
      - '/*'
```

## 3.4 Mysql数据安全检测

配置参数如下

```yaml
shanhai:
  mysqlguard:
    enable: true       #启用组件
    where-exist: false  #包含where语句
    limit-exist: false  #包含limit语句
    query-limit: 20000  #limit条数
```

## 3.5 @RequestBody 通用解码组件

配置参数如下

```yaml
shanhai:
  decodebody:
    enable: true #启用组件
```

注：此组件只是做了通用性封装，对于解码逻辑需要自行实现接口DecodeBodyService的decodeRequestBody方法，在进行数据解码时，会自动调用该接口的实现方法进行自定义参数解码操作。

接口定义如下：

```java
public interface DecodeBodyService {
    /**
     * 解析加密参数
     * @param body 原始报文数据
     * @return
     */
    public String decodeRequestBody(String body);
}
```

## 3.6 SpringBoot配置文件参数加密

### 3.6.1 使用内置PBE算法处理加密参数

**由于配置文件参数解析在Spring初始化的时候完成，因此默认启用，无法关闭。**

**现已经对数据解析进行了修复，支持使用命令行参数进行PEB密钥配置。**

使用PBE解密算法样例

```yaml
shanhai:
  envdecode:
    market:
      algorithm: PBE
      pebSalt: 'VjBnT0Qo8hI='  #自定义PBE解密盐
      pebPasswd: '20220111'    #自定义PBE解密密钥
```

SpringBoot配置参数加密说明：

1）所有加密参数的配置值以**envdecode::**打头，举例如下：

```yaml
app:
  version: 'envdecode::iezthxHWDp/fhXYXZSjhVw=='
```

2）PBE盐值及加密结果生成样例

```java
public static void main(String[] args) throws Exception {
    //生成盐值
    String salt= PBEUtils.initSalt();
    //自定义密钥
    String passwd="20220111";
    //待加密参数值
    String val="zhangsanxxx";
    String encryptStr=PBEUtils.encrypt(val,passwd,salt);
    //加密结果
    System.out.println(salt+":"+encryptStr);
    //解密结果
    System.out.println(PBEUtils.decrypt(encryptStr,passwd,salt));
}
```

### 3.6.2 自定义处理加密参数

首先需要实现一个自己的参数解密类，要求该类必须继承于PropertyDecode，并实现其定义的解密方法getProperty，PropertyDecoded定义如下：

```java
public  abstract class PropertyDecode {
    /**
     * 自定义解析算法
     * @param envProperties 通用配置参数 
     * @param configProperties 待解密属性key:value
     * @return
     */
    @Nullable
    public abstract Properties getProperty(Properties envProperties,Properties configProperties);
}
```

**注：SpringBoot配置文件中shanhai.envdecode打头的参数都会传递到envProperties中，可以自己获取自己配置的自定义参数。**

在SpringBoot的配置文件中指定自己新增的自定义解密类，如下所示：

```yaml
shanhai:
 envdecode:
    # 自定义解密类的优先级最高
    className: 'com.xxx.RSAPropertyDecode'
```


**注 ：如果既存在自定义解密类，又存在PBE解密配置的，自定义解密类优先级最高。**

## 3.7 基于Mybatis-Plus进行字段级加解密与数据脱敏

### 3.7.1 使用内置算法进行数据字段级加解密操作

配置参数如下：

```yaml
shanhai:
  dataguard:
    enable: true       #启用组件
    encryptRulesExt:  #加密算法参数配置(对称加密&非对称加密均需配置)
      - {ruleId: 'AES', ruleParams: {key: wjy59188wjy59188}} #内置AES加密算法示例(key的长度为16位) 
      - {ruleId: 'SM4', ruleParams: {key: wjy59188wjy59188}} #内置SM4加密算法示例(key的长度为16位) 
      - {ruleId: 'HMACSHA256', ruleParams: {key: wjy59188wjy59188}} #内置HMACSHA256加密算法示例
      - {ruleId: 'RSA', ruleParams: {publicKey: wjy59188wjy59188,privateKey: wjy59188wjy59188}} #内置RSA加密算法示例
      - {ruleId: 'SM2', ruleParams: {publicKey: wjy59188wjy59188,privateKey: wjy59188wjy59188}} #内置SM2加密算法示例 
```

注：此处的ruleId的值是示例，实际使用时需要修改为FieldDataGuard中定义的ruleId对应的值，ruleId必须自行确保全局唯一，否则数据解析会有问题。ruleParams中的参数，使用内置算法时，参数名必须为示例配置中的参数名才可以，相关key的值可以自己自定义。

如果需要使用国密算法，需要引入额外的依赖才可以

```
        <dependency>
            <groupId>org.bouncycastle</groupId>
            <artifactId>bcprov-jdk15to18</artifactId>
            <version>${bcprov.version}</version>
        </dependency>
```

新建自己的数据解析实现并继承DefaultDataGuardServiceImpl，可以实现使用默认算法或者自己扩展其他加解密算法。

```java
@Service
public class XXXDataGuardServiceImpl extends DefaultDataGuardServiceImpl {
    @Autowired
    private DataGuardConfig shanhaiDataGuardConfig;

    public XXXDataGuardServiceImpl(DataGuardConfig shanhaiDataGuardConfig) {
        super(shanhaiDataGuardConfig);
    }

    @Override
    public String encrypt(ShanHaiTmpData shanHaiTmpData) {
        //TODO 可以在此处扩展自定义加密算法实现
        return super.encrypt(shanHaiTmpData);
    }

    @Override
    public String decrypt(ShanHaiTmpData shanHaiTmpData) {
        //TODO 可以在此处扩展自定义解密算法实现
        return super.decrypt(shanHaiTmpData);
    }

    @Override
    public String hyposensit(ShanHaiTmpData shanHaiTmpData) {
        //TODO 可以在此处扩展自定义脱敏算法实现
        return super.hyposensit(shanHaiTmpData);
    }
}

```

在Domain中添加类扫描注解@ShanHaiDataGuard,在需要操作的字段添加 @FieldDataGuard注解：

加密示例如下：

```java
@ShanHaiDataGuard
public class DictData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @FieldDataGuard(ruleId = "dictLabel",encrypt = true,encryptMethod = DataEncryptDef.SHA256,encryptExecModel = DataExecModel.SAVE)
    private String dictLabel;
}

```

解密示例如下：

```java
@ShanHaiDataGuard
public class DictData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @FieldDataGuard(ruleId = "dictLabel",decrypt  = true,decryptMethod = DataEncryptDef.AES,decryptExecModel = DataExecModel.QUERY)
    private String dictLabel;
}

```

@FieldDataGuard 各字段定义如下：

```java
     /**
     * 规则ID，用于自行根据规则进行相关扩展
     * @return
     */
    String ruleId() default "";
    /**
     * 是否启用数据加密
     * @return
     */
    boolean encrypt() default false;
    /**
     * 加密算法 默认算法集参考DataEncryptDef
     * @return
     */
    String encryptMethod() default "";
    /**
     * 执行加密算法的时机 参考DataExecModel
     * @return
     */
    String encryptExecModel() default "";
    /**
     * 是否启用数据查询解密
     * @return
     */
    boolean decrypt() default false;

    /**
     * 解密算法 默认算法集参考DataEncryptDef
     * @return
     */
    String decryptMethod() default "";
    /**
     * 执行解密算法的时机 参考DataExecModel
     * @return
     */
    String decryptExecModel() default "";
    /**
     * 是否启用数据脱敏
     * @return
     */
    boolean hyposensit() default false;

    /**
     * 数据脱敏算法 默认算法集参考DataHyposensitDef
     * @return
     */
    String hyposensitMethod() default "";
    /**
     * 执行脱敏算法的时机 参考DataExecModel
     * @return
     */
    String hyposensitExecModel() default "";
}
```

ShanHaiTmpData可以获取到原始字段值以及处理这个值所需要的运行算法名称。需要注意的是，如果是既要加密又要脱敏，则先执行脱敏再执行加密。如果既要解密又要脱敏，则先执行解密然后执行脱敏。

加解密默认算法（DataEncryptDef）：

**MD5/SHA256/HMACSHA256/RSA/AES/SM2/SM3/SM4**

### 3.7.2 数据字段级脱敏操作

配置参数如下：

```yaml
shanhai:
  dataguard:
    enable: true       #启用组件
    trace-log: true    #启用跟踪日志
    hyposensitRulesExt: #脱敏规则定义（需要自行扩展时才需要配置，默认不需要配置该扩展）
      - {ruleId: 'yourRule',regex: '([1][1-9]\d{1})\d{4}(\d{4})',replacement: '$1****$2'}
```

注：此处的ruleId的值是示例，实际使用时自己可以改为其他标识字符串即可，regex为自定义正则，replacement为脱敏后的格式。使用的String.replaceAll做脱敏处理。

脱敏默认算法（DataHyposensitDef）：

**IDcard/RealName/TelPhone/email/money** 分别对应的是：身份证/姓名/手机号/邮箱/金额

数据脱敏示例如下：

```java
@ShanHaiDataGuard
public class DictData implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @FieldDataGuard(ruleId = "dictLabel",hyposensit = true,hyposensitMethod = DataHyposensitDef.IDcard,hyposensitExecModel = DataExecModel.QUERY)
    private String dictLabel;
}
```

### 3.7.3 使用自定义算法进行数据字段级加解密操作

配置参数如下：

```yaml
shanhai:
  dataguard:
    enable: true       #启用组件
    encryptRulesExt:  #加密算法参数配置(自定义算法实现)
      - {ruleId: 'xxxxx', ruleParams: {key: wjy59188wjy59188, iv: aaaaa1111, source: device}} 
```

此处ruleParams中的参数你可以按照自己需要进行自定义，此处只是一个示例。

在自己的数据解析实现扩展加解密算法或者数据脱敏操作。

```java
@Service
public class XXXDataGuardServiceImpl extends DefaultDataGuardServiceImpl {
    @Autowired
    private DataGuardConfig shanhaiDataGuardConfig;

    public XXXDataGuardServiceImpl(DataGuardConfig shanhaiDataGuardConfig) {
        super(shanhaiDataGuardConfig);
    }

    @Override
    public String encrypt(ShanHaiTmpData shanHaiTmpData) {
        //TODO 可以在此处扩展自定义加密算法实现
        return super.encrypt(shanHaiTmpData);
    }

    @Override
    public String decrypt(ShanHaiTmpData shanHaiTmpData) {
        //TODO 可以在此处扩展自定义解密算法实现
        return super.decrypt(shanHaiTmpData);
    }
}

```

自己定义的加解密算法的配置参数可以从DataGuardConfig->encryptRulesExt->EncryptRule中获取，ruleParams在EncryptRule中以Map的形式存在。对于Domain对象中的ruleId以及原始字段数据可以从ShanHaiTmpData中获取。

对于脱敏，暂不支持配置自定义脱敏算法。需要实现的可以自己重写hyposensit的实现。

## 3.8 常见问题

### 3.8.1 文件上传检测不生效

在springboot中使用多个继承WebMvcConfigurationSupport的类是行不通的，而且使用注解@configuration去加载配置类只能挂载一个继承WebMvcConfigurationSupport。

解决办法：自己项目中的webmvc配置要实现 webMvcConfigurer 接口而不能使用继承WebMvcConfigurationSupport类的方式，这样组件和自己项目中的webmvc都可以挂载。

示例如下：

```java
@Configuration
public class XXXConfig implements WebMvcConfigurer {

}
```
