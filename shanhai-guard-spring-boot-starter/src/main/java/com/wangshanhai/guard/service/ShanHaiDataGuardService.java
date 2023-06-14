package com.wangshanhai.guard.service;

import com.wangshanhai.guard.mybatis.ShanHaiTmpData;

/**
 * @author Shmily
 */
public interface ShanHaiDataGuardService {
    /**
     * 执行数据加密
     * @param shanHaiTmpData
     * @return
     */
    public String encrypt(ShanHaiTmpData shanHaiTmpData);
    /**
     * 执行数据解密
     * @param shanHaiTmpData
     * @return
     */
    public String decrypt(ShanHaiTmpData shanHaiTmpData);
    /**
     * 执行数据脱敏
     * @param shanHaiTmpData
     * @return
     */
    public String hyposensit(ShanHaiTmpData shanHaiTmpData);
}
