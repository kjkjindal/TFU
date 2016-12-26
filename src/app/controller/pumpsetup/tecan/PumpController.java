package app.controller.pumpsetup.tecan;

import app.model.pump.Pump;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Created by alexskrynnyk on 12/25/16.
 */
public class PumpController {

    @FXML private TitledPane pumpTitledFrame;
    @FXML private ComboBox syringeVolume;
    @FXML private TextField numPorts;
    @FXML private VBox ports;

    private Pump pump;

    @FXML
    public void initialize() {

    }

    public void configure(Pump pump) {
        this.pump = pump;

        this.setupPumpControls();

        this.createPortControls();
    }

    private void setupPumpControls() {
        this.syringeVolume.setValue(this.pump.getSyringeVolume());
        this.numPorts.setText(this.pump.getPumpPortList().size()+"");
    }

    private void createPortControls() {
        try {
            for(int i = 0; i < this.pump.getPumpPortList().size(); i++) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/pumpsetup/tecan/pumpPort.fxml")
                );

                Node controls = loader.load();

                PumpPortController controller = loader.getController();
                controller.configure(pump.getPumpPort(i), i+1);

                this.ports.getChildren().add(controls);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
