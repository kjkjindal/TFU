package app.controller.protocol.commands;

import app.controller.protocol.SettingsController;
import app.model.devices.thermocycler.Thermocycler;
import app.model.protocol.ProtocolComponent;
import app.model.protocol.commands.TemperatureCommand;
import app.utility.Util;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 1/23/17
 */
public class TemperatureCommandSettingsController implements SettingsController {

    @FXML private ComboBox<String> thermocycler;
    @FXML private TextField setpoint;
    @FXML private CheckBox reachTemp;
    @FXML private Label reachTempLabel;

    private TemperatureCommand command;

    @FXML
    public void initialize() {

    }

    public void configure(ProtocolComponent component) {
        this.command = (TemperatureCommand) component;

        this.setupControls();

        this.bindModelToControls();
    }

    private void setupControls() {
        this.thermocycler.setValue((this.command.getThermocycler() == null) ? "Empty" : this.command.getThermocycler().getThermocyclerName());

        ArrayList<String> thermocyclerNames = new ArrayList<>();
        for (Thermocycler thermo : this.command.getThermocyclerList())
            thermocyclerNames.add(thermo.getThermocyclerName());

        this.thermocycler.setItems(FXCollections.observableList(thermocyclerNames));

        this.setpoint.setText(String.valueOf(this.command.getSetpoint()));

        this.reachTemp.setSelected(this.command.isReachTemp());
    }

    private void bindModelToControls() {
        this.thermocycler.getSelectionModel().selectedItemProperty().addListener((item, oldVal, newVal) -> {
            for (Thermocycler thermo : this.command.getThermocyclerList()) {
                if (thermo.getThermocyclerName().equals(newVal)) {
                    this.command.setThermocycler(thermo);
                    this.updateCommandName();
                }
            }
        });

        Util.restrictTextFieldLength(this.setpoint, 2);
        Util.confineTextFieldToNumericAndBind(this.setpoint, this.command::setSetpoint);
        this.setpoint.textProperty().addListener((item, oldVal, newVal) -> this.updateCommandName());

        this.command.reachTempProperty().bind(this.reachTemp.selectedProperty());

        this.reachTempLabel.textProperty().bind(this.setpoint.textProperty());
    }

    private void updateCommandName() {
        this.command.setName(String.format("Set temp.: '%s' to %d",
                (this.command.getThermocycler() == null) ? "Empty" : this.command.getThermocycler().getThermocyclerName(),
                Integer.parseInt(this.setpoint.getText())));
    }

}
