package planverkehr.verkehrslinien;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class linienConfigWindow {
    private final Button saveButton = new Button();
    private final Button backButton = new Button();
    private TextField linienNameInputField;
    private Label speedNumberLabel;
    private Label cargoInfoLabel;
    private CheckBox circleCB;
    private final ComboBox fahrzeugInput = new ComboBox<>();
    private final linienConfigModel model;
    private Stage window;

    public linienConfigWindow(linienConfigModel model) {
        this.model = model;
    }

    public void display(String title) {
        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
        window.setMinHeight(200);

        GridPane layout = new GridPane();
        layout.setAlignment(Pos.CENTER);
        layout.setHgap(10);
        layout.setVgap(10);
        layout.setPadding(new Insets(25, 25, 25, 25));

        Label linienNameLabel = new Label("Linien Name:");
        layout.add(linienNameLabel, 0, 1);

        linienNameInputField = new TextField();
        linienNameInputField.getStyleClass().add("textfield-formInput");
        layout.add(linienNameInputField, 1, 1);

        Label fahrzeugLabel = new Label("Fahrzeug:");
        layout.add(fahrzeugLabel, 0, 2);

        fahrzeugInput.setItems(model.getOptions());
        fahrzeugInput.setValue(model.getOptions().get(0));
        fahrzeugInput.getStyleClass().add("textfield-formInput");
        layout.add(fahrzeugInput, 1, 2);


        Label seedInputInformation = new Label("Fahrzeuginformationen:");
        seedInputInformation.getStyleClass().add("label-seedLabel");
        layout.add(seedInputInformation, 0, 3, 2, 1);
        // layout.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);


        Label speedLabel = new Label("Speed:");
        layout.add(speedLabel, 0, 4);

        speedNumberLabel = new Label(model.selectedVehicle.getSpeed() + "");
        layout.add(speedNumberLabel, 1, 4);

        Label cargoLabel = new Label("Cargo:");
        layout.add(cargoLabel, 0, 5);

        cargoInfoLabel = new Label(model.getCargoString());
        layout.add(cargoInfoLabel, 1, 5);

        circleCB = new CheckBox("circle");
        circleCB.setSelected(false);
        layout.add(circleCB, 1, 6);

        saveButton.setText("SAVE");
        saveButton.getStyleClass().add("button-start-game");
        GridPane.setHalignment(saveButton, HPos.RIGHT);
        layout.add(saveButton, 1, 7);

        backButton.setText("Back");
        backButton.getStyleClass().add("");
        GridPane.setHalignment(saveButton, HPos.LEFT);
        layout.add(backButton, 0, 7);


        Scene scene = new Scene(layout);
        scene.getStylesheets().add("/planverkehr/layout.css");
        window.setScene(scene);
        window.showAndWait();
    }

    public ComboBox getFahrzeugInput() {
        return fahrzeugInput;
    }

    public Label getCargoInfoLabel() {
        return cargoInfoLabel;
    }

    public Label getSpeedNumberLabel() {
        return speedNumberLabel;
    }

    public Button getBackButton() {
        return backButton;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Stage getWindow() {
        return window;
    }

    public TextField getLinienNameInputField() {
        return linienNameInputField;
    }

    public CheckBox getCircleCB() {
        return circleCB;
    }
}
