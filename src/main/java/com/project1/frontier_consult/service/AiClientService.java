package com.project1.frontier_consult.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class AiClientService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String getAnswerFromFastApi(String question) {
        String fastApiUrl = "http://localhost:8000/ask";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format("{\"question\": \"%s\"}", question);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(fastApiUrl, entity, String.class);
            return response.getBody();

        } catch (HttpStatusCodeException ex) {
            log.error("FastAPI error ({}): {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            return "Сервис временно недоступен. Попробуйте позже.";

        } catch (Exception ex) {
            log.error("Unexpected error during FastAPI call: ", ex);
            return "Сервис временно недоступен. Попробуйте позже.";
        }
    }
}

