package com.stream.app.service;

import com.stream.app.models.Video;
import com.stream.app.repository.VideoRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoServiceImpl.class);

    @Value("${files.video}")
    String DIR;

    @Value("${files.video.hls}")
    String HLS_DIR;

    private final VideoRepository videoRepository;

    public VideoServiceImpl(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @PostConstruct
    public void createDirectoryIfNotExists() {
        File file = new File(DIR);

        if (!file.exists()) {
            file.mkdir();
            LOGGER.debug("Video Directory Created");
        } else {
            LOGGER.debug("Video Directory already created");
        }

        try {
            Files.createDirectories(Paths.get(HLS_DIR));
            LOGGER.debug("HLS Video Directory Created");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Video save(Video video, MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            String contentType = file.getContentType();
            InputStream inputStream = file.getInputStream();

            // file path
            String cleanFileName = StringUtils.cleanPath(fileName);

            // directory path
            String cleanFolderName = StringUtils.cleanPath(DIR);

            // directory path with filename
            Path path = Paths.get(cleanFolderName, cleanFileName);

            LOGGER.debug("path: {}", path);

            // copy file to the folder
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);

            // video meta data
            video.setContentType(contentType);
            video.setFilePath(path.toString());

            // save meta data
            videoRepository.save(video);

            // processing video
            processVideo(video.getVideoId());

            // TODO: delete actual video file and database entry if exception

            return video;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Video get(String videoId) {
        return videoRepository.findById(videoId).orElseThrow(() -> new RuntimeException("Video not found"));
    }

    @Override
    public Video getByTitle(String title) {
        return null;
    }

    @Override
    public List<Video> getAll() {
        return videoRepository.findAll();
    }

    @Override
    public void processVideo(String videoId) {

        // TODO: save in different resolutions

        Video video = this.get(videoId);
        String filePath = video.getFilePath();

        Path videoPath = Paths.get(filePath);

        try {
            Path outputPath = Paths.get(HLS_DIR, videoId);

            Files.createDirectories(outputPath);

            String ffmpegCmd = String.format(
                    "ffmpeg -i \"%s\" -c:v libx264 -c:a aac -strict -2 -f hls -hls_time 10 -hls_list_size 0 -hls_segment_filename \"%s/segment_%%3d.ts\"  \"%s/master.m3u8\" ",
                    videoPath, outputPath, outputPath
            );

            LOGGER.debug("ffmpegCmd: {}", ffmpegCmd);

            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", ffmpegCmd);
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            int exit = process.waitFor();
            if (exit != 0) {
                throw new RuntimeException("Video processing failed!!");
            }
            
        } catch (IOException ex) {
            throw new RuntimeException("Video processing failed!!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
