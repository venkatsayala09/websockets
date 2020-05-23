package com.demo.ws.client;

import com.demo.ws.model.Notification;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@RestController
public class WebSocketClient {


    private final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
    private int port;
    private SockJsClient sockJsClient;
    private WebSocketStompClient stompClient;

    @GetMapping("/greet")
    public void getNotification() throws Exception {

        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        this.sockJsClient = new SockJsClient(transports);
        this.stompClient = new WebSocketStompClient(sockJsClient);
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> error = new AtomicReference<>();

        StompSessionHandler handler = new SessionHandler(error) {

            @Override
            public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
                System.out.println("New session established : " + session.getSessionId());
                session.subscribe("/user/username/topic/notifications", new StompFrameHandler() {

                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return Notification.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        System.out.println("here");
                        Notification notification = (Notification) payload;
                        try {
                            System.out.println(notification.getContent());
                        } catch (Throwable t) {
                            error.set(t);
                        } finally {
                            session.disconnect();
                            latch.countDown();

                        }
                    }
                });
                try {
                    session.send("/app/notification", "username");
                } catch (Throwable t) {
                    t.printStackTrace();
                    error.set(t);
                    latch.countDown();
                }
            }
        };

        this.stompClient.connect("ws://localhost:8080/gs-guide-websocket", this.headers, handler, 8080);
        if (latch.await(3, TimeUnit.SECONDS)) {
            if (error.get() != null) {
                throw new AssertionError("", error.get());
            }
        } else {
            System.out.println("Notification not received");
        }

    }

    private class SessionHandler extends StompSessionHandlerAdapter {

        private final AtomicReference<Throwable> error;

        public SessionHandler(AtomicReference<Throwable> failure) {
            this.error = failure;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            this.error.set(new Exception(headers.toString()));
        }

        @Override
        public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) {
            this.error.set(ex);
        }

        @Override
        public void handleTransportError(StompSession session, Throwable ex) {
            this.error.set(ex);
        }
    }
}



