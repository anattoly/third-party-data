package com.loopme.thirdpartydata.repository;

import com.loopme.thirdpartydata.model.MoatDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MoatRepository extends JpaRepository<MoatDTO, String> {

    Optional<MoatDTO> findMoatDTOByAppID(String id);
}
