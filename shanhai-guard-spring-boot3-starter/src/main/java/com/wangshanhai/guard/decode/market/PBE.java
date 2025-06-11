package com.wangshanhai.guard.decode.market;

import com.wangshanhai.guard.decode.PropertyDecode;
import com.wangshanhai.guard.utils.PBEUtils;

import java.util.Properties;

/**
 * PEB解密算法
 * @author Shmily
 */
public class PBE extends PropertyDecode {
    @Override
    public Properties getProperty(Properties envProperties, Properties configProperties) {
        String salt=envProperties.getProperty("shanhai.envdecode.market.pebSalt");
        String passwd=envProperties.getProperty("shanhai.envdecode.market.pebPasswd");
        for(Object key:configProperties.keySet()){
            try{
                String k=String.valueOf(key);
                String val=configProperties.getProperty(k);
                configProperties.setProperty(k,PBEUtils.decrypt(val,passwd,salt));
            }catch (Exception e){
                System.out.println("[shanhai-envdecode-peb-error]-msg:"+e.getMessage());
            }
        }
        return configProperties;
    }
}
