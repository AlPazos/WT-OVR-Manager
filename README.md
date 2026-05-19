# WT-OVR-Manager

> **This project is currently under active development. Expect breaking changes.**

A JavaFX desktop client for managing and displaying WT (World Taekwondo) competition overlays in real time. It connects to a running [WT-OVR-Bridge](https://github.com/AlPazos/WT-OVR-Bridge) server via WebSocket and REST, receiving live match events (scores, penalties, round time, competitors) and rendering them through a fully custom dark-themed UI.

---

## Requirements

- **Java 11** or higher (JDK 21 recommended)
- **Maven 3.8+**
- A running instance of [**WT-OVR-Bridge**](https://github.com/AlPazos/WT-OVR-Bridge) — the Quarkus backend that connects to the scoring system and broadcasts events over WebSocket

---

## Getting Started

### 1. Set up WT-OVR-Bridge

Clone and run the backend server by following the instructions in [its repository](https://github.com/AlPazos/WT-OVR-Bridge). By default it listens on `http://localhost:8080`.

### 2. Configure the URLs

Open `src/main/resources/config.properties` and make sure the URLs match the host and port where WT-OVR-Bridge is running:

```properties
api.base.url=http://localhost:8080/manager
api.websocket.url=ws://localhost:8080/ws
```

If the bridge runs on a different host or port, update both values accordingly. The WebSocket client connects to `{api.websocket.url}/mats/{mat}`, where `mat` is the mat number.

### 3. Run the application

```bash
mvn clean javafx:run
```

---

## Configuration Reference

| Key | Description |
|---|---|
| `app.language` | UI language (`en`, `gl`, …) |
| `app.css` | Path to the custom stylesheet |
| `api.base.url` | Base URL of the WT-OVR-Bridge REST API |
| `api.websocket.url` | Base URL for WebSocket connections |
| `api.endpoint.matches` | Endpoint for match list |
| `api.endpoint.athletes` | Endpoint for athlete list |
| `api.endpoint.categories` | Endpoint for category list |

---

## Tech Stack

- [JavaFX 21](https://openjfx.io/) — UI framework
- [AtlantaFX](https://github.com/mkpaz/atlantafx) — base theme
- [Jackson](https://github.com/FasterXML/jackson) — JSON deserialization
- [WT-OVR-Bridge](https://github.com/AlPazos/WT-OVR-Bridge) — Quarkus backend (required)

---

## Contributing

Contributions are welcome. The project is in early development and there is plenty of room for improvement — bug fixes, new features, UI improvements, and documentation are all appreciated.

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes
4. Open a pull request describing what you changed and why

Please open an issue first if you plan to work on something significant, so we can discuss the approach before you invest time in it.

---

## License

To be defined.
