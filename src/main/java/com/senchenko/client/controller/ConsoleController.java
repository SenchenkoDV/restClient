package com.senchenko.client.controller;

import com.senchenko.client.entity.WeatherRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

@Component
public class ConsoleController implements CommandLineRunner {
    private static final String RESULT = "City: %s, temperature = %s \n";

    @Value("${integration.url}")
    private static String integrationUrl = "http://localhost:8081/integration/temperature";

    @Override
    public void run(String... args) throws Exception {

        try (ServerSocket serverSocket = new ServerSocket(8082)) {
            Socket socket = serverSocket.accept();
            Scanner scanner = new Scanner(socket.getInputStream());
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            printWriter.println("Hello! Enter the city name.");
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.equals("exit")) {
                    break;
                }
                try {
                    String time = getTime(line);
                    printWriter.println(time);
                }catch (IllegalStateException | JSONException e){
                    printWriter.println("Wrong city!");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getTime(String cityName) {
        RestTemplate restTemplate = new RestTemplate();
        WeatherRequest weatherRequest = new WeatherRequest();
        weatherRequest.setName(cityName);
        HttpEntity<WeatherRequest> request = new HttpEntity<>(weatherRequest);
        ResponseEntity<String> response = restTemplate
                .exchange(integrationUrl, HttpMethod.POST, request, String.class);
        JSONObject jsonBody = new JSONObject(Objects.requireNonNull(response.getBody()));
        JSONObject cityResponse = jsonBody.getJSONObject("ns2:getCityResponse");
        JSONObject city = cityResponse.getJSONObject("ns2:city");
        return String.format(RESULT, city.get("ns2:name"), city.get("ns2:temperature"));
    }
}
