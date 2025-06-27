package com.matheus.url_shortener;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main implements RequestHandler<Map<String, Object>, Map<String, String>> {
    private final String BUCKET_NAME = System.getenv("S3_BUCKET_NAME");
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final S3Client s3Client = S3Client.builder().build();

    @Override
    public Map<String, String> handleRequest(Map<String, Object> input, Context context) {
        Map<String, String> response = new HashMap<>();

        String body = input.get("body").toString();
        if (body == null || body.isEmpty()) {
            response.put("statusCode", "400");
            response.put("body", "Request body cannot be empty");

            return response;
        }

        UrlData urlData;
        try {
            Map<String, String> bodyMap = this.objectMapper.readValue(body, Map.class);
            urlData = this.extractBodyProperties(bodyMap);
        } catch (Exception e) {
            response.put("statusCode", "400");
            response.put("body", "Invalid request body: " + e.getMessage());

            return response;
        }

        try {
            String registerIdToS3 = UUID.randomUUID().toString().substring(0, 8);
            String urlDataJson = this.objectMapper.writeValueAsString(urlData);

            PutObjectRequest putObjectRequest = this.createPutRequestToS3(registerIdToS3);
            s3Client.putObject(putObjectRequest, RequestBody.fromString(urlDataJson));

            response.put("shortUrl", registerIdToS3);

            return response;
        } catch (Exception e) {
            response.put("statusCode", "500");
            response.put("body", "Error saving URL data to S3: " + e.getMessage());

            return response;
        }
    }

    private UrlData extractBodyProperties(Map<String, String> bodyMap) {
        String originalUrl = bodyMap.get("originalUrl");
        String expirationTime = bodyMap.get("expirationTime");

        if (originalUrl == null || expirationTime == null) {
            throw new IllegalArgumentException("Missing required fields: originalUrl and expirationTime");
        }

        try {
            long expTime = Long.parseLong(expirationTime);
            return UrlData.builder()
                    .originalUrl(originalUrl)
                    .expirationTime(expTime)
                    .build();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("expirationTime must be a valid number");
        }
    }

    private PutObjectRequest createPutRequestToS3(String id) {
        return PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(id + ".json")
                .build();
    }
}