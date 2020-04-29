package com.loopme.thirdpartydata.service.amazon;

import java.util.List;
import java.util.Map;

public interface S3Services {
    List<String> listFilesInBucket();

    void downloadFile(List<String> keyName);

    void uploadFile(String keyName);

    void confirmSubscription(String requestBody);
}
