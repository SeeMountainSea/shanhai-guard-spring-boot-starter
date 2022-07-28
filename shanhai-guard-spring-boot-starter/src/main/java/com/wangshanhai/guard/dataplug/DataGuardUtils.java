package com.wangshanhai.guard.dataplug;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.hutool.crypto.symmetric.SM4;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.wangshanhai.guard.config.DataGuardConfig;
import com.wangshanhai.guard.mybatis.ShanHaiTmpData;
import com.wangshanhai.guard.utils.Logger;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Shmily
 */
public class DataGuardUtils {
    /**
     * 数据加密
     * @param shanHaiTmpData 原始数据
     * @param shanhaiDataGuardConfig 数据防护组件配置参数
     * @return
     */
    public static String encrypt(ShanHaiTmpData shanHaiTmpData, DataGuardConfig shanhaiDataGuardConfig){
        List<EncryptRule> encryptRules=shanhaiDataGuardConfig.getEncryptRulesExt();
        String source=shanHaiTmpData.getSourceValue();
        Optional<EncryptRule> currentRule;
        Map<String,Object> ruleParams=new HashMap<>();
        if(!StringUtils.isEmpty(shanHaiTmpData.getRuleId())){
            currentRule=encryptRules
                    .stream()
                    .filter(t ->t.getRuleId().equals(shanHaiTmpData.getRuleId()))
                    .findAny();
            if(currentRule.isPresent()){
                ruleParams= currentRule.get().getRuleParams();
            }
        }
        switch (shanHaiTmpData.getEncryptMethod()){
            case DataEncryptDef.MD5:
                source= SecureUtil.md5(shanHaiTmpData.getSourceValue());
                break;
            case DataEncryptDef.SM3:
                source= SmUtil.sm3(shanHaiTmpData.getSourceValue());
                break;
            case DataEncryptDef.SHA256:
                source= SecureUtil.sha256(shanHaiTmpData.getSourceValue());
                break;
            case DataEncryptDef.HMACSHA256:
                if(ruleParams.containsKey("key")){
                    source= SecureUtil.hmac(HmacAlgorithm.HmacSHA256,String.valueOf(ruleParams.get("key"))).digestHex(shanHaiTmpData.getSourceValue());
                }
                break;
            case DataEncryptDef.RSA:
                if(ruleParams.containsKey("publicKey")){
                    RSA rsa=new RSA(null,String.valueOf(ruleParams.get("publicKey")));
                    source= rsa.encryptHex(shanHaiTmpData.getSourceValue(), KeyType.PublicKey);
                }
                break;
            case DataEncryptDef.SM2:
                if(ruleParams.containsKey("publicKey")){
                    SM2 sm2 = SmUtil.sm2(null,String.valueOf(ruleParams.get("publicKey")));
                    source=sm2.encryptHex(shanHaiTmpData.getSourceValue(), KeyType.PublicKey);
                }
                break;
            case DataEncryptDef.AES:
                if(ruleParams.containsKey("key")){
                    String key= String.valueOf(ruleParams.get("key"));
                    if(key.length()==16){
                        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key.getBytes());
                        source=aes.encryptHex(shanHaiTmpData.getSourceValue());
                    }
                }
                break;
            case DataEncryptDef.SM4:
                if(ruleParams.containsKey("key")){
                    String key= String.valueOf(ruleParams.get("key"));
                    if(key.length()==16){
                        SM4 sm4=new SM4(key.getBytes());
                        source=sm4.encryptHex(shanHaiTmpData.getSourceValue());
                    }
                }
                break;
            default:
                Logger.warn("[DataGuard]-字段未指定加密规则");
        }
        return source;
    }
    /**
     * 数据解密
     * @param shanHaiTmpData 原始数据
     * @param shanhaiDataGuardConfig 数据防护组件配置参数
     * @return
     */
    public static String decrypt(ShanHaiTmpData shanHaiTmpData, DataGuardConfig shanhaiDataGuardConfig){
        List<EncryptRule> encryptRules=shanhaiDataGuardConfig.getEncryptRulesExt();
        String source=shanHaiTmpData.getSourceValue();
        Optional<EncryptRule> currentRule;
        Map<String,Object> ruleParams=new HashMap<>();
        if(!StringUtils.isEmpty(shanHaiTmpData.getRuleId())){
            currentRule=encryptRules
                    .stream()
                    .filter(t ->t.getRuleId().equals(shanHaiTmpData.getRuleId()))
                    .findAny();
            if(currentRule.isPresent()){
                ruleParams= currentRule.get().getRuleParams();
            }
        }
        switch (shanHaiTmpData.getDecryptMethod()){
            case DataEncryptDef.RSA:
                if(ruleParams.containsKey("privateKey")){
                    RSA rsa=new RSA(String.valueOf(ruleParams.get("privateKey")),null);
                    source= rsa.decryptStr(shanHaiTmpData.getSourceValue(), KeyType.PrivateKey);
                }
                break;
            case DataEncryptDef.SM2:
                if(ruleParams.containsKey("privateKey")){
                    SM2 sm2 = SmUtil.sm2(String.valueOf(ruleParams.get("privateKey")),null);
                    source=sm2.decryptStr(shanHaiTmpData.getSourceValue(), KeyType.PrivateKey);
                }
                break;
            case DataEncryptDef.AES:
                if(ruleParams.containsKey("key")){
                   String key= String.valueOf(ruleParams.get("key"));
                   if(key.length()==16){
                       SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key.getBytes());
                       source=aes.decryptStr(shanHaiTmpData.getSourceValue());
                   }
                }
                break;
            case DataEncryptDef.SM4:
                if(ruleParams.containsKey("key")){
                    String key= String.valueOf(ruleParams.get("key"));
                    if(key.length()==16){
                        SM4 sm4=new SM4(key.getBytes());
                        source=sm4.decryptStr(shanHaiTmpData.getSourceValue());
                    }
                }
                break;
            default:
                Logger.warn("[DataGuard]-字段未指定加密规则");
        }
        return source;
    }
    /**
     * 数据脱敏
     * @param shanHaiTmpData 原始数据
     * @param shanhaiDataGuardConfig 数据防护组件配置参数
     * @return
     */
    public static String hyposensit(ShanHaiTmpData shanHaiTmpData, DataGuardConfig shanhaiDataGuardConfig){
        List<HyposensitRule> hyposensitRules=shanhaiDataGuardConfig.getHyposensitRulesExt();
        String source=shanHaiTmpData.getSourceValue();
        switch (shanHaiTmpData.getHyposensitMethod()){
            case DataHyposensitDef.IDcard:
                source=source.replaceAll("(?<=\\w{3})\\w(?=\\w{4})", "*"); break;
            case DataHyposensitDef.RealName:
                String reg = ".{1}";
                StringBuffer sb = new StringBuffer();
                Pattern p = Pattern.compile(reg);
                Matcher m = p.matcher(source);
                int i = 0;
                while(m.find()){
                    i++;
                    if(i==1){
                        continue;
                    }
                    m.appendReplacement(sb, "*");
                }
                m.appendTail(sb);
                source= sb.toString(); break;
            case DataHyposensitDef.TelPhone:
                source=source.replaceAll("([1][1-9]\\d{1})\\d{4}(\\d{4})", "$1****$2"); break;
            case DataHyposensitDef.Email:
                source=source.replaceAll("(\\w+)\\w{5}@(\\w+)","$1***@$2"); break;
            case DataHyposensitDef.Money:
                source=source.replaceAll("([1-9])(\\d{0,9})(\\.\\d{1,2})","$1***$3"); break;
            default :
                if(!StringUtils.isEmpty(shanHaiTmpData.getRuleId())){
                    Optional<HyposensitRule> currentRule=hyposensitRules
                            .stream()
                            .filter(t ->t.getRuleId().equals(shanHaiTmpData.getRuleId()))
                            .findAny();
                    if(currentRule.isPresent()){
                        source=source.replaceAll(currentRule.get().getRegex(),currentRule.get().getReplacement()); break;
                    }
                }
        }
        return source;
    }
}
