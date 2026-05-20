package com.pazos.wtovrmanager.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pazos.wtovrmanager.model.backendModels.Athlete;
import com.pazos.wtovrmanager.model.backendModels.Category;
import com.pazos.wtovrmanager.model.backendModels.Match;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Properties;

public class ApiService {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final String baseUrl;
    private final HttpClient httpClient;

    public ApiService() {
        Properties config = new Properties();
        try {
            config.load(getClass().getResourceAsStream("/config.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.baseUrl = config.getProperty("api.base.url", "");
        this.httpClient = HttpClient.newHttpClient();
    }

    public List<Match> getMatches() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/matches"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        return MAPPER.readValue(response.body(),
                MAPPER.getTypeFactory().constructCollectionType(List.class, Match.class));
    }

    public List<Match> getMatchesRing(int ring) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/matches/" + ring))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        return MAPPER.readValue(response.body(),
                MAPPER.getTypeFactory().constructCollectionType(List.class, Match.class));
    }
    public List<Category> getCategories() throws Exception{
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/categories")).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return MAPPER.readValue(response.body(),
                MAPPER.getTypeFactory().constructCollectionType(List.class, Category.class));
    }

    public List<Athlete> getAthletes() throws Exception{
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/athletes")).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return MAPPER.readValue(response.body(),
                MAPPER.getTypeFactory().constructCollectionType(List.class, Athlete.class));
    }
}
