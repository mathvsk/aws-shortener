package com.matheus.redirect_url_shortener;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Main implements RequestHandler<Map<String, Object>, Map<String, Object>> {
    private final static String BUCKET_NAME = System.getenv("AWS_BUCKET_NAME");
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final S3Client s3Client = S3Client.builder().build();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();

        String shortUrl = this.extractShortUrl(input);
        if (shortUrl == null || shortUrl.isEmpty()) {
            response.put("statusCode", 400);
            response.put("body", "Short URL cannot be empty");

            return response;
        }

        GetObjectRequest getObjectRequest = this.createGetRequestToS3(shortUrl);

        UrlData urlData;
        try (InputStream s3ObjectStream = s3Client.getObject(getObjectRequest)) {
            urlData =  this.objectMapper.readValue(s3ObjectStream, UrlData.class);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing URL data: " + e.getMessage(), e);
        }

        long currentTimeInSeconds = System.currentTimeMillis() / 1000;

        if (currentTimeInSeconds < urlData.getExpirationTime()) {
            Map<String, String> headers = new HashMap<>();
            headers.put("Location", urlData.getOriginalUrl());

            response.put("statusCode", 302);
            response.put("headers", headers);
        } else {
            response.put("statusCode", 410);
            response.put("body", "This URL has expired.");
        }

        return response;
    }

    private String extractShortUrl(Map<String, Object> input) {
        String pathParameter = (String) input.get("rawPath");
        if (pathParameter == null || pathParameter.isEmpty() || pathParameter.equals("/")) {
            return null;
        }

        return pathParameter.startsWith("/") ? pathParameter.substring(1) : pathParameter;
    }

    private GetObjectRequest createGetRequestToS3(String shortUrl) {
        return GetObjectRequest
                .builder()
                .bucket(BUCKET_NAME)
                .key(shortUrl + ".json")
                .build();
    }
}