package com.belka.newDiary;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class DiaryServiceImpl implements DiaryService {
    private final int PREFIX_LENGTH = 9;
    private DiaryRepository repository;

    @Autowired
    public void setRepository(DiaryRepository repository) {
        this.repository = repository;
    }

    @Override
    public void addNote(Long chatID, String text) {
        Date today = new Date();
        String note = text.substring(PREFIX_LENGTH);
        Optional<DiaryEntity> entity = repository.getByDateAndUserId(today, chatID);
        if (entity.isPresent()) {
            DiaryEntity entityForUpdate = entity.get();
            String newNote = entityForUpdate.getNote() + "\n" + note;
            entityForUpdate.setNote(newNote);
            repository.save(entityForUpdate);
            log.info("added a note to the diary");
        } else {
            DiaryEntity entityForSave = DiaryEntity.builder()
                    .date(today)
                    .userId(chatID)
                    .note(note)
                    .build();
            repository.save(entityForSave);
            log.info("added a note to the diary");
        }
    }
}

