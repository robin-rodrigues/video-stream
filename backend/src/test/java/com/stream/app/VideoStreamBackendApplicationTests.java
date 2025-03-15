package com.stream.app;

import com.stream.app.service.VideoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VideoStreamBackendApplicationTests {

    @Autowired
    VideoService videoService;

    @Test
    void contextLoads() {
        videoService.processVideo("1a646a8e-986f-4180-a1c5-64d85c87decc");
    }
}
