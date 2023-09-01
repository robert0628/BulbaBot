package com.belka.audio.services;

import com.belka.audio.models.NotListened;
import org.telegram.telegrambots.meta.api.objects.Voice;

import java.time.LocalDate;

/**
 * service for work with audio messages
 */
public interface AudioService {
    /**
     * save {@link Voice voice} to DB
     *
     * @param voice  {@link Voice voice}
     * @param userId ID of the user who sent this {@link Voice voice}
     */
    void saveVoice(Voice voice, Long userId);

    /**
     * get path to Voice in local DB
     *
     * @param fileId voice's ID
     * @return path to Voice in local DB
     */
    String getPathToAudio(String fileId);

    /**
     * delete chosen Voice
     *
     * @param fileId Voice's ID
     */
    void deleteVoice(String fileId);

    /**
     * change voice status on public or private
     *
     * @param fileId Voice's ID
     * @param flag   if the voice status is private, the flag is false
     */
    void changeIsPrivateFlag(boolean flag, String fileId);

    /**
     * deletes audio from the listening list
     *
     * @param userId subscriber's ID
     * @param fileId audio's ID
     */
    void removeAudioFromListening(Long userId, String fileId);

    /**
     * get meta data about audio for listening
     */
    NotListened getMetaDataAudioForPull();

    /**
     * check if exist new audios for pulling
     *
     * @param userId subscriber's ID
     * @return true if there are audios for pulling
     */
    boolean existAudioForUser(Long userId);

    /**
     * checks if an audio exists on that day
     *
     * @param userId user's id
     * @param date   which day we check
     * @return true if any note exists or false if not
     */
    boolean existsByUserIdAndDate(Long userId, LocalDate date);
}