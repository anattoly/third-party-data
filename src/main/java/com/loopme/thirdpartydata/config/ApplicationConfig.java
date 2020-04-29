package com.loopme.thirdpartydata.config;

import com.loopme.thirdpartydata.repository.MoatRepository;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.loopme.thirdpartydata.repository")
public class ApplicationConfig {

    private final MoatRepository moatRepository;

    public ApplicationConfig(MoatRepository moatRepository) {
        this.moatRepository = moatRepository;
    }
}
