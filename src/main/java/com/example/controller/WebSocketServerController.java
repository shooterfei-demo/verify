package com.example.controller;


import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Calendar;

@ServerEndpoint("/ws/test")
@Component
public class WebSocketServerController {
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("成功连接: " +  Calendar.getInstance().getTimeInMillis());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println(message);
        session.getAsyncRemote().sendText(message);
    }

    @OnMessage
    public void onMessage(byte[] message, Session session) {
        System.out.println(message);
    }
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println(error.getMessage());
    }

    @OnClose
    public void onCLose() {
        System.out.println("连接关闭: " + Calendar.getInstance().getTimeInMillis());
    }
}
