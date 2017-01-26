package app.controller.protocol.commands;

import app.model.devices.pump.PortType;
import app.model.devices.pump.Pump;
import app.model.devices.pump.PumpPort;
import app.model.protocol.Protocol;
import app.utility.Util;
import app.controller.protocol.SettingsController;
import app.model.protocol.ProtocolComponent;
import app.model.protocol.commands.OneWayCommand;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/21/16
 */
public class OneWayCommandSettingsController implements SettingsController {

    @FXML private ComboBox<String> fromPort;
    @FXML private ComboBox<String> toPort;
    @FXML private ComboBox<Integer> extractSpeed;
    @FXML private TextField volume;
    @FXML private ComboBox<Integer> dispenseSpeed;

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

        List<String> fromPortNames = new ArrayList<>();
        for (PumpPort port : this.getPumpPortList()) {
            if (port.getPortType() == PortType.REAGENT ||
                    port.getPortType() == PortType.WASH ||
                    port.getPortType() == PortType.OUTPUT)
                fromPortNames.add(port.getPortName());
        }

        this.fromPort.setItems(FXCollections.observableList(fromPortNames));

        this.fromPort.setValue((this.command.getFromPort() == null) ? "Empty" : this.command.getFromPort().getPortName());
        this.toPort.setValue((this.command.getToPort() == null) ? "Empty" : this.command.getToPort().getPortName());
        this.volume.setText(String.valueOf(this.command.getVolume()));


        List<Integer> speeds = new ArrayList<>();
        for (int i = 0; i < 36; i++)
            speeds.add(i);

        this.extractSpeed.setItems(FXCollections.observableList(speeds));
        this.dispenseSpeed.setItems(FXCollections.observableList(speeds));

        this.extractSpeed.setValue(this.command.getExtractSpeed());
        this.dispenseSpeed.setValue(this.command.getDispenseSpeed());
    }

    private void bindModelToControls() {
        this.fromPort.getSelectionModel().selectedItemProperty().addListener((item, oldVal, newVal) -> {
            for (PumpPort port : this.getPumpPortList()) {
                if (port.getPortName().equals(newVal)) {
                    this.command.setFromPort(port);
                    this.updateCommandName();
                    this.command.setPump(port.getPump());
                }
            }

            List<String> toPortNames = new ArrayList<>();
            for (PumpPort port : this.command.getFromPort().getPump().getPumpPortList()) {
                if (port.getPortType() == PortType.REAGENT ||
                        port.getPortType() == PortType.WASH ||
                        port.getPortType() == PortType.OUTPUT ||
                        port.getPortType() == PortType.WASTE)
                    toPortNames.add(port.getPortName());
            }

            this.toPort.setItems(FXCollections.observableList(toPortNames));
        });

        this.toPort.getSelectionModel().selectedItemProperty().addListener((item, oldVal, newVal) -> {
            for (PumpPort port : this.command.getFromPort().getPump().getPumpPortList()) {
                if (port.getPortName().equals(newVal)) {
                    this.command.setToPort(port);
                    this.updateCommandName();
                }
            }
        });

        Util.restrictTextFieldLength(this.volume, 4);
        Util.confineTextFieldToNumericAndBind(this.volume, this.command::setVolume);

        this.extractSpeed.getSelectionModel().selectedItemProperty().addListener((item, oldVal, newVal) -> {
            this.command.setExtractSpeed(newVal);
        });

        this.dispenseSpeed.getSelectionModel().selectedItemProperty().addListener((item, oldVal, newVal) -> {
            this.command.setDispenseSpeed(newVal);
        });
    }

    private void updateCommandName() {
        this.command.setName(String.format("Dispense '%s' to '%s'",
                (this.command.getFromPort() == null) ? "Empty" : this.command.getFromPort().getPortName(),
                (this.command.getToPort() == null) ? "Empty" : this.command.getToPort().getPortName()));
    }


    private ArrayList<PumpPort> getPumpPortList() {
        return this.command.getPumpList()
                .stream()
                .map(Pump::getPumpPortList)
                .collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
    }

}
