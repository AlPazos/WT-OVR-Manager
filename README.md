# WT-OVR-Manager

> **This project is currently under active development. Expect breaking changes.**

A JavaFX desktop client for managing and displaying WT (World Taekwondo) competition overlays in real time. It connects to a running [WT-OVR-Bridge](https://github.com/AlPazos/WT-OVR-Bridge) server via REST and WebSocket, showing live match data organized by mat/tatami in a fully custom dark-themed UI. It also includes an AI-powered tournament importer that extracts match data from bracket PDFs using the Gemini API and uploads it directly to the backend.

---

## Features

- **Per-mat ring panels** — one panel per tatami, filling the available screen width
- **Live match card** — each panel has a real-time card updated via WebSocket (scores, penalties, round time, competitor names, round status)
- **Available matches list** — scrollable list of upcoming matches for that mat, filtered by `status = available`, showing phase (QF/SF/F), gender, athletes and category
- **Match generator** — select a category and assign blue/red athletes to create new matches
- **Tournament importer** — drag & drop (or browse) a bracket PDF, extract all matches with Gemini 2.5 Flash, review the result and upload it to the backend in one click
- **Auto-reload on match start** — when a `MATCH_STARTED` event arrives, the available matches list refreshes automatically via REST
- **WebSocket auto-reconnect** — reconnects every 3 seconds if the connection drops
- **Custom dark theme** — built on AtlantaFX PrimerDark with a fully overridden CSS stylesheet
- **i18n** — English and Galician out of the box

---

## Requirements

- **Java 16** or higher (JDK 21 recommended)
- **Maven 3.8+**
- A running instance of [**WT-OVR-Bridge**](https://github.com/AlPazos/WT-OVR-Bridge) — the Quarkus backend that connects to the scoring system and broadcasts events over WebSocket
- A **Gemini API key** (only required for the tournament importer) — get one at [aistudio.google.com](https://aistudio.google.com)

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

## Architecture overview

Each mat is represented by a **`RingPanel`** which owns a single `WebSocketService` connection to `{api.websocket.url}/mats/{ring}`. Two independent listeners are registered on that connection:

- **`MatchCard`** — subscribes to all events and updates the live UI (scores, penalties, round time, competitor names, status badge)
- **`RingPanel`** — listens for `MATCH_STARTED` and triggers a REST reload of the available matches list

The WebSocket reconnects automatically every 3 seconds on drop. REST calls run on background threads; all UI updates go through `Platform.runLater`.

---

## Configuration Reference

| Key | Description |
|---|---|
| `app.language` | UI language (`en`, `gl`, …) |
| `app.css` | Path to the custom stylesheet |
| `api.base.url` | Base URL of the WT-OVR-Bridge REST API |
| `api.websocket.url` | Base URL for WebSocket connections |

---

## Tech Stack

- [JavaFX 21](https://openjfx.io/) — UI framework
- [AtlantaFX](https://github.com/mkpaz/atlantafx) — base theme (PrimerDark)
- [Jackson 2.18](https://github.com/FasterXML/jackson) — JSON deserialization
- [AnimateFX](https://github.com/Typhon0/AnimateFX) — UI animations
- [Google GenAI Java SDK](https://github.com/googleapis/java-genai) — Gemini API client
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

This project is licensed under the **GNU Affero General Public License v3.0 (AGPL-3.0)**.

**Copyright © 2026 Alex Pazos Amoedo**

You are free to use, study, modify and distribute this software under the terms of the AGPL-3.0. Any modified version distributed or made available over a network must also be released under the same license with its source code publicly accessible. See the [LICENSE](LICENSE) file for the full terms.

---

## Distribution

Any distribution of this software, modified or unmodified, must:

- Include the original copyright notice and a copy of the AGPL-3.0 license
- Make the complete corresponding source code available under the same license
- If the software is run over a network, provide users access to the source code

For questions about licensing or commercial use, contact the author:

- **Author:** Alex Pazos Amoedo
- **Email:** pazex04@gmail.com
