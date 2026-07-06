package com.wangshanhai.guard.systempreheat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.support.CronExpression;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 系统资源预热组件(调度器)
 *
 * @author Fly.Sky
 */
@Slf4j
public class SystemPreheatCronScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2, r -> {
        Thread t = new Thread(r, "Shanhai-SystemPreheat-Cron-Trigger");
        t.setDaemon(true);
        return t;
    });

    public void schedule(String cronExpr, Runnable task) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                CronExpression cron = CronExpression.parse(cronExpr);
                LocalDateTime next = cron.next(LocalDateTime.now());
                if (next != null) {
                    long delay = Duration.between(LocalDateTime.now(), next).toMillis();
                    if (delay > 0) {
                        Thread.sleep(delay);
                    }
                    task.run();
                }
            } catch (Exception e) {
                log.error("[Shanhai-SystemPreheat-Cron]-error:{}",e.getMessage());
            }
        }, 0, 1000, TimeUnit.MICROSECONDS);
    }

    @PreDestroy
    public void destroy() {
        scheduler.shutdownNow();
    }
}
