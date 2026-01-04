package com.anshindana.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("worker")
public class NotificationScheduler {

    private static final Logger log = LoggerFactory.getLogger(NotificationScheduler.class);

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Tokyo")
    public void sendDigest() {
        log.info("Mock digest notification dispatch (worker profile).");
    }
}
