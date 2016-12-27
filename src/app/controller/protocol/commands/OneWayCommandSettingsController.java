package app.controller.protocol.commands;

import app.controller.protocol.SettingsController;
import app.model.protocol.ProtocolComponent;
import app.model.protocol.commands.OneWayCommand;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

import java.util.ArrayList;

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

        this.fromPort.setValue(this.command.getFromPort().getPortName());
        this.toPort.setValue(this.command.getToPort().getPortName());
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


        this.volume.lengthProperty().addListener((item, oldValue, newValue) -> {
            if (newValue.intValue() > oldValue.intValue()) {
                // Check if the new character is greater than 4
                if (this.volume.getText().length() >= 4) {

                    // if it's 11th character then just setText to previous one
                    this.volume.setText(this.volume.getText().substring(0, 4));
                }
            }
        });
        this.volume.textProperty().addListener((item, oldVal, newVal) -> {
            // if newVal is an integer and is not empty, then parseInt and setVolume
            if (newVal.matches("\\d*") && !newVal.equals("") ) {
                Integer newVolume = Integer.parseInt(newVal);
                this.command.setVolume(newVolume);
            } else {
                this.volume.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });


        this.toPort.getSelectionModel().selectedItemProperty().addListener((item, oldVal, newVal) -> {
            //TODO: set cmd's extractSpeed
        });


        this.toPort.getSelectionModel().selectedItemProperty().addListener((item, oldVal, newVal) -> {
            //TODO: set cmd's dispenseSpeed
        });
    }

}
