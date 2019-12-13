package com.example.serversentevents.controller;

import com.example.serversentevents.task.AsyncTask;
import com.example.serversentevents.event.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
public class SseNotificationController {

    public static final Map<String, SseEmitter> SSE_MAP = new ConcurrentHashMap<>();

    @Autowired
    private AsyncTask asyncTask;

    @GetMapping("/api/login")
    public void login(HttpServletRequest request) {
        request.getSession();
    }

    @GetMapping("/api/publish")
    public void publish(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null){
            throw new Exception("Please Login First");
        }
        asyncTask.publishNotificationEvent(session.getId());
    }

    @GetMapping("/api/sse-notification")
    public SseEmitter getNewNotification(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null){
            throw new Exception("Please Login First");
        }
        String sessionId = session.getId();
//        long millis = TimeUnit.SECONDS.toMillis(20);
        SseEmitter sseEmitter = new SseEmitter(0L);

        sseEmitter.onCompletion(() -> {
            this.SSE_MAP.remove(sessionId);
        });

        sseEmitter.onTimeout(() -> {
            sseEmitter.complete();
        });

        SSE_MAP.put(sessionId, sseEmitter);

        return sseEmitter;
    }

    /**
     * 通過sessionId獲取對應的客戶端進行推送消息
     */
    @Async
    @EventListener
    public void onNotification(NotificationEvent notificationEvent) {
        SseEmitter emitter = SSE_MAP.get(notificationEvent.getSessionId());
        if (emitter != null) {
            try {
                emitter.send(notificationEvent.getContent());
            } catch (Exception e) {
                log.warn("sse send error", e);
                SSE_MAP.remove(notificationEvent.getSessionId());
            }
        }
    }
}
