package com.loopme.thirdpartydata.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "apps")
@Data
public class MoatDTO {

    @Id
    @Column(name = "id")
    public Integer id;
    @Column(name = "key")
    public String appID;
    @Column(name = "moat_measure_rate")
    public Double measureRate;
    @Column(name = "moat_viewable_rate")
    public Double viewableRate;
    @Column(name = "moat_avoc_rate")
    public Double avocRate;
    @Column(name = "moat_1_sec_on_screen_rate")
    public Double oneSecOnScreenRate;

    public MoatDTO() {
    }

    public MoatDTO(Integer id, String appID, Double measureRate, Double viewableRate, Double avocRate, Double oneSecOnScreenRate) {
        this.id = id;
        this.appID = appID;
        this.measureRate = measureRate;
        this.viewableRate = viewableRate;
        this.avocRate = avocRate;
        this.oneSecOnScreenRate = oneSecOnScreenRate;
    }
}