package com.example.enums;

import okhttp3.OkHttpClient;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public enum OkHttpClientObject {
    CLIENT;
    private OkHttpClient clientInstance;
    private Integer connectTimeout_time = 30;

    private Integer writeTimeout_time = 30;

    private Integer readTimeout_time = 30;

    OkHttpClientObject() {
        clientInstance = new OkHttpClient.Builder()
                .connectTimeout(connectTimeout_time, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout_time, TimeUnit.SECONDS)
                .readTimeout(readTimeout_time, TimeUnit.SECONDS)
                .build();
    }


    public OkHttpClient getClientInstance() {
        return clientInstance;
    }


    private static double calculateMagnitude(Map<String, Integer> vector) {
       double sum = 0;
        for (Integer value : vector.values()) {
            sum += Math.pow(value, 2);
        }
        return Math.sqrt(sum);
    }
}
