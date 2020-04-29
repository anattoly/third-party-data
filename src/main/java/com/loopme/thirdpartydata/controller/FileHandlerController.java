package com.loopme.thirdpartydata.controller;

import com.loopme.thirdpartydata.service.amazon.S3Services;
import com.loopme.thirdpartydata.service.csv.CsvService;
import com.loopme.thirdpartydata.service.moat.MoatService;
import com.loopme.thirdpartydata.utils.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("third-party-data")
public class FileHandlerController {

    @Value("${file.download-dir}")
    private String resourceFile;

    private Logger logger = LoggerFactory.getLogger(FileHandlerController.class);

    private final MoatService moatService;
    private final S3Services s3Service;
    private final CsvService csvService;
    private final JsonUtils jsonUtils;

    public FileHandlerController(MoatService moatService,
                                 S3Services s3Service,
                                 CsvService csvService,
                                 JsonUtils jsonUtils) {
        this.moatService = moatService;
        this.s3Service = s3Service;
        this.csvService = csvService;
        this.jsonUtils = jsonUtils;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("files") MultipartFile[] files) {
        Path pathToFile = Paths.get(resourceFile);
        if (files != null) {
            for (MultipartFile file : files) {
                try {
                    file.transferTo(pathToFile);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                moatService.updateMoatDTOByAppId(csvService.parsingCSV(pathToFile));
            }
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/download/s3", consumes = "text/plain;charset=UTF-8")
    public ResponseEntity<?> downloadFilesFromS3(@RequestHeader("x-amz-sns-message-type") String requestType,
                                                 @RequestBody String body) {
        Path pathToFile = Paths.get(resourceFile);
        ResponseEntity response = null;
        List<String> listS3Files = new ArrayList<>();

        if (requestType.equals("SubscriptionConfirmation")) {
            s3Service.confirmSubscription(body);
            response = ResponseEntity.status(HttpStatus.CREATED).build();

        } else if (requestType.equals("Notification")) {
            listS3Files.add(jsonUtils.getFileName(body));
            s3Service.downloadFile(listS3Files);
            response = ResponseEntity.status(HttpStatus.CREATED).build();
        }

        try {
            TimeUnit.SECONDS.sleep(10);
            moatService.updateMoatDTOByAppId(csvService.parsingCSV(pathToFile));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return response;
    }

    @PostMapping("/download")
    public ResponseEntity<?> testDownload() {
        Path pathToFile = Paths.get(resourceFile);

        s3Service.downloadFile(s3Service.listFilesInBucket());
        moatService.updateMoatDTOByAppId(csvService.parsingCSV(pathToFile));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @SqsListener("${jsa.sqs.queue}")
    public void readMessage(String message) {
    }
}
