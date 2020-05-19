package com.example.messagingstompwebsocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.HtmlUtils;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import java.security.Principal;

@Slf4j
@Controller
public class GreetingController {
    @Autowired
    private SimpUserRegistry userRegistry;

    @Autowired
    private GreetingService greetingService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

   /* @Autowired
    private SimpMessageHeaderAccessor headerAccessor;
*/

    /*@MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay
		printConnectedUsers();
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }*/

    public void printConnectedUsers() {
        log.info("Total users connected: " + userRegistry.getUserCount());
        userRegistry.getUsers().stream().map(u -> u.getName()).forEach(System.out::println);

    }

    @MessageMapping("/hello")
    @SendToUser("/topic/greetings")
    public Greeting greeting(HelloMessage message, Principal principal/*,SimpMessageHeaderAccessor headerAccessor*/ ) throws Exception {
        log.info("Received greeting message {} from {}", message, principal.getName());
//        String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();
        //log.info("Session id is {} ", sessionId);
        printConnectedUsers();

        greetingService.addUserName(principal.getName());
        Thread.sleep(1000); // simulated delay

       // messagingTemplate.convertAndSendToUser(message.getName(), "/topic/greetings", "Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

   /* @SubscribeMapping("/topic/greetings")
    public Greeting try1() {
        System.out.println("inside TRY 1");
        return new Greeting("Hello, " + "TRY 1" + "!");
    }*/


}
