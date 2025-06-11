package com.wangshanhai.guard.service.impl;

import com.wangshanhai.guard.config.DataGuardConfig;
import com.wangshanhai.guard.dataplug.DataGuardUtils;
import com.wangshanhai.guard.mybatis.ShanHaiTmpData;
import com.wangshanhai.guard.service.ShanHaiDataGuardService;

/**
 * 字段级数据处理
 * @author Shmily
 */
public class DefaultDataGuardServiceImpl implements ShanHaiDataGuardService {
    private DataGuardConfig shanhaiDataGuardConfig;

    public DefaultDataGuardServiceImpl(DataGuardConfig shanhaiDataGuardConfig) {
        this.shanhaiDataGuardConfig = shanhaiDataGuardConfig;
    }

    @Override
    public String encrypt(ShanHaiTmpData shanHaiTmpData) {
        return  DataGuardUtils.encrypt(shanHaiTmpData,shanhaiDataGuardConfig);
    }

    @Override
    public String decrypt(ShanHaiTmpData shanHaiTmpData) {
        return DataGuardUtils.decrypt(shanHaiTmpData,shanhaiDataGuardConfig);
    }

    @Override
    public String hyposensit(ShanHaiTmpData shanHaiTmpData) {
        return DataGuardUtils.hyposensit(shanHaiTmpData,shanhaiDataGuardConfig);
    }

}
