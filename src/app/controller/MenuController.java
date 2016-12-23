package app.controller;

import javafx.fxml.FXML;

/**
 * Created by alexskrynnyk on 12/20/16.
 */
public class MenuController {

    private AppController appController;

    public void configure(AppController appController) {
        this.appController = appController;
    }

    @FXML
    private void about() {

    }

    @FXML
    private void preferences() {

    }

    @FXML
    private void newExperiment() {
        this.appController.newExperiment();
    }

    @FXML
    private void loadExperiment() {
        this.appController.loadExperiment();
    }

    @FXML
    private void saveExperiment() {
        this.appController.saveExperiment();
    }

    @FXML
    private void saveExperimentAs() {

    }

    @FXML
    private void quit() {
        this.appController.quit();
    }

    @FXML
    private void undo() {

    }

    @FXML
    private void redo() {

    }

    @FXML
    private void copy() {

    }

    @FXML
    private void cut() {

    }

    @FXML
    private void paste() {

    }

    @FXML
    private void selectAll() {

    }

    @FXML
    private void deleteSelected() {

    }

    @FXML
    private void deselectAll() {

    }

    @FXML
    private void groupSelected() {

    }

    @FXML
    private void ungroupSelected() {

    }

    @FXML
    private void execute() {

    }

    @FXML
    private void abort() {

    }

}
