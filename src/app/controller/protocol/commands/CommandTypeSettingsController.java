package app.controller.protocol.commands;

import app.controller.protocol.SettingsController;
import app.model.protocol.ProtocolComponent;
import app.model.protocol.commands.Command;
import app.model.protocol.commands.CommandType;
import app.model.protocol.commands.OneWayCommand;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/21/16
 */
public class CommandTypeSettingsController implements SettingsController {

    @FXML private ComboBox<String> type;

    private Command command;

    @FXML
    public void initialize() {
        List list = Arrays.stream(CommandType.values())
                .map(CommandType::getFullName)
                .collect(Collectors.toList());
        // FXCollections.observableList(list)
        this.type.setItems(FXCollections.observableList(list));

        this.configure(new OneWayCommand());
    }

    public void configure(ProtocolComponent cmd) {
        this.command = (Command) cmd;
        this.type.setValue(CommandType.valueOf(cmd.getClass().getSimpleName()).getFullName());
    }

    public String getCommandType() {
        return this.type.getValue();
    }

}
