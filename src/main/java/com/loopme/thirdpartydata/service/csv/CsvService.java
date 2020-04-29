package com.loopme.thirdpartydata.service.csv;

import com.loopme.thirdpartydata.model.CsvDTO;
import com.loopme.thirdpartydata.model.MoatDTO;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public interface CsvService {
    List<MoatDTO> parsingCSV(Path pathToFiles);

    List<CsvDTO> readingCsvFiles(File[] files);

    List<MoatDTO> transformCsvToMoatDTO(List<CsvDTO> csvDTOList);
}
