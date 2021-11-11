package com.techelevator.tenmo.services;

import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {
    private String baseUrl;
    private RestTemplate restTemplate = new RestTemplate();

    public AccountService(String url) {
        baseUrl = url;
    }

    public BigDecimal getBalance(String token) {
        BigDecimal balance = BigDecimal.ZERO;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<BigDecimal> responseEntity = restTemplate.exchange(baseUrl + "/balance", HttpMethod.GET, entity, BigDecimal.class);
            balance = responseEntity.getBody();

        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println(e.getMessage());
        }
        return balance;
    }
}
