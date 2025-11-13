package com.wangshanhai.guard.interceptor;

import com.wangshanhai.guard.config.AssaultSimulatorConfig;
import com.wangshanhai.guard.utils.AssaultSimulator;
import com.wangshanhai.guard.utils.ShanHaiGuardErrorCode;
import com.wangshanhai.guard.utils.ShanHaiGuardException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.security.SecureRandom;

/**
 * 混沌模拟组件
 *
 * @author Fly.Sky
 */
@Configuration
@EnableConfigurationProperties(AssaultSimulatorConfig.class)
@AutoConfigureAfter(WebMvcConfigurationSupport.class)
@ConditionalOnProperty(
        prefix = "shanhai.assaultsimulator",
        name = "enable",
        havingValue = "true"
)
public class AssaultSimulatorInterceptor implements HandlerInterceptor {
    private final SecureRandom secureRandom = new SecureRandom();
    @Autowired
    private AssaultSimulatorConfig assaultSimulatorConfig;
    private AssaultSimulator apiRequestSimulator;
    private AssaultSimulator apiResponseSimulator;

    @PostConstruct
    public void init() {
        this.apiRequestSimulator=new AssaultSimulator(assaultSimulatorConfig.getSleepBatchSize(),assaultSimulatorConfig.getSleepMinTimes(),assaultSimulatorConfig.getSleepMaxTimes());
        this.apiResponseSimulator=new AssaultSimulator(assaultSimulatorConfig.getExceptionBatchSize(),assaultSimulatorConfig.getExceptionMinTimes(),assaultSimulatorConfig.getExceptionMaxTimes());
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(assaultSimulatorConfig.getEnableSimulatorSleep()){
            if(apiRequestSimulator.makeRequest()){
                checkAndUpdate(apiRequestSimulator,assaultSimulatorConfig,1);
                // 计算范围大小
                int range = assaultSimulatorConfig.getSleepTimeMax() -  assaultSimulatorConfig.getSleepTimeMin() + 1;
                // 生成随机数
                int randomNumber = secureRandom.nextInt(range) +  assaultSimulatorConfig.getSleepTimeMin();
                Thread.sleep(randomNumber);
            }
        }
        if(assaultSimulatorConfig.getEnableSimulatorException()){
            if(apiResponseSimulator.makeRequest()){
                checkAndUpdate(apiResponseSimulator,assaultSimulatorConfig,2);
                throw new ShanHaiGuardException(ShanHaiGuardErrorCode.ASSAULT_SIMULATOR_ERROR,"服务异常");
            }
        }
        return true;
    }

    private void checkAndUpdate(AssaultSimulator simulator,AssaultSimulatorConfig assaultSimulatorConfig,int type){
        int batchSize=0;
        int minExceptions=1;
        int maxExceptions=2;
        if (type == 1) {
            batchSize = assaultSimulatorConfig.getSleepBatchSize();
            minExceptions = assaultSimulatorConfig.getSleepMinTimes();
            maxExceptions = assaultSimulatorConfig.getSleepMaxTimes();
            String config = String.format("%d|%d-%d", batchSize, minExceptions, maxExceptions);
            String source = simulator.getConfiguration();
            if (!config.equals(source)) {
                apiRequestSimulator = new AssaultSimulator(batchSize, minExceptions, maxExceptions);
            }
        } else {
            batchSize = assaultSimulatorConfig.getExceptionBatchSize();
            minExceptions = assaultSimulatorConfig.getExceptionMinTimes();
            maxExceptions = assaultSimulatorConfig.getExceptionMaxTimes();
            String configDefault = String.format("%d|%d-%d", batchSize, minExceptions, maxExceptions);
            String sourceDefault = simulator.getConfiguration();
            if (!configDefault.equals(sourceDefault)) {
                apiResponseSimulator = new AssaultSimulator(batchSize, minExceptions, maxExceptions);
            }
        }
    }
}
