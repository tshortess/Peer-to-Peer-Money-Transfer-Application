package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransferService {
    private String baseUrl;
    private RestTemplate restTemplate = new RestTemplate();

    public TransferService(String url) {
        baseUrl = url;
    }

    public Transfer initiateTransfer(int toUserId, int amount, String token) {
        Transfer transfer = new Transfer();
        transfer.setToUserId(toUserId);
        transfer.setAmount(BigDecimal.valueOf(amount));
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON); //consider extracting to separate method as it's used throughout Transfer Service
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
        try {
            ResponseEntity<Transfer> responseEntity = restTemplate.exchange(baseUrl + "/transfers", HttpMethod.POST, entity, Transfer.class);
            transfer = responseEntity.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println(e.getMessage());

        }
        return transfer;
    }

    public Transfer requestTransfer(int fromUserId, int amount, String token) {
        Transfer transfer = new Transfer();
        transfer.setFromUserId(fromUserId);
        transfer.setAmount(BigDecimal.valueOf(amount));
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
        try {
            ResponseEntity<Transfer> responseEntity = restTemplate.exchange(baseUrl + "/transfers/request", HttpMethod.POST, entity, Transfer.class);
            transfer = responseEntity.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println(e.getMessage());

        }
        return transfer;
    }

    public Transfer approveTransfer(String token, Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
        try {
            ResponseEntity<Transfer> responseEntity = restTemplate.exchange(baseUrl + "/transfers/approve", HttpMethod.PUT, entity, Transfer.class);
            transfer = responseEntity.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println(e.getMessage());
        }
        return transfer;
    }

    public Transfer rejectTransfer(String token, Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
        try {
            ResponseEntity<Transfer> responseEntity = restTemplate.exchange(baseUrl + "/transfers/reject", HttpMethod.PUT, entity, Transfer.class);
            transfer = responseEntity.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println(e.getMessage());
        }
        return transfer;
    }

    public List<User> getUsers(String token) {
        List<User> users = new ArrayList<>();
        Map<Integer, String> responseMap = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(baseUrl + "/users", HttpMethod.GET, entity, Map.class);
            responseMap = response.getBody();
            int i = 0;

            for (Map.Entry<Integer, String> user: responseMap.entrySet()) {
                User newUser = new User();
                newUser.setId(Integer.parseInt(String.valueOf(user.getKey())));
                newUser.setUsername(user.getValue());
                users.add(newUser);
                i++;
            }
            return users;
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println(e.getMessage());
        }
        return users;
    }

    public List<Transfer> getCompletedTransfers(String token) {
        List<Transfer> transfers = new ArrayList<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<List<Transfer>> response = restTemplate.exchange(baseUrl + "transfers/completed", HttpMethod.GET, entity, new ParameterizedTypeReference<List<Transfer>>(){});
            transfers = response.getBody();
            return transfers;
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println(e.getMessage());
        }
        return transfers;
    }

    public List<Transfer> getPendingTransfers(String token) {
        List<Transfer> transfers = new ArrayList<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<List<Transfer>> response = restTemplate.exchange(baseUrl + "transfers/pending", HttpMethod.GET, entity, new ParameterizedTypeReference<List<Transfer>>(){});
            transfers = response.getBody();
            return transfers;
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println(e.getMessage());
        }
        return transfers;
    }

}
