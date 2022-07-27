package com.wangshanhai.guard.component;

import com.wangshanhai.guard.decode.PropertyDecode;
import com.wangshanhai.guard.utils.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import java.util.Map;
import java.util.Properties;

public class IEnvironmentPostProcessor implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Properties envProperties = new Properties();
        Properties configProperties = new Properties();
        for(PropertySource<?> propertySource:environment.getPropertySources()){
            if (propertySource instanceof SimpleCommandLinePropertySource) {
                SimpleCommandLinePropertySource source = (SimpleCommandLinePropertySource) propertySource;
                String[] keys=source.getPropertyNames();
                for(String key:keys){
                    String val=source.getProperty(key);
                    if(key.contains("shanhai.envdecode")){
                        envProperties.setProperty(key,val);
                    }
                    if(val.contains("envdecode::")){
                        configProperties.setProperty(key,val.replace("envdecode::",""));
                    }
                }
            }else{
                Object envTmp=propertySource.getSource();
                if(envTmp instanceof Map){
                    Map<String,Object> env=(Map)envTmp;
                    for(String key:env.keySet()){
                        String val=String.valueOf(env.get(key));
                        if(key.contains("shanhai.envdecode")){
                            envProperties.setProperty(key,val);
                        }
                        if(val.contains("envdecode::")){
                            configProperties.setProperty(key,val.replace("envdecode::",""));
                        }
                    }
                }
            }
        }
        PropertiesPropertySource propertiesPropertySource = new PropertiesPropertySource("shanhai",decode(envProperties,configProperties));
        environment.getPropertySources().addFirst(propertiesPropertySource);
    }

    public Properties decode(Properties envProperties,Properties configProperties){
        try{
            if(envProperties.containsKey("shanhai.envdecode.className")){
                PropertyDecode propertyDecode=Class.forName(envProperties.getProperty("shanhai.envdecode.className")).asSubclass(PropertyDecode.class).newInstance();;
                Logger.info("[shanhai-envdecode-init-success]-decode:"+envProperties.getProperty("shanhai.envdecode.className"));
                return propertyDecode.getProperty(envProperties,configProperties);
            }
            if(envProperties.containsKey("shanhai.envdecode.market.algorithm")){
                String pkName="com.wangshanhai.guard.decode.market."+envProperties.getProperty("shanhai.envdecode.market.algorithm");
                PropertyDecode propertyDecode=Class.forName(pkName).asSubclass(PropertyDecode.class).newInstance();;
                Logger.info("[shanhai-envdecode-init-success]-decode:"+envProperties.getProperty("shanhai.envdecode.market.algorithm"));
                return propertyDecode.getProperty(envProperties,configProperties);
            }
        }catch (Exception e){
            Logger.info("[shanhai-envdecode-init-error]-msg:"+e.getMessage());
        }
        return configProperties;
    }

}
