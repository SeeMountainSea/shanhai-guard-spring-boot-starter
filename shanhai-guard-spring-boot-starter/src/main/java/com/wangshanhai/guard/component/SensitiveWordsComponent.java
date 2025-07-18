package com.wangshanhai.guard.component;

import com.wangshanhai.guard.config.WordsSensitiveConfig;
import com.wangshanhai.guard.sensitive.Finder;
import com.wangshanhai.guard.service.SensitiveWordsDictService;
import com.wangshanhai.guard.service.impl.DefaultSensitiveWordsDictServiceImpl;
import com.wangshanhai.guard.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 敏感词检测组件
 * @author Shmily
 */
@Configuration
@EnableConfigurationProperties(WordsSensitiveConfig.class)
@ConditionalOnProperty(
        prefix = "shanhai.sensitivewords",
        name = "enable",
        havingValue = "true")
public class SensitiveWordsComponent {

    @Autowired
    private WordsSensitiveConfig wordsSensitiveConfig;
    @Autowired
    private SensitiveWordsDictService sensitiveWordsDictService;
    @PostConstruct
    public void init() {
        // 定义敏感词库 后续可以对接数据库
        String[] sensitiveWords = wordsSensitiveConfig.getSensitiveWords().split(",");
        // 设置敏感词库
        Finder.addSensitiveWords(sensitiveWords);
        if(wordsSensitiveConfig.getTaskEnable()){
            if(sensitiveWordsDictService instanceof DefaultSensitiveWordsDictServiceImpl){
                Logger.error("[ShanhaiGuard-WordsSensitive]-alert:{}","请实现获取字典的方法后再开启字典刷新选项!");
            }else{
                ScheduledExecutorService service = Executors
                        .newSingleThreadScheduledExecutor();
                // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
                service.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        Set<String> dicts=sensitiveWordsDictService.loadDict();
                        if(dicts!=null&&dicts.size()>0){
                            Finder.clearSensitiveWords();
                            Finder.addSensitiveWords(dicts.toArray(new String[0]));
                            Logger.info("[ShanhaiGuard-WordsSensitive]-msg:{}","敏感词字典刷新成功!");
                        }
                    }
                }, 5, wordsSensitiveConfig.getTaskIntevalPeriod(), TimeUnit.SECONDS);
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public SensitiveWordsDictService generateDefaultSensitiveWordsDictService() {
        return new DefaultSensitiveWordsDictServiceImpl();
    };
}
