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
  
使用说明：https://seemountainsea.github.io/guard/
