package com.wangshanhai.guard.interceptor;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.wangshanhai.guard.license.HardwareLicense;
import com.wangshanhai.guard.license.HardwareUtil;
import com.wangshanhai.guard.license.LicenseInfo;
import com.wangshanhai.guard.service.LicenseAuthService;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 授权拦截器
 * @author Shmily
 */
public class LicenseAuthInterceptor  implements HandlerInterceptor {
    private static String lic="";
    private String licFile;
    private String licPublicKey="-";
    private int reqCount=1;
    private int reqCheck=10;
    private LicenseAuthService licenseAuthService;
    public LicenseAuthInterceptor(String licFile,LicenseAuthService licenseAuthService) {
        this.licFile = licFile;
        if(!StrUtil.isEmpty(System.getenv("license.publicKey"))){
            this.licPublicKey=System.getenv("license.publicKey");
        }
        this.licenseAuthService=licenseAuthService;
        if(!StrUtil.isEmpty(System.getenv("license.reqCheck"))){
            this.reqCheck= Integer.parseInt(System.getenv("license.reqCheck"));
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String, String> bannerInfo=new HashMap<>();
        reqCount++;
        if(StrUtil.isEmpty(lic)){
            try{
                lic= FileUtil.readString(licFile,StandardCharsets.UTF_8);
            }catch (Exception e){
                bannerInfo.put("tips","请安装License后继续使用");
                return exit(bannerInfo);
            }
        }
        if(StrUtil.isEmpty(lic)){
            bannerInfo.put("tips","请检查License安装是否正确");
            return exit(bannerInfo);
        }
        if(reqCount % reqCheck == 0){
            if(HardwareLicense.validateLicense(lic,licPublicKey)){
                 LicenseInfo licenseInfo= HardwareLicense.showLicense(lic,licPublicKey);
                 FileUtil.writeUtf8String(String.valueOf(Instant.now().getEpochSecond()),System.getenv(licenseInfo.getHardwareClock()));
                 return true;
            }
            LicenseInfo licenseInfo= HardwareLicense.showLicense(lic,licPublicKey);
            bannerInfo.put("tips","License授权已失效");
            bannerInfo.put("hardwareId", HardwareUtil.getMachineId());
            bannerInfo.put("hardwareOwner",licenseInfo==null?"-":licenseInfo.getHardwareOwner());
            bannerInfo.put("installTime",licenseInfo==null?"-":licenseInfo.getInstallTime().toString());
            bannerInfo.put("expiryDate",licenseInfo==null?"-":licenseInfo.getExpiryDate().toString());
            bannerInfo.put("licenseNo",licenseInfo==null?"-":licenseInfo.getLicenseNo());
            return exit(bannerInfo);
        }
        return true;
    }
    private boolean exit(Map<String, String> bannerInfo){
        try{
            bannerInfo.put("hardwareId",HardwareUtil.getMachineId());
            bannerInfo.put("hardwareFingerprinting",HardwareUtil.getMachineInfo(false));
            licenseAuthService.handleInvalidLicense();
            Thread.sleep(10_000);
            print(bannerInfo);
            System.exit(0);
        }catch (Exception e){
            System.out.println("请安装License后继续使用");
        }
        return false;
    }
    private void print(Map<String, String> params){
        System.out.println("【软件授权证书】");
        System.out.println("当前日期: "+ LocalDate.now());
        System.out.println("设备 ID: "+params.getOrDefault("hardwareId","-"));
        if(params.containsKey("hardwareFingerprinting")){
            System.out.println("设备指纹: "+params.getOrDefault("hardwareFingerprinting","-"));
        }
        System.out.println("授权单位: "+params.getOrDefault("hardwareOwner","-"));
        System.out.println("签发日期: "+params.getOrDefault("installTime","-"));
        System.out.println("到期日期: "+params.getOrDefault("expiryDate","-"));
        System.out.println("授权编号: "+params.getOrDefault("licenseNo","-"));
        System.out.println("温馨提示: "+params.getOrDefault("tips","-"));
    }
}
