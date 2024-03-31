package com.example.iflycode_verify;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class WavToMp3Converter {

    public static void main(String[] args) {
        String inputFilePath = "D:\\data\\tmp\\zhangsan.wav";
        String outputFilePath = "D:\\data\\tmp\\zhangsan.mp3";
        convertWavToMp3(inputFilePath, outputFilePath);
    }

    public static void convertWavToMp3(String inputFilePath, String outputFilePath) {
        String command = "D:\\tools\\ffmpeg-win64\\bin\\ffmpeg -i " + inputFilePath + " " + outputFilePath;
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("conversion failed with code " + exitCode);
            }
            System.out.println("Conversion completed successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
