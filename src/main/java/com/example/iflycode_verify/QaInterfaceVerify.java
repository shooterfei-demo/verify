package com.example.iflycode_verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import okio.ByteString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QaInterfaceVerify {

    private static OkHttpClient okHttpClient = new OkHttpClient();

    public static void main(String[] args) throws IOException {

        String host = "127.0.0.1";
        int port = 8081;
        String baseUrl = "";  // 接口指定前缀，通过nginx之类的转发可能会携带固定前缀
        String httpPath = "/api/v1/oa/category/list";
        String wsPath = "/api/v1/oa/doc/chat";
        String token = "testToken";
        String httpUrl = String.format("http://%s:%d%s%s", host, port, baseUrl, httpPath);
        String httpResp = httpVerify(httpUrl, token);
        System.out.println("http result ------- ");
        System.out.println(httpResp);
        String wsUrl = String.format("ws://%s:%d%s%s", host, port, baseUrl, wsPath);
        List<String> wsResp = wsVerify(wsUrl, token);
    }


    private static String httpVerify(String url, String token) throws IOException {
        Request request = new Request.Builder()
                .addHeader("token", token)
                .get()
                .url(url)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        String respStr = response.body().string();

        return respStr;
    }

    private static List<String> wsVerify(String url, String token) {
        ObjectMapper om = new ObjectMapper();
        System.out.println("ws result ------- ");

        String sendData = "{\n" +
                "    \"header\": {\n" +
                "        \"traceId\": \"140a8bb656ca4402b78b8c2f12dd7092\"\n" +
                "    },\n" +
                "    \"payload\": {\n" +
                "        \"content\": \"权益\",\n" +
                "        \"sessionId\": \"e3df64bd26d74904be5bd9c10a0a6093\",\n" +
                "        \"categoryId\": [\n" +
                "            \"65b348282b068806dade070b\"\n" +
                "        ],\n" +
                "        \"docId\": []\n" +
                "    }\n" +
                "}";
        Request request = new Request.Builder()
                .get()
                .url(url + "?token=" + token)
                .build();

        ArrayList<String> results = new ArrayList<>();

        WebSocket webSocket = okHttpClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                // 连接建立成功发送信息
                webSocket.send(sendData);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                try {
                    System.out.println(text);
                    JsonNode jsonNode = om.readTree(text);
                    if (jsonNode.get("header").get("code").asInt() != 0) {
                        webSocket.close(1001, "返回结果异常");
                    } else {
                        results.add(text);
                        if (jsonNode.get("payload").get("choices").get("status").asInt() == 2) {
                            // 最后一次会话，此时该轮会话结束
                            System.out.println("会话结束");
                            webSocket.close(1005, "");
                        }
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }


            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
            }
        });

        return results;
    }

}
