package com.loopme.thirdpartydata.service.moat;

import com.loopme.thirdpartydata.model.MoatDTO;

import java.util.List;
import java.util.Optional;

public interface MoatService {
    Optional<MoatDTO> findMoatDTOByAppId(String id);
    void updateMoatDTOByAppId(List<MoatDTO> moatDTO);
}
