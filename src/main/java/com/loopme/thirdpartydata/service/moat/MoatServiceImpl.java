package com.loopme.thirdpartydata.service.moat;

import com.loopme.thirdpartydata.model.MoatDTO;
import com.loopme.thirdpartydata.repository.MoatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MoatServiceImpl implements MoatService {

    private final MoatRepository moatRepository;

    public MoatServiceImpl(MoatRepository moatRepository) {
        this.moatRepository = moatRepository;
    }


    @Override
    public Optional<MoatDTO> findMoatDTOByAppId(String id) {
        return moatRepository.findMoatDTOByAppID(id);
    }

    @Override
    public void updateMoatDTOByAppId(List<MoatDTO> moatDTOList) {
        moatDTOList.forEach(moatDTO -> {
            moatRepository.findMoatDTOByAppID(moatDTO.appID)
                    .map(moat -> {
                        moat.setAvocRate(moatDTO.avocRate);
                        moat.setMeasureRate(moatDTO.measureRate);
                        moat.setViewableRate(moatDTO.viewableRate);
                        moat.setOneSecOnScreenRate(moatDTO.oneSecOnScreenRate);
                        return moatRepository.save(moat);
                    });
        });
    }
}
