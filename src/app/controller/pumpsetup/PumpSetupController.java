package app.controller.pumpsetup;

import app.controller.pumpsetup.tecan.PumpController;
import app.controller.pumpsetup.tecan.PumpPortController;
import app.model.pump.Pump;
import app.model.pump.PumpManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
            for(Pump pump : this.pumpManager.getPumpList()) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/pumpsetup/tecan/pump.fxml")
                );

                Node controls = loader.load();

                PumpController controller = loader.getController();
                controller.configure(pump);

                this.pumps.getChildren().add(controls);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
