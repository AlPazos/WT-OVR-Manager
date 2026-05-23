package com.pazos.wtovrmanager.controller;

import animatefx.animation.AnimationFX;
import com.pazos.wtovrmanager.I18n;
import com.pazos.wtovrmanager.component.RingPanel;
import com.pazos.wtovrmanager.component.SelectableCard;
import com.pazos.wtovrmanager.model.backendModels.Athlete;
import com.pazos.wtovrmanager.model.backendModels.Category;
import com.pazos.wtovrmanager.model.backendModels.Match;
import com.pazos.wtovrmanager.service.ApiService;
import com.pazos.wtovrmanager.service.GeminiService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class MainController {

    @FXML private HBox ringsContainer;
    @FXML private ScrollPane ringsScroll;
    @FXML private ScrollPane generatorScroll;
    @FXML private FlowPane categoriesContainer;
    @FXML private FlowPane athletesContainer;
    @FXML private ScrollPane uploadScroll;
    @FXML private PasswordField apiKeyField;
    @FXML private VBox dropZone;
    @FXML private Label fileNameLabel;
    @FXML private Label uploadStatusLabel;
    @FXML private ProgressIndicator uploadProgress;
    @FXML private Button generateButton;
    @FXML private Button uploadButton;

    private final ApiService api = new ApiService();
    private File pendingPdf;
    private String generatedJson;
    private String wsUrl;
    private Category categoryNewMatch;
    private SelectableCard<Category> selectedCategoryCard;
    private SelectableCard<Athlete> blueCard;
    private SelectableCard<Athlete> redCard;
    private Match newMatch;

    @FXML
    public void initialize() {
        Properties config = new Properties();
        try (InputStream is = getClass().getResourceAsStream("/config.properties")) {
            config.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        wsUrl = config.getProperty("api.websocket.url", "");
        initDropZone();
        loadRings();
    }

    private void initDropZone() {
        dropZone.getChildren().forEach(child -> child.setMouseTransparent(true));

        dropZone.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.COPY);
            event.consume();
        });

        dropZone.setOnDragEntered(event -> {
            dropZone.getStyleClass().add("drop-zone-active");
            event.consume();
        });

        dropZone.setOnDragExited(event -> {
            dropZone.getStyleClass().remove("drop-zone-active");
            event.consume();
        });

        dropZone.setOnDragDropped(event -> {
            dropZone.getStyleClass().remove("drop-zone-active");
            List<File> files = event.getDragboard().getFiles();
            if (!files.isEmpty()) {
                File file = files.get(0);
                if (file.getName().toLowerCase().endsWith(".pdf")) {
                    pendingPdf = file;
                    generatedJson = null;
                    fileNameLabel.setText(file.getName());
                    uploadStatusLabel.setText("");
                    generateButton.setDisable(false);
                    uploadButton.setDisable(true);
                } else {
                    uploadStatusLabel.setText(I18n.get("upload.status.invalid.file"));
                }
            }
            event.setDropCompleted(true);
            event.consume();
        });
    }

    @FXML
    private void browsePdf(MouseEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select tournament PDF");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File file = chooser.showOpenDialog(dropZone.getScene().getWindow());
        if (file != null) {
            pendingPdf = file;
            generatedJson = null;
            fileNameLabel.setText(file.getName());
            uploadStatusLabel.setText("");
            generateButton.setDisable(false);
            uploadButton.setDisable(true);
        }
    }

    @FXML
    private void generate() {
        if (pendingPdf == null) return;
        String apiKey = apiKeyField.getText().trim();
        if (apiKey.isEmpty()) {
            uploadStatusLabel.setText(I18n.get("upload.status.no.key"));
            return;
        }

        generateButton.setDisable(true);
        uploadButton.setDisable(true);
        uploadProgress.setVisible(true);
        uploadStatusLabel.setText(I18n.get("upload.status.extracting"));
        File pdfFile = pendingPdf;

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());
                GeminiService gemini = new GeminiService(apiKey, "gemini-2.5-flash");
                return gemini.extractMAtches(pdfBytes);
            }
        };

        task.setOnSucceeded(e -> {
            generatedJson = task.getValue();
            try {
                Files.writeString(java.nio.file.Path.of("tournament_output.json"), generatedJson);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            uploadProgress.setVisible(false);
            uploadStatusLabel.setText(I18n.get("upload.status.generated"));
            generateButton.setDisable(false);
            uploadButton.setDisable(false);
        });

        task.setOnFailed(e -> {
            uploadProgress.setVisible(false);
            uploadStatusLabel.setText(I18n.get("upload.status.error", task.getException().getMessage()));
            generateButton.setDisable(false);
        });

        new Thread(task).start();
    }

    @FXML
    private void upload() {
        if (generatedJson == null) return;

        uploadButton.setDisable(true);
        uploadProgress.setVisible(true);
        uploadStatusLabel.setText(I18n.get("upload.status.uploading"));

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                api.uploadTournament(generatedJson);
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            uploadProgress.setVisible(false);
            uploadStatusLabel.setText(I18n.get("upload.status.done"));
            uploadButton.setDisable(false);
        });

        task.setOnFailed(e -> {
            uploadProgress.setVisible(false);
            uploadStatusLabel.setText(I18n.get("upload.status.error", task.getException().getMessage()));
            uploadButton.setDisable(false);
        });

        new Thread(task).start();
    }

    @FXML
    private void loadRings() {
        ringsContainer.getChildren().clear();

        Task<List<Match>> task = new Task<>() {
            @Override
            protected List<Match> call() throws Exception {
                return api.getMatches();
            }
        };

        task.setOnSucceeded(e -> {
            List<Match> matches = task.getValue();
            new TreeSet<>(
                matches.stream()
                    .map(m -> m.getMat() != null ? m.getMat() : 0)
                    .collect(Collectors.toList())
            ).forEach(ring -> ringsContainer.getChildren().add(new RingPanel(wsUrl, ring, api)));
        });

        task.setOnFailed(e -> {
            Label err = new Label(I18n.get("error.loading", task.getException().getMessage()));
            err.getStyleClass().add("label-subtitle");
            ringsContainer.getChildren().add(err);
        });

        new Thread(task).start();
    }

    @FXML
    private void loadRingsView() {
        showView("rings");
        loadRings();
    }

    @FXML
    private void newMatchGenerator() {
        showView("generator");
        categoriesContainer.getChildren().clear();
        athletesContainer.getChildren().clear();
        selectedCategoryCard = null;
        categoryNewMatch = null;
        blueCard = null;
        redCard  = null;
        loadSelectionCategory();
    }

    @FXML
    private void showTournamentUpload() {
        showView("upload");
    }

    private void loadSelectionCategory() {
        try {
            List<Category> categories = api.getCategories();
            categories.forEach(c -> categoriesContainer.getChildren().add(
                    new SelectableCard<>(c, (categorySelected, card) -> {
                        if (categoryNewMatch != null && categoryNewMatch.getId() == categorySelected.getId()) {
                            return;
                        }
                        if (selectedCategoryCard != null) {
                            selectedCategoryCard.deselect();
                        }
                        categoryNewMatch = categorySelected;
                        selectedCategoryCard = card;
                        card.selectBlue();
                        athletesContainer.getChildren().clear();
                        blueCard = null;
                        redCard  = null;
                        try {
                            List<Athlete> athletes = api.getAthletesCategory(categoryNewMatch.getId());
                            loadSelectionAthletes(athletes);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSelectionAthletes(List<Athlete> athletes) {
        athletes.forEach(a -> {
            SelectableCard<Athlete> cardNew = new SelectableCard<>(a, (athleteSelected, card) -> {
                if (card == blueCard) {
                    blueCard = null;
                    card.deselect();
                } else if (card == redCard) {
                    redCard = null;
                    card.deselect();
                } else if (blueCard == null) {
                    blueCard = card;
                    card.selectBlue();
                } else if (redCard == null) {
                    redCard = card;
                    card.selectRed();
                }
            });
            cardNew.setOpacity(0);
            athletesContainer.getChildren().add(cardNew);
            AnimationFX anim = new animatefx.animation.RotateInUpLeft(cardNew);
            anim.setSpeed(1.5);
            anim.setDelay(Duration.millis(100 * athletesContainer.getChildren().size()));
            anim.setOnFinished(event -> cardNew.setOpacity(1));
            anim.play();
        });
    }

    private void showView(String view) {
        ringsScroll.setVisible("rings".equals(view));
        ringsScroll.setManaged("rings".equals(view));
        generatorScroll.setVisible("generator".equals(view));
        generatorScroll.setManaged("generator".equals(view));
        uploadScroll.setVisible("upload".equals(view));
        uploadScroll.setManaged("upload".equals(view));
    }
}
