package com.wangshanhai.guard.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.Key;
import java.security.SecureRandom;

/**
 * PBE加解密算法
 */
public class PBEUtils {
    /**
     * 支持列表
     * @apiNote
     * PBEWithMD5AndDES
     * PBEWithMD5AndTripleDES
     * PBEWithSHA1AndDESede
     * PBEWithSHA1AndRC2_40
     */
    public static final String ALGORITHM = "PBEWITHMD5andDES";

    /**
     * 盐初始化
     * @return
     * @throws Exception
     */
    public static String initSalt() throws Exception {
        //实例化安全随机数
        SecureRandom random = new SecureRandom();
        //产出盐
        return encryptBASE64(random.generateSeed(8));
    }
    /**
     * 转换密钥<br>
     *
     * @param password
     * @return
     * @throws Exception
     */
    private static Key toKey(String password) throws Exception {
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey secretKey = keyFactory.generateSecret(keySpec);
        return secretKey;
    }

    /**
     * 加密
     *
     * @param data 数据
     * @param password 密码
     * @param salt  盐
     * @return
     * @throws Exception
     */
    public static String encrypt(String data, String password, String salt)
            throws Exception {
        Key key = toKey(password);
        PBEParameterSpec paramSpec = new PBEParameterSpec(decryptBASE64(salt), 100);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
        return encryptBASE64(cipher.doFinal(data.getBytes()));
    }


    /**
     * 安全模式加密
     * @param data 数据
     * @param password 密码
     * @param salt 盐
     * @param cycleNum 循环次数（>100000）
     * @return
     * @throws Exception
     */
    public static String encryptSafe(String data, String password, String salt,int cycleNum)
            throws Exception {
        Key key = toKey(password);
        PBEParameterSpec paramSpec = new PBEParameterSpec(decryptBASE64(salt), cycleNum>10000?cycleNum:100000);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
        return encryptBASE64(cipher.doFinal(data.getBytes()));
    }

    /**
     * 解密
     *
     * @param data  数据
     * @param password 密码
     * @param salt  盐
     * @return
     * @throws Exception
     */
    public static String decrypt(String data, String password, String salt)
            throws Exception {
        Key key = toKey(password);
        PBEParameterSpec paramSpec = new PBEParameterSpec(decryptBASE64(salt), 100);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        return new String(cipher.doFinal(decryptBASE64(data)));
    }

    /**
     * 安全模式解密
     * @param data 数据
     * @param password 密码
     * @param salt 盐
     * @param cycleNum 循环次数（>10000）
     * @return
     * @throws Exception
     */
    public static String decryptSafe(String data, String password, String salt,int cycleNum)
            throws Exception {
        Key key = toKey(password);
        PBEParameterSpec paramSpec = new PBEParameterSpec(decryptBASE64(salt), cycleNum>10000?cycleNum:100000);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        return new String(cipher.doFinal(decryptBASE64(data)));
    }

    /**
     * BASE64解密
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptBASE64(String key) throws Exception {
        return (new BASE64Decoder()).decodeBuffer(key);
    }

    /**
     * BASE64加密
     * @param key
     * @return
     * @throws Exception
     */
    public static String encryptBASE64(byte[] key) throws Exception {
        return (new BASE64Encoder()).encodeBuffer(key);
    }

    public static void main(String[] args) throws Exception {
        //生成盐值
        String salt= PBEUtils.initSalt();
        //自定义密钥
        String passwd="20220111";
        //待加密参数值
        String val="zhangsanxxx1";
        String encryptStr=PBEUtils.encryptSafe(val,passwd,salt,100000);
        //加密结果
        System.out.println("salt:"+salt);
        System.out.println("encryptStr:"+encryptStr);
        //解密结果
        System.out.println(PBEUtils.decryptSafe(encryptStr,passwd,salt,100000));
    }
}
