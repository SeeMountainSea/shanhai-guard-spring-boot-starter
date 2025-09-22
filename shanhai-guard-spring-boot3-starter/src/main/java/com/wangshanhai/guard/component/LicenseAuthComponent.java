package com.wangshanhai.guard.component;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.wangshanhai.guard.interceptor.LicenseAuthInterceptor;
import com.wangshanhai.guard.license.HardwareLicense;
import com.wangshanhai.guard.license.HardwareUtil;
import com.wangshanhai.guard.license.LicenseInfo;
import com.wangshanhai.guard.service.LicenseAuthService;
import com.wangshanhai.guard.service.impl.DefaultLicenseAuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 硬件授权
 * @author license
 */
@Configuration
@AutoConfigureAfter(WebMvcConfigurationSupport.class)
public class LicenseAuthComponent implements WebMvcConfigurer,ApplicationListener<ApplicationReadyEvent> {
    @Value("${license.lic:}")
    private String licFile;
    private String licPublicKey="-";
    @Lazy
    @Autowired
    private LicenseAuthService licenseAuthService;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LicenseAuthInterceptor(this.licFile,this.licenseAuthService))
                .addPathPatterns("/**");
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Map<String, String> bannerInfo=new HashMap<>();
        String lic="";
        try{
            lic= FileUtil.readString(licFile, StandardCharsets.UTF_8);
        }catch (Exception e){
            bannerInfo.put("tips","请安装License后继续使用");
            exit(bannerInfo);
        }
        if(StrUtil.isEmpty(lic)){
            bannerInfo.put("tips","请检查License安装是否正确");
            exit(bannerInfo);
        }
        if(!StrUtil.isEmpty(System.getenv("license.publicKey"))){
            this.licPublicKey=System.getenv("license.publicKey");
        }
        LicenseInfo licenseInfo=null;
        if(!HardwareLicense.validateLicense(lic,licPublicKey)){
            licenseInfo= HardwareLicense.showLicense(lic,licPublicKey);
            bannerInfo.put("tips","License授权已失效");
            bannerInfo.put("hardwareOwner",licenseInfo==null?"-":licenseInfo.getHardwareOwner());
            bannerInfo.put("installTime",licenseInfo==null?"-":licenseInfo.getInstallTime().toString());
            bannerInfo.put("expiryDate",licenseInfo==null?"-":licenseInfo.getExpiryDate().toString());
            bannerInfo.put("licenseNo",licenseInfo==null?"-":licenseInfo.getLicenseNo());
            exit(bannerInfo);
        }else{
            licenseInfo= HardwareLicense.showLicense(lic,licPublicKey);
            bannerInfo.put("tips","已授权");
            bannerInfo.put("hardwareId",licenseInfo==null?"-":licenseInfo.getHardwareId());
            bannerInfo.put("hardwareOwner",licenseInfo==null?"-":licenseInfo.getHardwareOwner());
            bannerInfo.put("installTime",licenseInfo==null?"-":licenseInfo.getInstallTime().toString());
            bannerInfo.put("expiryDate",licenseInfo==null?"-":licenseInfo.getExpiryDate().toString());
            bannerInfo.put("licenseNo",licenseInfo==null?"-":licenseInfo.getLicenseNo());
            print(bannerInfo);
            FileUtil.writeUtf8String(String.valueOf(Instant.now().getEpochSecond()),System.getenv(licenseInfo.getHardwareClock()));
        }
    }
    private void exit(Map<String, String> bannerInfo){
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
    @Bean
    @ConditionalOnMissingBean
    public LicenseAuthService generateDefaultLicenseAuthService() {
        return new DefaultLicenseAuthServiceImpl();
    }
}
