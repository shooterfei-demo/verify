package com.example.http2_verify;

import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Http2Client {
    // socket 连接
    private Socket socket;
    // zhuan
    private boolean ready;

    public Http2Client(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        this.defaultHandler();
    }

    private void defaultHandler() throws IOException {
        new Thread(){
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            @SneakyThrows
            @Override
            public void run() {
                int retryCount = 0;
                boolean initFlag = false;
                while (true) {
                    if (!socket.isClosed() && !initFlag) {
                        String magic = "PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n";
                        outputStream.write(magic.getBytes("utf-8"));
                        byte[] payload = new byte[]{0x00, 0x04, 0x01, 0x00, 0x00, 0x00, 0x00};
                        Http2Frame http2Frame = new Http2Frame();
                        http2Frame.setType((byte)0x04);
                        http2Frame.setPayload(payload);
//                        http2Frame.setR();
                    }
                    Thread.sleep(20);
                    System.out.println(111);
                }
            }
        }.start();
    }

    public void ping() {

    }

}
