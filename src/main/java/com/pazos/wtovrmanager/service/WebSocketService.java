package com.pazos.wtovrmanager.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pazos.wtovrmanager.model.MatchEvent;
import javafx.application.Platform;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class WebSocketService {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static final int RECONNECT_DELAY_SECONDS = 3;

    private final String baseUrl;
    private final HttpClient httpClient;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private WebSocket webSocket;
    private volatile boolean active = false;
    private int currentMat;

    private Consumer<MatchEvent> onMessage;
    private Runnable onConnect;
    private Runnable onDisconnect;

    public WebSocketService(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
    }

    public void setOnMessage(Consumer<MatchEvent> onMessage) {
        this.onMessage = onMessage;
    }

    public void setOnConnect(Runnable onConnect) {
        this.onConnect = onConnect;
    }

    public void setOnDisconnect(Runnable onDisconnect) {
        this.onDisconnect = onDisconnect;
    }

    public void connect(int mat) {
        if (active) disconnect();
        currentMat = mat;
        active = true;
        doConnect();
    }

    public void disconnect() {
        active = false;
        if (webSocket != null && !webSocket.isOutputClosed()) {
            webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "disconnect");
        }
    }

    private void doConnect() {
        String url = baseUrl + "/mats/" + currentMat;
        httpClient.newWebSocketBuilder()
                .buildAsync(URI.create(url), new Listener())
                .thenAccept(ws -> {
                    webSocket = ws;
                    Platform.runLater(() -> {
                        if (onConnect != null) onConnect.run();
                    });
                })
                .exceptionally(ex -> {
                    scheduleReconnect();
                    return null;
                });
    }

    private void scheduleReconnect() {
        if (!active) return;
        Platform.runLater(() -> {
            if (onDisconnect != null) onDisconnect.run();
        });
        scheduler.schedule(this::doConnect, RECONNECT_DELAY_SECONDS, TimeUnit.SECONDS);
    }

    private class Listener implements WebSocket.Listener {

        private final StringBuilder buffer = new StringBuilder();

        @Override
        public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
            buffer.append(data);
            if (last) {
                String json = buffer.toString();
                buffer.setLength(0);
                try {
                    MatchEvent event = MAPPER.readValue(json, MatchEvent.class);
                    Platform.runLater(() -> {
                        if (onMessage != null) onMessage.accept(event);
                    });
                } catch (Exception ignored) {
                }
            }
            ws.request(1);
            return null;
        }

        @Override
        public CompletionStage<?> onClose(WebSocket ws, int statusCode, String reason) {
            scheduleReconnect();
            return null;
        }

        @Override
        public void onError(WebSocket ws, Throwable error) {
            scheduleReconnect();
        }
    }
}
