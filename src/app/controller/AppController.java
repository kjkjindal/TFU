package app.controller;

import app.model.Saveable;
import app.model.XMLSaver;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppController {

    private Stage primaryStage;

    @FXML private MenuController menuBarController;
    @FXML private MainTabController mainTabsController;

    public AppController(Stage primaryStage) { this.primaryStage = primaryStage; }

    @FXML
    public void initialize() {
        this.primaryStage.setOnCloseRequest(e -> this.quit());

        this.menuBarController.configure(this);
    }

    public void newExperiment() {

    }

    public void loadExperiment() {

    }

    public void saveExperiment() {
        List<Saveable> toSave = new ArrayList<>();
        toSave.add(this.mainTabsController.getProtocolController().getProtocol());

        File file = new File("/Users/alexskrynnyk/Desktop/test.xml");
        try {
            XMLSaver.saveComposition(toSave, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void quit() {
        Platform.exit();
        System.exit(0);
    }

}
