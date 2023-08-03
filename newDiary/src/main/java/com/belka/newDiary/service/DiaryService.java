package com.belka.newDiary.service;

import java.time.LocalDate;

/**
 * service for interaction with {@link com.belka.newDiary.entity.DiaryEntity}
 */
public interface DiaryService {
    /**
     * add a note to the diary
     *
     * @param chatID user's id
     * @param text   the text that the user sent to the diary
     */
    void addNote(Long chatID, String text);

    String getNote(LocalDate date, Long chatID);
}
