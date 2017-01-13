package app.controller.pumpsetup.tecan;

import app.model.devices.MaximumAttemptsException;
import app.model.devices.pump.tecanapi.SyringeCommandException;
import app.model.devices.pump.tecanapi.SyringeTimeoutException;
import app.utility.Util;
import app.model.devices.pump.Pump;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/25/16
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

        this.bindModelToControls();

        this.loadPortControls();
    }

    private void setupPumpControls() {
        this.pumpTitledFrame.setText(this.pump.getPumpName());

        this.syringeVolume.setValue(this.pump.getSyringeVolume());
        List<Integer> syringeVolumes = new ArrayList<>();
        syringeVolumes.add(250);
        syringeVolumes.add(500);
        syringeVolumes.add(1000);
        syringeVolumes.add(5000);
        this.syringeVolume.setItems(FXCollections.observableList(syringeVolumes));

        this.numPorts.setText(String.valueOf(this.pump.getPumpPortList().size()));
    }

    private void bindModelToControls() {
        this.syringeVolume.getSelectionModel().selectedItemProperty().addListener((item, oldVal, newVal) -> {
            this.pump.setSyringeVolume(Integer.valueOf(newVal.toString()));
        });

        Util.restrictTextFieldLength(this.numPorts, 1);
        Util.confineTextFieldToNumericAndBind(this.numPorts, this.pump::setNumPorts);
    }

    private void loadPortControls() {
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

    @FXML
    public void handleInit() {
        try {

            this.pump.getPumpAPI().connect();

            this.pump.getPumpAPI().init();

            this.pump.getPumpAPI().disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MaximumAttemptsException e) {
            e.printStackTrace();
        } catch (SyringeTimeoutException e) {
            e.printStackTrace();
        } catch (SyringeCommandException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handlePlungerDown() {
        try {

            this.pump.getPumpAPI().connect();

            this.pump.getPumpAPI().setSpeed(11);
            this.pump.getPumpAPI().extract(1, this.pump.getSyringeVolume());
            this.pump.getPumpAPI().executeChain();

            this.pump.getPumpAPI().disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MaximumAttemptsException e) {
            e.printStackTrace();
        } catch (SyringeTimeoutException e) {
            e.printStackTrace();
        } catch (SyringeCommandException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handlePlungerUp() {
        try {

            this.pump.getPumpAPI().connect();

            this.pump.getPumpAPI().setSpeed(11);
            this.pump.getPumpAPI().dispense(1, this.pump.getSyringeVolume());
            this.pump.getPumpAPI().executeChain();

            this.pump.getPumpAPI().disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MaximumAttemptsException e) {
            e.printStackTrace();
        } catch (SyringeTimeoutException e) {
            e.printStackTrace();
        } catch (SyringeCommandException e) {
            e.printStackTrace();
        }
    }

}
