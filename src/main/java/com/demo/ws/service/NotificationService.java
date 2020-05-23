package com.demo.ws.service;

import com.demo.ws.model.Notification;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Slf4j
@Service
public class NotificationService {
    private static final String WS_MESSAGE_TRANSFER_DESTINATION = "/topic/notifications";
    private final SimpMessagingTemplate simpMessagingTemplate;
    private List<String> userNames = new ArrayList<>();

    NotificationService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public static boolean pingHost(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void sendMessages() {

        String message = pingHost("localhost", 8081, 1000) ? "Service up" : "Service down";

        for (String userName : userNames) {
            simpMessagingTemplate.convertAndSendToUser(userName, WS_MESSAGE_TRANSFER_DESTINATION,
                    new Notification("Hello " + userName + " - " + message + " at " + new Date().toString()));

        }
    }

    public void addUserName(String username) {
        userNames.add(username);
    }
}
