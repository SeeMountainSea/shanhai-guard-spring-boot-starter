package com.wangshanhai.guard.interceptor;

import com.wangshanhai.guard.config.AssaultSimulatorConfig;
import com.wangshanhai.guard.utils.AssaultSimulator;
import com.wangshanhai.guard.utils.ShanHaiGuardErrorCode;
import com.wangshanhai.guard.utils.ShanHaiGuardException;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.SecureRandom;

/**
 * 混沌模拟组件
 *
 * @author Fly.Sky
 */
public class AssaultSimulatorInterceptor implements HandlerInterceptor {
    private AssaultSimulatorConfig assaultSimulatorConfig;
    private AssaultSimulator apiRequestSimulator=new AssaultSimulator();
    private AssaultSimulator apiResponseSimulator=new AssaultSimulator();
    private final SecureRandom secureRandom = new SecureRandom();
    public AssaultSimulatorInterceptor(AssaultSimulatorConfig assaultSimulatorConfig) {
        this.assaultSimulatorConfig=assaultSimulatorConfig;
        if(assaultSimulatorConfig.getEnableSimulatorSleep()){
            this.apiRequestSimulator=new AssaultSimulator(assaultSimulatorConfig.getSleepBatchSize(),
                    assaultSimulatorConfig.getSleepMinTimes(),assaultSimulatorConfig.getSleepMaxTimes());
        }
        if(assaultSimulatorConfig.getEnableSimulatorException()){
            this.apiRequestSimulator=new AssaultSimulator(assaultSimulatorConfig.getExceptionBatchSize(),
                    assaultSimulatorConfig.getExceptionMinTimes(),assaultSimulatorConfig.getExceptionMaxTimes());
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(assaultSimulatorConfig.getEnableSimulatorSleep()){
            if(apiRequestSimulator.makeRequest()){
                // 计算范围大小
                int range = assaultSimulatorConfig.getSleepTimeMax() -  assaultSimulatorConfig.getSleepTimeMin() + 1;
                // 生成随机数
                int randomNumber = secureRandom.nextInt(range) +  assaultSimulatorConfig.getSleepTimeMin();
                Thread.sleep(randomNumber);
            }
        }
        if(assaultSimulatorConfig.getEnableSimulatorException()){
            if(apiResponseSimulator.makeRequest()){
                throw new ShanHaiGuardException(ShanHaiGuardErrorCode.ASSAULT_SIMULATOR_ERROR,"服务异常");
            }
        }
        return true;
    }
}
