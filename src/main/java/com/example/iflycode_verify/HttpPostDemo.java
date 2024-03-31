package com.example.iflycode_verify;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.util.Base64Util;
import org.json.JSONObject;

public class HttpPostDemo {
    public static void main(String[] args) throws Exception {
        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT")); // 设置时区为GMT
        String date = sdf.format(cd.getTime());
        String host = "itrans.xfyun.cn";
        String appid = "7d89783f";
        String api_key = "6376f435bfd3ad7238a2ec88076317f2";
        String apiSecret = "OGZkYzhjNGNmZmZlZTkxNjU0MTBhNjM5";
        String content = "今天天气很好，适合出去玩。";
        String res = httpPost(host, appid, api_key, apiSecret, content, date);
        System.out.println(res);
    }

    public static String httpPost(String host, String appid, String api_key, String apiSecret, String content,String date) throws Exception {
        String url = "https://" + host + "/v2/its";
        JSONObject data = new JSONObject();
        data.put("common", new JSONObject().put("app_id", appid));
        data.put("business", new JSONObject().put("from", "cn").put("to", "en"));
        data.put("data", new JSONObject().put("text", Base64Util.encode(content)));
        String digest = Base64.getEncoder().encodeToString(sha256(data.toString()));
        String signatureOrigin = String.format("host: %s\ndate: %s\nPOST /v2/its HTTP/1.1\ndigest: %s", host, date, digest);
        String signature = Base64.getEncoder().encodeToString(hmacSha256(signatureOrigin, apiSecret));
        return sendPostRequest(url, data, digest, signature, api_key, host, date);
    }

    private static String sendPostRequest(String url, JSONObject data, String digest, String signature, String api_key, String host, String date) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");

        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json,version=1.0");
        con.setRequestProperty("Host", host);
        con.setRequestProperty("Method", "POST");
        con.setRequestProperty("Date", date);
        con.setRequestProperty("Digest", digest);
        String authorization = String.format("api_key=\"%s\", algorithm=\"hmac-sha256\", headers=\"host date request-line digest\", signature=\"%s\"", api_key, signature);
        con.setRequestProperty("Authorization", authorization);


        con.setDoOutput(true);
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            bos.write(data.toString().getBytes(StandardCharsets.UTF_8));
            con.getOutputStream().write(bos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            con.disconnect();
        }
        return readResponse(con);
    }

    private static String readResponse(HttpURLConnection con) throws IOException {
        InputStream is = null;
        try {
            is = con.getInputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((len = is.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            return new String(bos.toByteArray(), StandardCharsets.UTF_8);
        } finally {
            if (is != null) {
                is.close();
            }
            con.disconnect();
        }
    }

    private static byte[] hmacSha256(String message, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
//        Mac mac = Mac.getInstance("HmacSHA256");
//        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8.name());
//        mac.init(secretKeySpec);
//        byte[] digest = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));

        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8.name());
        try {
            mac.init(keySpec);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        byte[] digest = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return digest;
    }



    public static byte[] sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return hash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("无法找到SHA-256算法", e);
        }
    }
}
