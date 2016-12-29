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

    private PumpPort pumpPort;

    @FXML
    public void initialize() {
    }

    public void configure(PumpPort port, int number) {
        this.pumpPort = port;

        this.setupControls(number);

        this.bindModelToControls();
    }

    private void setupControls(int number) {
        this.portNumber.setText(number+")");

        this.portName.setText(this.pumpPort.getPortName());

        this.portType.setItems(FXCollections.observableList(
                Arrays.stream(
                        PortType.values())
                        .map(PortType::getName)
                        .collect(Collectors.toList())
                )
        );

        this.portType.setValue(this.pumpPort.getPortType().getName());
        if (this.portType.getValue() == PortType.EMPTY.getName()) {
            this.portName.setText("");
            this.portName.setDisable(true);
        }
    }

    private void bindModelToControls() {
        this.pumpPort.portNameProperty().bind(this.portName.textProperty());

        this.portType.getSelectionModel().selectedItemProperty().addListener((item, oldVal, newVal) -> {
            if (newVal == PortType.EMPTY.getName()) {
                this.portName.setText("");
                this.portName.setDisable(true);
            } else {
                this.portName.setDisable(false);
            }
            this.pumpPort.setPortType(PortType.getByName(newVal.toString()));
        });
    }

}
