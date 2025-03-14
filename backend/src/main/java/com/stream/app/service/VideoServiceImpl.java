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

    private VideoRepository videoRepository;

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
            return videoRepository.save(video);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Video get(String videoId) {
        return null;
    }

    @Override
    public Video getByTitle(String title) {
        return null;
    }

    @Override
    public List<Video> getAll() {
        return List.of();
    }
}
