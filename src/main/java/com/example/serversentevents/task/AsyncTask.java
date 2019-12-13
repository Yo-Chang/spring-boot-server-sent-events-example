package com.example.serversentevents.task;

import com.example.serversentevents.event.NotificationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AsyncTask {

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Async
    public void publishNotificationEvent(String sessionId) throws InterruptedException {
        for (int i = 0; i < 3600; i++) {
            Thread.sleep(1000);
            eventPublisher.publishEvent(new NotificationEvent(sessionId, new Date()));
        }
    }

}
