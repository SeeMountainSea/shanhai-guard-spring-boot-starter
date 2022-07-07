package com.wangshanhai.examples.service.impl;

import com.wangshanhai.guard.mybatis.ShanHaiTmpData;
import com.wangshanhai.guard.service.ShanHaiDataGuardService;
import org.springframework.stereotype.Service;

@Service
public class ShanHaiDataGuardServiceImpl implements ShanHaiDataGuardService {
    @Override
    public String encrypt(ShanHaiTmpData shanHaiTmpData) {
        return shanHaiTmpData.getSourceValue()+"@"+shanHaiTmpData.getEncryptMethod();
    }

    @Override
    public String hyposensit(ShanHaiTmpData shanHaiTmpData) {
        return shanHaiTmpData.getSourceValue()+"@"+shanHaiTmpData.getHyposensitMethod();
    }

    @Override
    public String decrypt(ShanHaiTmpData shanHaiTmpData) {
        return shanHaiTmpData.getSourceValue()+"@"+shanHaiTmpData.getDecryptMethod();
    }
}
