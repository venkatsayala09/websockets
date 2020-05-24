package com.demo.ws.controller;

import com.demo.ws.model.Registration;
import com.demo.ws.model.StompPrincipal;
import com.demo.ws.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController

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
        return notificationService.listServices();
    }


    //TODO - add integration test
    @MessageMapping("/register")
    @SendTo("/topic/registrations")
    public Registration register(Registration registration, StompPrincipal principal, SimpMessageHeaderAccessor headerAccessor) throws Exception {
        log.info("Received registration request from {}", principal.getName());
        String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();
        log.info("Session id is {} ", sessionId);
        notificationService.addUserName(principal.getName());
        notificationService.register(registration);
        Thread.sleep(1000);
        return registration;
        //return new Ack("Registration Successful ->" + registration.toString() );
    }


}
