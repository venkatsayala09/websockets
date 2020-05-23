package com.demo.ws.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/user");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/gs-guide-websocket").setAllowedOrigins("*")
                .setHandshakeHandler(new CustomHandshakeHandler()).addInterceptors(new HttpHandshakeInterceptor());
        registry.addEndpoint("/gs-guide-websocket").setAllowedOrigins("*")
                .setHandshakeHandler(new CustomHandshakeHandler()).addInterceptors(new HttpHandshakeInterceptor())
                .withSockJS();
    }

    @EventListener
    public void onSocketConnected(SessionConnectedEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        log.info("WebSocket Session Connected: {}", event.getMessage());
        log.info("Connect event [sessionId: {} ]", sha.getSessionId());
    }

    @EventListener
    public void onSocketDisconnected(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        log.info("WebSocket Session Disconnected: {}", event.getMessage());
        log.info("DisConnect event [sessionId: {}]", sha.getSessionId());
    }

}
