package app.controller.pumpsetup;

import app.controller.pumpsetup.tecan.PumpController;
import app.model.devices.pump.Pump;
import app.model.devices.pump.PumpManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/20/16
 */
public class PumpSetupController {

    @FXML private HBox pumps;

    private PumpManager pumpManager;

    @FXML
    public void initialize() {
        this.configure(new PumpManager());
    }

    public void configure(PumpManager pumpManager) {
        this.pumpManager = pumpManager;

        this.setupControls();
    }

    private void setupControls() {
        try {
            if (this.pumpManager.getPumpList().size() > 0) {
                for (Pump pump : this.pumpManager.getPumpList()) {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/pumpsetup/tecan/pump.fxml")
                    );

                    Node controls = loader.load();

                    PumpController controller = loader.getController();
                    controller.configure(pump);

                    this.pumps.getChildren().add(controls);
                }
            } else {
                Label label = new Label("No pumps were found");

                this.pumps.getChildren().add(label);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
