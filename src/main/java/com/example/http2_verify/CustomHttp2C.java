package com.example.http2_verify;

import com.example.utils.CompressUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http2.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPOutputStream;

public class CustomHttp2C {


    private static byte[] concat(byte[] a, byte[] b, int len) {
        byte[] c = new byte[a.length + len];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, len);
        return c;
    }


    private static String byte2HexString(byte[] b, int len) {
        StringBuffer sbf = new StringBuffer();
        for (int i = 0; i < len; i++) {
            sbf.append(String.format("%02X", b[i])).append("\t");
            if (i % 9 == 0 && i > 0)
                sbf.append("\r\n");
        }
        return sbf.toString();
    }

    public static void main(String[] args) throws IOException, InterruptedException, Http2Exception {
        Map<String, String> argv = Http2Verify.parseArgs(args);
        String ip = "localhost";
        int port = 8762;
        int intervalTime = 5;
//        Http2Client http2Client = new Http2Client(ip, port);

        if (argv.get("ip") != null) {
            ip = argv.get("ip");
        }
        if (argv.get("p") != null) {
            port = Integer.parseInt(argv.get("p"));
        }

        if (argv.get("pi") != null) {
            intervalTime = Integer.parseInt(argv.get("pi"));
        }
        System.out.println("current ip: " + ip);
        System.out.println("current port: " + port);
        System.out.println("current interval: " + intervalTime);
        Socket sck = new Socket(ip, port);

        String magic = "PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n";
        byte[] magicRequest = (magic).getBytes("utf-8");
        OutputStream os = sck.getOutputStream();

        InputStream ins = sck.getInputStream();


        byte[] lenFrame;
        byte typeFrame;
        byte flagFrame;
        byte[] streamIdFrame;
        byte[] dataFrame;
        byte[] commonFrame;


        lenFrame = int2LenBytes(4);
        typeFrame = 0x08;
        flagFrame = 0x00;
        streamIdFrame = int2StreamIdBytes(0);


        dataFrame = int2StreamIdBytes(16711681);
        commonFrame = aggregationCommonFrame(lenFrame, typeFrame, flagFrame, streamIdFrame);
//        byte[] updateFrame = concat(commonFrame, dataFrame, dataFrame.length);


        lenFrame = int2LenBytes(6);
        typeFrame = 0x04;
        flagFrame = 0x00;
        streamIdFrame = int2StreamIdBytes(0);


        dataFrame = new byte[]{0x00, 0x04, 0x01, 0x00, 0x00, 0x00};
        commonFrame = aggregationCommonFrame(lenFrame, typeFrame, flagFrame, streamIdFrame);
        byte[] setting1Frame = concat(commonFrame, dataFrame, dataFrame.length);


        send(os, magicRequest);
        System.out.println();
//        byte[] bytes = new byte[setting1Frame.length + updateFrame.length];
//        System.arraycopy(setting1Frame, 0, bytes, 0, setting1Frame.length);
//        System.arraycopy(updateFrame, 0, bytes, setting1Frame.length, updateFrame.length);
        send(os, setting1Frame);
//        System.out.println();
//        send(os, bytes);
        int count = 0;
        Http2Frame http2Frame = new Http2Frame();
        boolean breakFlag = false;
        while (true) {
            Thread.sleep(50);
            byte[] ack = receive(ins);
            while (ack != null && ack.length > 8) {
                ack = http2Frame.decode(ack);
                if (http2Frame.getType() == 0x04) {
                    send(os, http2Frame.encode());
                }
                if (http2Frame.getType() == 0x06) {
                    if (http2Frame.getFlags() == 0x00) {
                        http2Frame.setFlags((byte)0x01);
                    }
                    send(os, http2Frame.encode());
                    breakFlag = true;
                    break;
                }
            }
            if(breakFlag) break;
        }

        byte[] pingFrame = {0x00, 0x00, 0x08, 0x06, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01};
        send(os, pingFrame);
        while (true) {
            count++;
            Thread.sleep(500);
            if (count % (intervalTime * 2) == 0) send(os, pingFrame);
            if (ins.available() > 0) {
                byte[] receive = receive(ins);
                if (receive[4] == 0x00) break;
            }
        }
        ins.close();
        os.close();
        sck.close();


//        byte[] ack = receive(ins);
/*
        if (ack.length >= 8) {
            byte[] settingAckFrame = Arrays.copyOfRange(ack, 0, 9);
            send(os, settingAckFrame);

            System.out.println(111);
            if (ack.length > 8) {
                byte[] pingAckFrame = Arrays.copyOfRange(ack, 9, 9 + 17);
                pingAckFrame[4] = 0x01;
                send(os, pingAckFrame);
            } else {
                byte[] pingAckFrame = Arrays.copyOfRange(ack, 0, 17);
                pingAckFrame[4] = 0x01;
                send(os, pingAckFrame);
            }
        }
*/

        // header Frame 生成
/*        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writableBytes();
        DefaultHttp2HeadersEncoder headersEncoder = new DefaultHttp2HeadersEncoder();
        DefaultHttp2Headers headers = new DefaultHttp2Headers();
        headers.method("GET");
        headers.path("/aiems/test");
//        headers.authority("localhost:8762");
        headers.scheme("http");
//        headers.set("accept-encoding", "gzip");
//        headers.set("user-agent", "okhttp/3.14.9");
        headersEncoder.encodeHeaders(3, headers, byteBuf);

        dataFrame = ByteBufUtil.getBytes(byteBuf);

        lenFrame = int2LenBytes(dataFrame.length);
        typeFrame = 0x01;
        flagFrame = 0x05;
        streamIdFrame = int2StreamIdBytes(3);


        commonFrame = aggregationCommonFrame(lenFrame, typeFrame, flagFrame, streamIdFrame);
        byte[] headersFrame = concat(commonFrame, dataFrame, dataFrame.length);

        send(os, headersFrame);*/

//        int count = 0;


//        byte[] pingFrame = {0x00, 0x00, 0x08, 0x06, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01};
//        send(os, pingFrame);
//        while (true) {
//            count++;
//            Thread.sleep(500);
//            if (count % (intervalTime * 2) == 0) send(os, pingFrame);
//            if (ins.available() > 0) {
//                byte[] receive = receive(ins);
//                if (receive[4] == 0x00) break;
//            }
//        }


//
//        String oriText = ":method: GET";
//        DefaultHttp2HeadersDecoder defaultHttp2HeadersDecoder = new DefaultHttp2HeadersDecoder();
//        defaultHttp2HeadersDecoder.decodeHeaders(3, )

//        byte[] bytes = CompressUtils.compressData(oriText, "UTF-8");
//        System.out.println(byte2HexString(bytes, bytes.length));

    }


    private static void send(OutputStream os, byte[] data) throws IOException {
        os.write(data);
    }


    private static byte[] aggregationCommonFrame(byte[] lenFrame, byte typeFrame, byte flagsFrame, byte[] streamIdFrame) {
        int length = lenFrame.length + 2 + streamIdFrame.length;
        byte[] commonFrame = new byte[length];

        int edgeIndex = lenFrame.length;
        for (int i = 0; i < edgeIndex; i++) {
            commonFrame[i] = lenFrame[i];
        }

        commonFrame[edgeIndex] = typeFrame;
        commonFrame[edgeIndex + 1] = flagsFrame;

        for (int i = 0; i < streamIdFrame.length; i++) {
            commonFrame[i + edgeIndex + 2] = streamIdFrame[i];
        }
        return commonFrame;
    }

    private static void printSendData(byte[] data) {
        System.out.println("-------->");
        System.out.println(byte2HexString(data, data.length));
    }

    private static byte[] test(byte[] ori) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
            gzipOutputStream.write(ori);
            gzipOutputStream.flush();
            gzipOutputStream.finish();
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

        return byteArrayOutputStream.toByteArray();
    }

    private static byte[] receive(InputStream ins) throws IOException {
        if (ins.available() > 0) {
            int len = 0;
            byte[] temp = new byte[ins.available()];
            len = ins.read(temp);
            System.out.println("<-------");
            System.out.println(byte2HexString(temp, len));
            return temp;
        }
        return null;
    }


    public static byte[] int2LenBytes(int i) {
        byte[] targets = new byte[3];
        targets[2] = (byte) (i & 0xFF);
        targets[1] = (byte) (i >> 8 & 0xFF);
        targets[0] = (byte) (i >> 16 & 0xFF);
        return targets;
    }

    public static int byte2LengthInt(byte[] b) {
        int result = b[0] << 16 + b[1] << 8 + b[2];
        return result;
    }

    public static byte[] int2StreamIdBytes(int i) {
        byte[] targets = new byte[4];
        targets[3] = (byte) (i & 0xFF);
        targets[2] = (byte) (i >> 8 & 0xFF);
        targets[1] = (byte) (i >> 16 & 0xFF);
        targets[0] = (byte) (i >> 24 & 0xFF);
        return targets;
    }


}
