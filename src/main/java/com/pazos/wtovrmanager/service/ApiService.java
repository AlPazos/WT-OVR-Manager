package com.pazos.wtovrmanager.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pazos.wtovrmanager.model.backendModels.Athlete;
import com.pazos.wtovrmanager.model.backendModels.Category;
import com.pazos.wtovrmanager.model.backendModels.Match;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Properties;
import java.nio.charset.StandardCharsets;

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

    private String get(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .GET()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    public List<Match> getMatches() throws IOException, InterruptedException {
        return MAPPER.readValue(get("/matches"),
                MAPPER.getTypeFactory().constructCollectionType(List.class, Match.class));
    }

    public List<Match> getMatchesRing(int ring) throws IOException, InterruptedException {
        return MAPPER.readValue(get("/matches/" + ring),
                MAPPER.getTypeFactory().constructCollectionType(List.class, Match.class));
    }

    public List<Category> getCategories() throws IOException, InterruptedException {
        return MAPPER.readValue(get("/categories"),
                MAPPER.getTypeFactory().constructCollectionType(List.class, Category.class));
    }

    public List<Athlete> getAthletes() throws IOException, InterruptedException {
        return MAPPER.readValue(get("/athletes"),
                MAPPER.getTypeFactory().constructCollectionType(List.class, Athlete.class));
    }

    public List<Athlete> getAthletesCategory(int categoryId) throws IOException, InterruptedException {
        return MAPPER.readValue(get("/athletes/category/" + categoryId),
                MAPPER.getTypeFactory().constructCollectionType(List.class, Athlete.class));
    }

    public void uploadTournament(String json) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/uploadTournament"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
