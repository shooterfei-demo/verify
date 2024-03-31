package com.example.http2_verify;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class StreamTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        PipedInputStream pipedInputStream = new PipedInputStream();
        PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0,
                2,
                10,
                TimeUnit.SECONDS,
                new SynchronousQueue<>()
        );

        threadPoolExecutor.execute(() -> {
            byte[] bytes = new byte[1024];
            int readLen = 0;
            while (true) {
                try {
                    if (!((readLen = pipedInputStream.read(bytes)) != -1)) break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (readLen > 0) {
                    log.info("read: {}", new String(Arrays.copyOf(bytes, readLen)));
                }
            }
            log.info("read end");
        });
        threadPoolExecutor.execute(() -> {
            byte[] bytes = new byte[1024];
            int readLen = 0;
            while (true) {
                try {
                    if (!((readLen = pipedInputStream.read(bytes)) != -1)) break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (readLen > 0) {
                    log.info("read: {}", new String(Arrays.copyOf(bytes, readLen)));
                }
            }
            log.info("read end");
        });


        for (int i = 0; i < 20; i++) {
            Thread.sleep(1000);
            pipedOutputStream.write("hello world".getBytes("utf-8"));
            log.info("write: {}", "hello world");
        }
        log.info("write end");
        pipedOutputStream.close();
        threadPoolExecutor.execute(()->{
            log.info("test");
        });

//        Thread.sleep(5);
//        threadPoolExecutor.shutdown();
//        pipedInputStream.close();

    }
}
