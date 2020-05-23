package com.demo.ws.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
public class SessionDisconnectedEventListener implements ApplicationListener<SessionDisconnectEvent> {


    @Autowired
    private SimpMessagingTemplate template;


    @Override
    public void onApplicationEvent(SessionDisconnectEvent sessionDisconnectEvent) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(sessionDisconnectEvent.getMessage());

        String sessionId = headerAccessor.getSessionId();

        template.convertAndSend("/user" + sessionId + "/topic/notifications", sessionId + "got disconnected");
    }

}