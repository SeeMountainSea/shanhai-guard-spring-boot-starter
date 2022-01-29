package com.sayrmb.guard.component;

import com.sayrmb.guard.decode.PropertyDecode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Map;
import java.util.Properties;

public class SayHiEnvironmentPostProcessor implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Properties envProperties = new Properties();
        Properties configProperties = new Properties();
        for(PropertySource<?> propertySource:environment.getPropertySources()){
            if(propertySource instanceof OriginTrackedMapPropertySource){
                Map<String,Object> env=(Map)propertySource.getSource();
                for(String key:env.keySet()){
                    String val=String.valueOf(env.get(key));
                    if(key.contains("sayhi.envdecode")){
                        envProperties.setProperty(key,val);
                    }
                    if(val.contains("envdecode::")){
                        configProperties.setProperty(key,val.replace("envdecode::",""));
                    }
                }
            }

        }
        PropertiesPropertySource propertiesPropertySource = new PropertiesPropertySource("sayhi",decode(envProperties,configProperties));
        environment.getPropertySources().addFirst(propertiesPropertySource);
    }

    public Properties decode(Properties envProperties,Properties configProperties){
        try{
            if(envProperties.containsKey("sayhi.envdecode.className")){
                PropertyDecode propertyDecode=Class.forName(envProperties.getProperty("sayhi.envdecode.className")).asSubclass(PropertyDecode.class).newInstance();;
                System.out.println("[sayhi-envdecode-init-success]-decode:"+envProperties.getProperty("sayhi.envdecode.className"));
                return propertyDecode.getProperty(envProperties,configProperties);
            }
            if(envProperties.containsKey("sayhi.envdecode.market.algorithm")){
                String pkName="com.sayrmb.guard.decode.market."+envProperties.getProperty("sayhi.envdecode.market.algorithm");
                PropertyDecode propertyDecode=Class.forName(pkName).asSubclass(PropertyDecode.class).newInstance();;
                System.out.println("[sayhi-envdecode-init-success]-decode:"+envProperties.getProperty("sayhi.envdecode.market.algorithm"));
                return propertyDecode.getProperty(envProperties,configProperties);
            }
        }catch (Exception e){
            System.out.println("[sayhi-envdecode-init-error]-msg:"+e.getMessage());
        }
        return configProperties;
    }
}
