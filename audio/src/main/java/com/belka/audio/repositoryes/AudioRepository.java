package com.belka.audio.repositoryes;

import com.belka.audio.entityes.AudioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AudioRepository extends JpaRepository<AudioEntity, String> {
}