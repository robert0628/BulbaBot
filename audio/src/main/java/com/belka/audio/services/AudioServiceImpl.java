package com.belka.audio.services;

import com.belka.audio.entityes.AudioEntity;
import com.belka.audio.models.NotListened;
import com.belka.audio.repositoryes.AudioRepository;
import com.belka.audio.repositoryes.NotListenedRepository;
import com.belka.core.converter.ConverterService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Voice;

import java.io.ByteArrayOutputStream;
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
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class AudioServiceImpl implements AudioService {
    final static String AUDIO_EXTENSION = ".ogg";
    private final RestTemplate restTemplate;
    private final HttpHeaders headers;
    private final AudioRepository audioRepository;
    private final NotListenedRepository notListenedRepository;
    private final ConverterService converterService;
    @Value("${bot.audio.path}")
    private String pathToAudio;
    @Value("${bot.token}")
    private String token;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;

    @Autowired
    public AudioServiceImpl(RestTemplate restTemplate, HttpHeaders headers, AudioRepository audioRepository,
                            NotListenedRepository notListenedRepository, ConverterService converterService) {
        this.restTemplate = restTemplate;
        this.headers = headers;
        this.audioRepository = audioRepository;
        this.notListenedRepository = notListenedRepository;
        this.converterService = converterService;
    }

    @Override
    @Transactional
    public void saveVoice(Voice voice, Long userId) {
        LocalDate today = LocalDate.now();
        if (audioRepository.existsByDateAndUserId(today, userId)) {
            try {
                String audioIdInDB = getFileId(userId, today);
                writeDataToDB(voice, userId);
                concatenateAudios(
                        pathToAudio + audioIdInDB + AUDIO_EXTENSION,
                        pathToAudio + voice.getFileId() + AUDIO_EXTENSION
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
        Path filePath = Paths.get(pathToAudio, fileId + AUDIO_EXTENSION);
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
    public void removeAudioFromListening(Long userId, String fileId) {
        audioRepository.deleteFromNotListened(userId, fileId);
    }

    @Override
    public String getPathToAudio(String fileId) {
        return pathToAudio + fileId + AUDIO_EXTENSION;
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

    @Override
    public NotListened getMetaDataAudioForPull() {
        return converterService.ConvertTo(NotListened.class, notListenedRepository.getOldestAudio());
    }

    @Override
    public boolean existAudioForUser(Long userId) {
        return notListenedRepository.existsBySubscriber(userId);
    }

    @Override
    public boolean existsByUserIdAndDate(Long userId, LocalDate date) {
        return audioRepository.existsByDateAndUserId(date, userId);
    }

    @Override
    public String getFileId(Long userId, LocalDate date) {
        Optional<String> audioIdInDB = audioRepository.getIdByDateAndUserId(date, userId);
        if (audioIdInDB.isEmpty()) {
            throw new RuntimeException("something was wrong, there are no audio");
        }
        return audioIdInDB.get();
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
        String fullUri = fileStorageUri
                .replace("{token}", token)
                .replace("{filePath}", filePath);
        URL urlObj;
        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new RuntimeException("bad URL passed, " + e.getMessage());
        }

        try (InputStream is = urlObj.openStream()) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(urlObj.toExternalForm(), e);
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void changeStatus() {
        audioRepository.fillNotListened();
        log.info("audios added to listening");
    }
}
