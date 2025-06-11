package com.wangshanhai.guard.dataplug;

/**
 * 数据加解密
 * @author Shmily
 */
public class DataEncryptDef {
    /**
     * 签名算法MD5
     */
    public static final String MD5 ="MD5";
    /**
     * 签名算法SHA256
     */
    public static final String SHA256 ="SHA256";
    /**
     * 签名算法HMACSHA256（需要配置密钥）
     */
    public static final String HMACSHA256 ="HMACSHA256";
    /**
     * 非对称加密 （公钥加密私钥解密模式）
     */
    public static final String RSA  ="RSA";
    /**
     * AES
     */
    public static final String AES ="AES";
    /**
     * 国密非对称加密算法，对应RSA
     */
    public static final String SM2 ="SM2";
    /**
     * 国密消息摘要算法，对应MD5
     */
    public static final String SM3 ="SM3";
    /**
     * 国密对称加密算法，对应AES 或 DES等
     */
    public static final String SM4 ="SM4";
}
