package com.sayrmb.guard.decode.market;

import com.sayrmb.guard.decode.PropertyDecode;
import com.sayrmb.guard.utils.PBEUtils;

import java.util.Properties;

/**
 * PEB解密算法
 */
public class PBE extends PropertyDecode {
    @Override
    public Properties getProperty(Properties envProperties, Properties configProperties) {
        String salt=envProperties.getProperty("sayhi.envdecode.market.pebSalt");
        String passwd=envProperties.getProperty("sayhi.envdecode.market.pebPasswd");
        for(Object key:configProperties.keySet()){
            try{
                String k=String.valueOf(key);
                String val=configProperties.getProperty(k);
                configProperties.setProperty(k,PBEUtils.decrypt(val,passwd,salt));
            }catch (Exception e){
                System.out.println("[sayhi-envdecode-peb-error]-msg:"+e.getMessage());
            }
        }
        return configProperties;
    }
}
