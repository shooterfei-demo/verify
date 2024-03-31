package com.example.websocket_verify;

import com.example.http2_verify.Http2Verify;
import okhttp3.*;
import okio.ByteString;

import java.net.Socket;
import java.util.Calendar;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class WebSocketVerify {

    public static void main(String[] args) throws InterruptedException {

        Map<String, String> argv = Http2Verify.parseArgs(args);
        String proto = "ws";
        String ip = "127.0.0.1";
        int port = 8762;
        int intervalTime = 5;
        String path = "/ws/test";

        if (argv.get("ip") != null) {
            ip = argv.get("ip");
        }
        if (argv.get("proto") != null) {
            ip = argv.get("proto");
        }
        if (argv.get("p") != null) {
            port = Integer.parseInt(argv.get("p"));
        }
        if (argv.get("path") != null) {
            path = argv.get("path");
        }
        if (argv.get("pi") != null) {
            intervalTime = Integer.parseInt(argv.get("pi"));
        }
        System.out.println("current ip: " + ip);
        System.out.println("current port: " + port);
        System.out.println("current path: " + path);
        System.out.println("current interval: " + intervalTime);

        String url = proto + "://" + ip + ":" + port + path;

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .pingInterval(intervalTime, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();


        WebSocket webSocket = okHttpClient.newWebSocket(request, new WebSocketListener() {
            private long startTime;
            private long endTime;

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                startTime = Calendar.getInstance().getTimeInMillis();
                super.onOpen(webSocket, response);
                System.out.println("连接成功");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                System.out.println("接收消息: " + text);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                endTime = Calendar.getInstance().getTimeInMillis();
                super.onClosing(webSocket, code, reason);
                System.out.println("关闭中, 连接时长: " + (endTime - startTime) + "ms");
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                endTime = Calendar.getInstance().getTimeInMillis();
                super.onClosed(webSocket, code, reason);
                System.out.println("正在关闭, 连接时长: " + (endTime - startTime) + "ms");
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                System.out.println("连接异常:" + response.toString());
            }
        });


        Scanner scanner = new Scanner(System.in);

        while (true) {
            if (scanner.hasNext()) {
                String str = scanner.next();
                if ("exit".equals(str)) {
                    webSocket.close(1001, "");
                    break;
                } else {
                    webSocket.send(str);
                }
            }
            Thread.sleep(100);
        }
        scanner.close();
    }

}
