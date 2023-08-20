package com.belka.audio.handlers;

import com.belka.core.handlers.AbstractBelkaHandler;
import com.belka.core.handlers.BelkaEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Voice;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@AllArgsConstructor
public class AudioHandler extends AbstractBelkaHandler {
    private final RestTemplate restTemplate;
    private final HttpHeaders headers;
    @Value("${bot.audio.path}")
    private String pathToAudio;
    @Value("${bot.token}")
    private String token;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;
    private final String extension = ".ogg";

    @Autowired
    public AudioHandler(RestTemplate restTemplate, HttpHeaders headers) {
        this.restTemplate = restTemplate;
        this.headers = headers;
    }

    @Override
    public Flux<PartialBotApiMethod<?>> handle(BelkaEvent event) {
        CompletableFuture<Flux<PartialBotApiMethod<?>>> future = CompletableFuture.supplyAsync(() -> {
            if (event.getUpdate().getMessage().hasVoice()) {
                Voice voice = event.getUpdate().getMessage().getVoice();
                String fileId = voice.getFileId();
                ResponseEntity<String> response = getFilePath(fileId);
                byte[] downloadFile = downloadFile(getFilePath(response));
                saveVoice(downloadFile, fileId);

                return Flux.just(sendAudioFromLocalStorage(pathToAudio + fileId + extension, event.getChatId()));
            }
            return Flux.empty();
        });
        return getCompleteFuture(future, event.getChatId());
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

    void saveVoice(byte[] downloadFile, String fileId) {
        Path filePath = Paths.get(pathToAudio, fileId + extension);
        try {
            Files.write(filePath, downloadFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}