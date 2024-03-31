package com.example.iflycode_verify;

import okhttp3.*;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AiMindV3Verify {

    public static void main(String[] args) throws IOException {
        String testFilePath = "D:/temp/test.docx";

        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        String mediaType = mimeTypesMap.getContentType(testFilePath);
        String url = "http://172.31.186.2:8906/doc/semantic-doc/document/file/upload";
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
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse(mimeType), file))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();
        Response response = okHttpClient.newCall(request).execute();
        String result =  response.body().string();
        System.out.println(result);

    }
}
