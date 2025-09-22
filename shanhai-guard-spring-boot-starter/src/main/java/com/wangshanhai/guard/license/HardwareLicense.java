package com.wangshanhai.guard.license;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.alibaba.fastjson2.JSONObject;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Random;

/**
 * 硬件授权
 * @author license
 */
public class HardwareLicense {
    private static final String CHAR_POOL = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Random random = new Random();
    /**
     * 生成证书
     * @param licenseInfo 证书信息
     * @param privateKey 私钥
     */
    public static String generateLicense(LicenseInfo licenseInfo,String privateKey){
        try{
            if(StrUtil.isEmpty(licenseInfo.getLicenseNo())){
                licenseInfo.setLicenseNo(generate(10));
            }
            RSA rsa = new RSA(privateKey,null );
            return HexUtil.encodeHexStr(rsa.encrypt(JSONObject.toJSONString(licenseInfo), KeyType.PrivateKey));
        }catch (Exception e){
            System.out.println("生成证书失败："+e.getMessage());
        }
        return null;
    }

    public static String generate(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHAR_POOL.length());
            sb.append(CHAR_POOL.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 展示授权信息
     * @param licenseInfo 证书信息
     * @param publicKey 公钥
     * @return
     */
    public static LicenseInfo showLicense(String licenseInfo,String publicKey){
        try{
            RSA rsa = new RSA(null, publicKey);
            String license=rsa.decryptStr(licenseInfo, KeyType.PublicKey);
            return JSONObject.parseObject(license,LicenseInfo.class);
        }catch (Exception e){
            System.out.println("证书加载失败");
        }
        return null;
    }
    /**
     * 校验证书
     * @param licenseInfo 证书信息
     * @param publicKey 公钥
     * @return
     */
    public static boolean validateLicense(String licenseInfo,String publicKey){
        try{
            RSA rsa = new RSA(null, publicKey);
            String license=rsa.decryptStr(licenseInfo, KeyType.PublicKey);
            LicenseInfo souceLicenseInfo=JSONObject.parseObject(license,LicenseInfo.class);
            // 5. 硬件绑定验证
            if(!HardwareUtil.getMachineId().contains(souceLicenseInfo.getHardwareId())) {
                return false;
            }
            // 6. 有效期验证
            if (LocalDate.now().isAfter(souceLicenseInfo.getExpiryDate())) {
                return false;
            }
            long installTime = souceLicenseInfo.getInstallTime().atStartOfDay(ZoneOffset.ofHours(8))
                    .toInstant()
                    .getEpochSecond();
            // 7. 时间篡改防护（三重验证）
            return checkTimeConsistency(installTime, souceLicenseInfo.getHardwareClock(),souceLicenseInfo.getExpiryDate());
        }catch (Exception e){
            System.out.println("证书校验失败");
        }
        return false;
    }
    private static boolean checkTimeConsistency(long installTime,String hardwareClock,LocalDate expiry){
        try{
            long currentSec = Instant.now().getEpochSecond();
            // 规则1：当前时间必须晚于安装时间
            if (currentSec < installTime) {
                return false;
            }
            long lastTime=Long.parseLong(FileUtil.readString(System.getenv(hardwareClock), StandardCharsets.UTF_8));
            // 当前时间必须大于上次记录时间
            if (currentSec <= lastTime) {
                return false;
            }
            // 规则3：存储的时间不能超过证书有效期
            LocalDate maxDate = Instant.ofEpochSecond(currentSec).atZone(ZoneId.systemDefault()).toLocalDate();
            if (maxDate.isAfter(expiry)) {
                return false;
            }
            FileUtil.writeUtf8String(String.valueOf(currentSec),System.getenv(hardwareClock));
            return true;
        }catch (Exception e){
            System.out.println("证书校验失败");
        }
        return false;
    }


}
