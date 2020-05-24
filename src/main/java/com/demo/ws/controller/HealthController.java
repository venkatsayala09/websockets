package com.demo.ws.controller;

import com.demo.ws.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Date;

@Slf4j
@Controller
public class HealthController {
    @Autowired
    private NotificationService notificationService;

    //TODO - No client has subscribed to this topic. Add code in UI and integration test
    @MessageMapping("/health/{serviceName}/")
    @SendTo("/health/{serviceName}/")
    public String status(SimpMessageHeaderAccessor headerAccessor, @DestinationVariable String name) {
        return new StringBuilder().append("Service").append(name)
                .append("is up and running at ").append(new Date().toString()).toString();
    }

}
