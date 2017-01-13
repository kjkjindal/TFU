package app.controller;

import app.controller.protocol.ProtocolController;
import app.controller.pumpsetup.PumpSetupController;
import javafx.fxml.FXML;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/20/16
 */
public class MainController {

    @FXML private ProtocolController protocolController;
    @FXML private PumpSetupController pumpSetupController;

    @FXML
    public void initialize() {

    }

    public ProtocolController getProtocolController() {
        return this.protocolController;
    }

    public PumpSetupController getPumpSetupController() {
        return this.pumpSetupController;
    }

}
