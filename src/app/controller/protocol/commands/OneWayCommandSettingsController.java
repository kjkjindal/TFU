package app.controller.protocol.commands;

import app.utility.Util;
import app.controller.protocol.SettingsController;
import app.model.protocol.ProtocolComponent;
import app.model.protocol.commands.OneWayCommand;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/21/16
 */
public class OneWayCommandSettingsController implements SettingsController {

    @FXML private ComboBox<String> fromPort;
    @FXML private ComboBox<String> toPort;
    @FXML private ComboBox<String> extractSpeed;
    @FXML private TextField volume;
    @FXML private ComboBox<String> dispenseSpeed;

    private OneWayCommand command;

    @FXML
    public void initialize() {

    }

    public void configure(ProtocolComponent component) {
        this.command = (OneWayCommand) component;

        this.setupControls();

        this.bindModelToControls();
    }

    private void setupControls() {
        //TODO: provide items for all comboboxes

        this.fromPort.setValue((this.command.getFromPort() == null) ? "Empty" : this.command.getFromPort().getPortName());
        this.toPort.setValue((this.command.getToPort() == null) ? "Empty" : this.command.getToPort().getPortName());
        this.volume.setText(String.valueOf(this.command.getVolume()));
        this.extractSpeed.setValue(String.valueOf(this.command.getExtractSpeed()));
        this.dispenseSpeed.setValue(String.valueOf(this.command.getDispenseSpeed()));
    }

    private void bindModelToControls() {
        this.fromPort.getSelectionModel().selectedItemProperty().addListener((item, oldVal, newVal) -> {
            //TODO: set cmd's fromPort
        });

        this.toPort.getSelectionModel().selectedItemProperty().addListener((item, oldVal, newVal) -> {
            //TODO: set cmd's toPort
        });

        Util.restrictTextFieldLength(this.volume, 4);
        Util.confineTextFieldToNumericAndBind(this.volume, this.command::setVolume);

        this.toPort.getSelectionModel().selectedItemProperty().addListener((item, oldVal, newVal) -> {
            //TODO: set cmd's extractSpeed
        });

        this.toPort.getSelectionModel().selectedItemProperty().addListener((item, oldVal, newVal) -> {
            //TODO: set cmd's dispenseSpeed
        });
    }

}
