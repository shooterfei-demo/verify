package com.example.http2_verify;

import java.io.IOException;
import java.net.Socket;

public class Test {
    public static void main(String[] args) throws IOException {
        String ip = "localhost";
        int port = 8762;

        Socket sck = new Socket(ip, port);
        System.out.println(123);
        new Http2Client(ip, port);
    }
}
