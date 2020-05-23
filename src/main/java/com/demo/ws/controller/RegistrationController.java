package com.demo.ws.controller;

import com.demo.ws.model.Notification;
import com.demo.ws.service.NotificationService;
import com.demo.ws.model.StompPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.HtmlUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class RegistrationController {
    @Autowired
    private SimpUserRegistry userRegistry;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/users")
    public List<String> printConnectedUsers() {
        log.info("Total users connected: " + userRegistry.getUserCount());
        return userRegistry.getUsers().stream().
                map(principal -> principal.getName()).collect(Collectors.toList());
    }

    @GetMapping("/services")
    public List<String> printServices() {
        return Arrays.asList(new String[]{"PhoneService"});
    }


    @MessageMapping("/register")
    @SendToUser("/topic/registrations")
    public Notification register(StompPrincipal principal, SimpMessageHeaderAccessor headerAccessor) throws Exception {
        log.info("Received registration request from {}", principal.getName());
        String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();
        log.info("Session id is {} ", sessionId);
        notificationService.addUserName(principal.getName());
        Thread.sleep(1000);
        return new Notification("Hello, " + HtmlUtils.htmlEscape(principal.getName()) + "!");
    }

    @MessageMapping("/health/{serviceName}/")
    @SendTo("/health/{serviceName}/")
    public String status(SimpMessageHeaderAccessor headerAccessor, @DestinationVariable String name) {
        return new StringBuilder().append("Service").append(name)
                .append("is up and running at ").append(new Date().toString()).toString();
    }


}
