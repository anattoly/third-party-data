package com.loopme.thirdpartydata.service.amazon;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.ConfirmSubscriptionRequest;
import com.amazonaws.util.IOUtils;
import com.loopme.thirdpartydata.utils.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class S3ServiceImpl implements S3Services {
    private Logger logger = LoggerFactory.getLogger(S3ServiceImpl.class);

    @Value("${jsa.s3.bucket}")
    private String bucketName;
    @Value("${file.download-dir}")
    private String resourceFile;

    private final AmazonS3 s3client;
    private final AmazonSNS snsClient;
    private final JsonUtils jsonUtils;

    public S3ServiceImpl(AmazonS3 s3client, AmazonSNS snsClient, JsonUtils jsonUtils) {
        this.s3client = s3client;
        this.snsClient = snsClient;
        this.jsonUtils = jsonUtils;
    }

    @Override
    public List<String> listFilesInBucket() {
        ListObjectsRequest listObjectsRequest =
                new ListObjectsRequest()
                        .withBucketName(bucketName);
        List<String> keys = new ArrayList<>();
        ObjectListing objects = s3client.listObjects(listObjectsRequest);

        while (true) {
            List<S3ObjectSummary> summaries = objects.getObjectSummaries();
            if (summaries.size() < 1) {
                break;
            }

            for (S3ObjectSummary item : summaries) {
                if (item.getKey().endsWith(".csv")) {
                    keys.add(item.getKey());
                }
            }
            objects = s3client.listNextBatchOfObjects(objects);
        }
        return keys;
    }

    @Override
    public void downloadFile(List<String> filesName) {
        int countFile = 0;

        Path dir = Paths.get(resourceFile);
        for (String fileName : filesName) {
            File file = null;
            try {
                if (!Files.exists(dir)) {
                    Files.createDirectory(dir);
                    file = new File(resourceFile + fileName);
                } else {
                    file = new File(resourceFile + fileName);
                }
                S3Object s3Object = s3client.getObject(new GetObjectRequest(bucketName, fileName));
                S3ObjectInputStream reader = s3Object.getObjectContent();
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                byte[] bytes = IOUtils.toByteArray(reader);
                fileOutputStream.write(bytes);

                s3client.deleteObject(bucketName, fileName);

            } catch (AmazonServiceException ase) {
                logger.info("Caught an AmazonServiceException from GET requests, reject reasons:");
                logger.info("Error Message:    " + ase.getMessage());
                logger.info("HTTP Status Code: " + ase.getStatusCode());
                logger.info("AWS Error Code:   " + ase.getErrorCode());
                logger.info("Error Type:       " + ase.getErrorType());
                logger.info("Request ID:       " + ase.getRequestId());
            } catch (AmazonClientException ace) {
                logger.info("Caught an AmazonClientException: ");
                logger.info("Error Message: " + ace.getMessage());
            } catch (IOException ioe) {
                ioe.getMessage();
            }
            countFile++;
        }
        logger.info(countFile + " files where downloaded to local directory");
    }

    @Override
    public void uploadFile(String keyName) {
    }

    @Override
    public void confirmSubscription(String requestBody) {
        Map<String, String> request = jsonUtils.getConfirmField(requestBody);

        snsClient.confirmSubscription(new ConfirmSubscriptionRequest(request.get("TopicArn"), request.get("Token")));
    }
}
