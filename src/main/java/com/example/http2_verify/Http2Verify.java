package com.example.http2_verify;

import jdk.nashorn.internal.runtime.regexp.joni.constants.Arguments;
import okhttp3.*;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Http2Verify {

    public static Map<String, String> parseArgs(String[] args) {
        HashMap<String, String> argv = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                String key = args[i].substring(1);
                i++;
                try {
                    String value = args[i];
//                    System.out.println(key + " == " + value);
                    argv.put(key, value);
                } catch (Exception e) {
                    System.out.println("输入参数格式异常");
                    System.exit(2);
                }
            }
        }
        return argv;
    }

    public static void main(String[] args) throws IOException {
//        String url = "http://172.31.108.14:31763/aiems/media/create";
//        String url = "https://localhost:443";
//        String url = "http://172.31.108.14:31763/aiems/test";
        Map<String, String> argv = parseArgs(args);

//        String url = "http://localhost:8761/aiems/test";
        String url = "http://localhost:8762/aiems/test";
        if (argv.get("url") != null) {
            url = argv.get("url");
        }

        int timeout = 120;
        if (argv.get("tm") != null) {
            timeout = Integer.parseInt(argv.get("tm"));
        }

        int pingInterval = 60;
        if (argv.get("pi") != null) {
            pingInterval = Integer.parseInt(argv.get("pi"));
        }

        List<Protocol> protocols = new ArrayList<>();
        protocols.add(Protocol.H2_PRIOR_KNOWLEDGE);
        String bodyString = "{\"mediaType\":\"AUDIO\",\"sdp\":\"v=0\\r\\no=HuaweiATS9900 57320592 57320596 IN IP4 10.188.29.36\\r\\ns=SBC call\\r\\nc=IN IP4 10.193.190.9\\r\\nb=AS:2211\\r\\nb=RS:8600\\r\\nb=RR:8000\\r\\nt=0 0\\r\\nm=audio 64984 RTP/AVP 104 96\\r\\nb=AS:41\\r\\nb=RS:600\\r\\nb=RR:2000\\r\\na=rtpmap:104 AMR-WB/16000/1\\r\\na=fmtp:104 mode-change-capability=2;max-red=0\\r\\na=sendonly\\r\\na=maxptime:240\\r\\na=ptime:20\\r\\na=rtpmap:96 telephone-event/16000\\r\\na=fmtp:96 0-15\\r\\n\"}";
        ConnectionPool connectionPool = new ConnectionPool(1, 60, TimeUnit.SECONDS);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .sslSocketFactory(MySSLSocketClient.getSSLSocketFactory(), MySSLSocketClient.X509)
//                .hostnameVerifier(MySSLSocketClient.getHostnameVerifier())
                .protocols(protocols)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .pingInterval(pingInterval, TimeUnit.SECONDS)
//                .connectionPool(connectionPool)
                .build();

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), bodyString);

        Request request = new Request.Builder()
                .url(url)
                .get()
//                .post(body)
                .build();

//        okHttpClient.socketFactory().createSocket().getChannel().write()
        Response resp = okHttpClient.newCall(request).execute();

        String string = resp.body().string();
        System.out.println(string);
    }
}
