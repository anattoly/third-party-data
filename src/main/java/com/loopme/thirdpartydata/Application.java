package com.loopme.thirdpartydata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;

@SpringBootApplication
@EnableSqs
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }



}
