package com.demo.ws.scheduler;

import com.demo.ws.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@Configuration
public class Scheduler {
    private final NotificationService notificationService;

    Scheduler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(fixedRateString = "20000", initialDelayString = "0")
    public void schedulingTask() {
        log.info("Scheduled message sent");
        try {
            notificationService.sendMessages();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
