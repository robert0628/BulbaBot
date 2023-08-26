package com.belka.audio.services;

import com.belka.audio.entityes.AudioEntity;
import com.belka.audio.repositoryes.AudioRepository;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Voice;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class AudioServiceImpl implements AudioService {
    private final static String EXTENSION = ".ogg";
    private final RestTemplate restTemplate;
    private final HttpHeaders headers;
    private final AudioRepository audioRepository;
    @Value("${bot.audio.path}")
    private String pathToAudio;
    @Value("${bot.token}")
    private String token;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;

    @Autowired
    public AudioServiceImpl(RestTemplate restTemplate, HttpHeaders headers, AudioRepository audioRepository) {
        this.restTemplate = restTemplate;
        this.headers = headers;
        this.audioRepository = audioRepository;
    }

    @Override
    @Transactional
    public void saveVoice(Voice voice, Long userId) {
        LocalDate today = LocalDate.now();
        if (audioRepository.existsByDateAndUserId(today, userId)) {
            try {
                List<String> audiosIdInDB = audioRepository.getAllIdByDateAndUserId(today, userId);
                if (audiosIdInDB.size() > 1) {
                    throw new RuntimeException("something was wrong and we have two records in one day");
                }
                String audioIdInDB = audiosIdInDB.get(0);
                writeDataToDB(voice, userId);
                concatenateAudios(
                        pathToAudio + audioIdInDB + EXTENSION,
                        pathToAudio + voice.getFileId() + EXTENSION
                );
            } finally {
                deleteVoice(voice.getFileId());
            }
        } else {
            writeDataToDB(voice, userId);
        }

    }

    @Transactional
    public void writeDataToDB(Voice voice, Long userId) {
        String fileId = voice.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        byte[] downloadFile = downloadFile(getFilePath(response));
        Path filePath = Paths.get(pathToAudio, fileId + EXTENSION);
        try {
            Files.write(filePath, downloadFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        AudioEntity entity = AudioEntity.builder()
                .id(fileId)
                .date(LocalDate.now())
                .userId(userId)
                .build();
        audioRepository.save(entity);
    }

    @Override
    @Transactional
    public void deleteVoice(String fileId) {
        String pathToAudio = getPathToAudio(fileId);
        Path path = Paths.get(pathToAudio);
        try {
            boolean result = Files.deleteIfExists(path);
            if (result) {
                audioRepository.deleteById(fileId);
            } else {
                throw new RuntimeException("File deletion failed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional
    public void changeIsPrivateFlag(boolean flag, String fileId) {
        audioRepository.changeIsPrivateFlag(flag, fileId);
    }

    @Override
    public String getPathToAudio(String fileId) {
        return pathToAudio + fileId + EXTENSION;
    }

    @Transactional
    public void concatenateAudios(String firstAudio, String secondAudio) {
        Path firstAudioPath = Path.of(firstAudio);
        try (InputStream inputStream1 = Files.newInputStream(firstAudioPath);
             InputStream inputStream2 = Files.newInputStream(Path.of(secondAudio));
             InputStream concatenatedStream = new SequenceInputStream(inputStream1, inputStream2)) {
            Files.copy(concatenatedStream, firstAudioPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        HttpEntity<String> request = new HttpEntity<>(headers);
        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token,
                fileId
        );
    }

    private String getFilePath(ResponseEntity<String> response) {
        JSONObject jsonObject = new JSONObject(response.getBody());
        return String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
    }

    private byte[] downloadFile(String filePath) {
        String fullUri = fileStorageUri.replace("{token}", token)
                .replace("{filePath}", filePath);
        URL urlObj;
        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        //TODO подумать над оптимизацией
        try (InputStream is = urlObj.openStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(urlObj.toExternalForm(), e);
        }
    }
}
