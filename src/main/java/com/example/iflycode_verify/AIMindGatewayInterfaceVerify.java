package com.example.iflycode_verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AIMindGatewayInterfaceVerify {


    public static void main(String[] args) throws IOException {
//        String testFilePath = "D:/temp/test.docx";
        String testFilePath = "D:/temp/test.txt";
//        String url = "http://172.31.205.47:30800/files/upload";
        String url = "http://172.31.186.2:8906/doc/semantic-doc/document/file/upload";
        String appId = "zhanghui";
        String appKey = "cee79d7f3465a24048cbcb0f40777134";

        String appName = url.split("/")[3];
        if (appName.length() < 24) {
            appName += String.format("%0"+ (24 - appName.length())+"d", 0);
        }
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");


        String csid = appId + appName + uuid;

        Map<String, String> xServerParamObjectPrototype = new HashMap<>();
        xServerParamObjectPrototype.put("appid", appId);
        xServerParamObjectPrototype.put("csid", csid);

        ObjectMapper objectMapper = new ObjectMapper();
        String xServerParamObjectPrototypeStr = objectMapper.writeValueAsString(xServerParamObjectPrototype);



        String xCurTime = String.valueOf(Calendar.getInstance().getTimeInMillis() / 1000);
        String xServerParam = Base64.getEncoder().encodeToString(xServerParamObjectPrototypeStr.getBytes(StandardCharsets.UTF_8));


        String xCheckSum = HexUtils.toHexString(DigestUtils.md5Digest((appKey + xCurTime + xServerParam).getBytes(StandardCharsets.UTF_8)));


        File file = new File(testFilePath);

        String[] nameSplit = file.getName().split("\\.");
        String fileSuffix = nameSplit[nameSplit.length - 1];
        Map<String, String> mimeTypeMap = new HashMap<>();
        mimeTypeMap.put("doc", "application/msword");
        mimeTypeMap.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        mimeTypeMap.put("pdf", "application/pdf");
        mimeTypeMap.put("xls", "application/vnd.ms-excel");
        mimeTypeMap.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        mimeTypeMap.put("html", "text/html");
        mimeTypeMap.put("txt", "text/plain");

        String mimeType = "application/octet-stream";
        if (mimeTypeMap.get(fileSuffix) != null) {
            mimeType = mimeTypeMap.get(fileSuffix);
        }


        MultipartBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse(mimeType), file))
                .build();

        Request request = new Request.Builder()
                .url(url)
//                .addHeader("appKey", appKey)
//                .addHeader("X-Server-Param", xServerParam)
//                .addHeader("X-CurTime", xCurTime)
//                .addHeader("X-CheckSum", xCheckSum)
                .post(requestBody)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        Response response = okHttpClient.newCall(request).execute();
        String result =  response.body().string();
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(result)));

    }
}
