package com.belka.audio.repositoryes;

public interface AudioSqlRepository {
    /**
     * fill the table not_listened with new audios for every subscriber
     */
    void fillNotListened();

    /**
     * delete a chosen audio from the table not_listened
     *
     * @param fileId audio's ID
     * @param userId subscriber's ID
     */
    void deleteFromNotListened(Long userId, String fileId);
}