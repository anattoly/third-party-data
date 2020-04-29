package com.loopme.thirdpartydata.utils.json;

import java.time.LocalDateTime;
import java.util.Map;

public interface JsonUtils {
    String getFileName(String requestBody);
    Map<String, String> getConfirmField(String requestBody);
    LocalDateTime getTimeRequest(String requestBody);
}
