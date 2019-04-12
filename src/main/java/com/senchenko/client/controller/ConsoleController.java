package com.senchenko.client.controller;

import com.senchenko.client.entity.WeatherRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Scanner;

@Component
public class ConsoleController implements CommandLineRunner {
    private static final String RESULT = "City: %s, temperature = %s \n";

    @Value("${integration.url}")
    private String integrationUrl;

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String cityName;
        while (true) {
            cityName = scanner.nextLine();
            RestTemplate restTemplate = new RestTemplate();
            WeatherRequest weatherRequest = new WeatherRequest();
            weatherRequest.setName(cityName);
            HttpEntity<WeatherRequest> request = new HttpEntity<>(weatherRequest);
            ResponseEntity<String> response = restTemplate
                    .exchange(integrationUrl, HttpMethod.POST, request, String.class);
            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.getBody()));
            JSONObject jsonObject1 = jsonObject.getJSONObject("ns2:getCityResponse");
            JSONObject jsonObject2 = jsonObject1.getJSONObject("ns2:city");
            System.out.printf(RESULT, jsonObject2.get("ns2:name"), jsonObject2.get("ns2:temperature"));
        }
    }
}
