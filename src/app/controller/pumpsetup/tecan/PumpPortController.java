package app.controller.pumpsetup.tecan;

import app.model.pump.PortType;
import app.model.pump.PumpPort;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/25/16
 */
public class PumpPortController {

    @FXML private Label portNumber;
    @FXML private TextField portName;
    @FXML private ComboBox portType;

    @FXML
    public void initialize() {
    }

    public void configure(PumpPort port, int number) {
        this.portName.setText(port.getPortName());

        port.portNameProperty().bind(this.portName.textProperty());

        this.portNumber.setText(number+")");

        this.setupControls();

        this.portType.setValue(port.getPortType().getName());

        this.portType.getSelectionModel().selectedItemProperty().addListener((item, oldVal, newVal) -> {
            port.setPortType(PortType.getByName(newVal.toString()));
        });
    }

    private void setupControls() {
        this.portType.setItems(FXCollections.observableList(
                Arrays.stream(
                        PortType.values())
                        .map(p -> p.getName())
                        .collect(Collectors.toList())
                )
        );
    }

}
