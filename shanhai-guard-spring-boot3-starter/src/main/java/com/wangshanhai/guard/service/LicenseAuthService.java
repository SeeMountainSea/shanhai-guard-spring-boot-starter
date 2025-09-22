package com.wangshanhai.guard.service;

/**
 * 授权服务
 * @author License
 */
public interface LicenseAuthService {
    /**
     * 证书无效时调用
     */
    public void handleInvalidLicense();
}
