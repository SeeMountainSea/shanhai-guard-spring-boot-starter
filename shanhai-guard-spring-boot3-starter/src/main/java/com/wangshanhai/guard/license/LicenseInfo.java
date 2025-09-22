package com.wangshanhai.guard.license;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 证书授权信息
 * @author license
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicenseInfo {
    /**
     * 硬件指纹
     */
    private String hardwareId;
    /**
     * 硬件归属
     */
    private String hardwareOwner;
    /**
     * 证书到期时间
     */
    private LocalDate expiryDate;
    /**
     * 证书初始化时间
     */
    private LocalDate installTime;
    /**
     * 证书信息
     */
    private String license;
    /**
     * 授权编码
     */
    private String licenseNo;
    /**
     * 机器时钟
     */
    private String hardwareClock;
}
