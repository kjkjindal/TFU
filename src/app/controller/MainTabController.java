package app.controller;

import app.controller.protocol.ProtocolController;
import app.controller.pumpsetup.PumpSetupController;
import javafx.fxml.FXML;

/**
 * Created by alexskrynnyk on 12/20/16.
 */
public class MainTabController {

    @FXML
    ProtocolController protocolController;
    @FXML
    PumpSetupController pumpSetupController;

    @FXML
    public void initialize() {

    }

    public ProtocolController getProtocolController() {
        return this.protocolController;
    }

}
