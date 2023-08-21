package com.belka.audio.services;

import org.telegram.telegrambots.meta.api.objects.Voice;

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
}