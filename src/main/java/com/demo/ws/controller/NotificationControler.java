package com.demo.ws.controller;

import com.demo.ws.model.Ack;
import com.demo.ws.model.Notification;
import com.demo.ws.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;

@Slf4j
@Controller
public class NotificationControler {
    @Autowired
    private SimpUserRegistry userRegistry;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/notification")
    @SendToUser("/topic/notifications")
    public Notification notification(Ack message, Principal principal/*,SimpMessageHeaderAccessor headerAccessor*/) throws Exception {
        log.info("Received notification message {} from {}", message, principal.getName());
        Thread.sleep(1000); // simulated delay
        return new Notification("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }


}
