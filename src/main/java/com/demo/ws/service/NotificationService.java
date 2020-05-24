package com.demo.ws.service;

import com.demo.ws.model.Notification;
import com.demo.ws.model.Registration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Slf4j
@Service
public class NotificationService {
    private static final String WS_MESSAGE_TRANSFER_DESTINATION = "/topic/notifications";
    private final SimpMessagingTemplate simpMessagingTemplate;
    private List<String> userNames = new ArrayList<>();

    private Map<String, String> services = new HashMap<>();

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

    public void sendMessages() throws InterruptedException {
        URL url = null;
        String message = null;
        for (String serviceName : services.keySet()) {
            try {
                url = new URL(services.get(serviceName));
                message = pingHost(url.getHost(), url.getPort(), 1000) ? serviceName + " is up " : serviceName + " is down";
            } catch (Exception e) {
                e.printStackTrace();
                message = "Failed to get the status of service " + serviceName;
            }
            for (String userName : userNames) {
                simpMessagingTemplate.convertAndSendToUser(userName, WS_MESSAGE_TRANSFER_DESTINATION,
                        new Notification(message + " at " + new Date().toString()));
                Thread.sleep(2000);
            }
        }
    }

    public void addUserName(String username) {
        userNames.add(username);
    }

    public void register(Registration service) {
        services.put(service.getName(), service.getUrl());
    }

    public List listServices() {
        return services.keySet().stream().collect(Collectors.toCollection(ArrayList::new));
    }
}
