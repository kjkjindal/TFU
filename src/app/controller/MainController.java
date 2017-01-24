package app.controller;

import app.controller.protocol.ProtocolController;
import app.controller.pumpsetup.PumpSetupController;
import app.model.devices.pump.PumpManager;
import app.model.devices.thermocycler.ThermocyclerManager;
import app.model.protocol.commands.CommandFactory;
import javafx.fxml.FXML;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/20/16
 */
public class MainController {

    @FXML private ProtocolController protocolController;
    @FXML private PumpSetupController pumpSetupController;

    private ThermocyclerManager thermocyclerManager;
    private PumpManager pumpManager;

    @FXML
    public void initialize() {
        this.thermocyclerManager = new ThermocyclerManager();
        this.pumpManager = new PumpManager();

        this.pumpSetupController.configure(this.pumpManager);
        this.protocolController.configure(new CommandFactory(this.pumpManager, this.thermocyclerManager));
    }

    public ProtocolController getProtocolController() {
        return this.protocolController;
    }

    public PumpSetupController getPumpSetupController() {
        return this.pumpSetupController;
    }

}
