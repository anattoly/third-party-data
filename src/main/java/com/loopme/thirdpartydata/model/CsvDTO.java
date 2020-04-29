package com.loopme.thirdpartydata.model;

import com.opencsv.bean.CsvBindAndJoinByName;
import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import org.apache.commons.collections4.MultiValuedMap;

@Data
public class CsvDTO {
    @CsvBindAndJoinByName(column = ".*App ID", elementType = String.class)
    public MultiValuedMap<String, String> appID;
    @CsvBindByName(column = "Impressions Analyzed (unfiltered)")
    public Integer impressionUnfiltered;
    @CsvBindByName(column = "Impressions Analyzed")
    public Integer impression;
    @CsvBindByName(column = "Valid and Viewable Rate")
    public Double viewableRate;
    @CsvBindByName(column = "Valid and AVOC Rate")
    public Double avocRate;
    @CsvBindByName(column = "1 Sec Fully On-Screen Rate")
    public Double oneSecOnScreenRate;
}
