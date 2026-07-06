package com.wangshanhai.guard.component;

import com.wangshanhai.guard.config.SystemPreheatConfig;
import com.wangshanhai.guard.systempreheat.SystemPreheatCronScheduler;
import com.wangshanhai.guard.systempreheat.SytemPreheatLifecycle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 系统资源预热组件
 *
 * @author Fly.Sky
 */
@Slf4j
@Component
@ConditionalOnProperty(
        prefix = "shanhai.systempreheat",
        name = {"enable"},
        havingValue = "true"
)
@EnableConfigurationProperties(SystemPreheatConfig.class)
public class SystemPreheatComponent {
    /**
     * 装配独立 Cron 调度器
     * <p>
     * 使用专用的守护线程池 ("Shanhai-SystemPreheat-Cron-Trigger") 监听 Cron 触发时间。
     * 不使用 Spring 的 @Scheduled 注解，确保不干扰应用原有的定时任务调度。
     * </p>
     */
    @Bean
    public SystemPreheatCronScheduler chaosCronScheduler() {
        return new SystemPreheatCronScheduler();
    }

    /**
     * 装配核心压测状态机引擎
     * <p>
     * 接收配置属性和独立调度器，在应用启动完成后自动注册 Cron 任务。
     * 内部使用 "Shanhai-State-Machine" 线程池处理长时间的拉升/保持阻塞逻辑，防止阻塞 Cron 触发线程。
     * </p>
     */
    @Bean
    public SytemPreheatLifecycle chaosLifecycleService(SystemPreheatConfig properties, SystemPreheatCronScheduler scheduler) {
        return new SytemPreheatLifecycle(properties, scheduler);
    }
}
