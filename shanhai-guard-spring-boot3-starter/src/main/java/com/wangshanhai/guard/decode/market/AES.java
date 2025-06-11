package com.wangshanhai.guard.decode.market;

import com.wangshanhai.guard.decode.PropertyDecode;
import com.wangshanhai.guard.utils.AESUtils;

import java.util.Properties;

/**
 * AES解密算法
 * @author Shmily
 */
public class AES extends PropertyDecode {
    @Override
    public Properties getProperty(Properties envProperties, Properties configProperties) {
        String aeskey=envProperties.getProperty("shanhai.envdecode.market.key");
        String iv=envProperties.getProperty("shanhai.envdecode.market.iv");
        int mode=Integer.parseInt(envProperties.getProperty("shanhai.envdecode.market.mode"));
        for(Object key:configProperties.keySet()){
            try{
                String k=String.valueOf(key);
                String val=configProperties.getProperty(k);
                configProperties.setProperty(k, AESUtils.decrypt(val,aeskey,iv,mode));
            }catch (Exception e){
                System.out.println("[shanhai-envdecode-aes-error]-msg:"+e.getMessage());
            }
        }
        return configProperties;
    }
}
