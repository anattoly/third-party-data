package com.loopme.thirdpartydata.service.csv;

import com.loopme.thirdpartydata.model.CsvDTO;
import com.loopme.thirdpartydata.model.MoatDTO;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CsvServiceImpl implements CsvService {

    private Logger logger = LoggerFactory.getLogger(CsvServiceImpl.class);

    @Override
    public List<MoatDTO> parsingCSV(Path pathToFiles) {
        File path = pathToFiles.toFile();
        File[] files = path.listFiles((d, file) -> file.endsWith(".csv"));

        return transformCsvToMoatDTO(readingCsvFiles(files));
    }

    @Override
    public List<CsvDTO> readingCsvFiles(File[] files) {
        List<CsvDTO> csvDTOList = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                try (Reader reader = Files.newBufferedReader(file.toPath())) {
                    HeaderColumnNameMappingStrategy<CsvDTO> strategy
                            = new HeaderColumnNameMappingStrategy<>();

                    strategy.setType(CsvDTO.class);

                    CsvToBean<CsvDTO> builder = new CsvToBeanBuilder<CsvDTO>(reader)
                            .withType(CsvDTO.class)
                            .withMappingStrategy(strategy)
                            .withIgnoreLeadingWhiteSpace(true)
                            .withEscapeChar('%')
                            .build();

                    csvDTOList.addAll(builder.parse());
                    Files.delete(file.toPath());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        logger.info("Loaded " + csvDTOList.size() + " rows from csv files");
        return csvDTOList;
    }

    @Override
    public List<MoatDTO> transformCsvToMoatDTO(List<CsvDTO> csvDTOList) {
        //filtered same appKey data
        List<CsvDTO> filteredOutOfRepeatedList = new ArrayList<>();
        for (CsvDTO csvDto : csvDTOList) {
            if (filteredOutOfRepeatedList.isEmpty()) filteredOutOfRepeatedList.add(csvDto);
            String appKey = getLastTenChar(csvDto.appID.values().iterator().next());
            Integer currentImpression = csvDto.impression;
            boolean addRowInFilteredList = false;
            for (int j = 0; j < filteredOutOfRepeatedList.size(); j++) {
                CsvDTO filteredDTO = filteredOutOfRepeatedList.get(j);
                String appID = filteredDTO.appID.values().iterator().next();
                if (appID.contains(appKey)) {
                    if (currentImpression > filteredDTO.impression) {
                        filteredOutOfRepeatedList.remove(filteredDTO);
                        addRowInFilteredList = true;
                        break;
                    } else {
                        addRowInFilteredList = false;
                        break;
                    }
                } else {
                    addRowInFilteredList = true;
                }
            }
            if (addRowInFilteredList) filteredOutOfRepeatedList.add(csvDto);
        }

        int duplicateAppRow = csvDTOList.size() - filteredOutOfRepeatedList.size();
        logger.info(filteredOutOfRepeatedList.size() + " rows will be updated in the DB (" + duplicateAppRow + " duplicate rows)");

        //create MoatDTO & add MoatDTO to List
        List<MoatDTO> moatDTOList = new ArrayList<>();
        for (CsvDTO csvDTO : filteredOutOfRepeatedList) {
            MoatDTO mdto = new MoatDTO();
            String appID = getLastTenChar(csvDTO.appID.values().iterator().next());

            mdto.setAppID(appID);
            mdto.setMeasureRate(csvDTO.impression.doubleValue() / csvDTO.impressionUnfiltered.doubleValue());
            mdto.setViewableRate(isNull(csvDTO.viewableRate) / 100.00);
            mdto.setAvocRate(isNull(csvDTO.avocRate) / 100.00);
            mdto.setOneSecOnScreenRate(isNull(csvDTO.oneSecOnScreenRate) / 100.00);
            moatDTOList.add(mdto);
        }
        return moatDTOList;
    }

    private static String getLastTenChar(String appID) {
        return appID.length() < 10 ? appID : appID.substring(appID.length() - 10);
    }

    private static Double isNull(Double value) {
        return Optional.ofNullable(value).orElse(0.00);
    }
}
