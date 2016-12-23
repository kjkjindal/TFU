package app.controller.protocol.commands;

import app.controller.protocol.SettingsController;
import app.model.protocol.ProtocolComponent;
import javafx.fxml.FXML;

/**
 * Created by alexskrynnyk on 12/21/16.
 */
public class OneWayCommandSettingsController implements SettingsController {

    @FXML
    private CommandTypeSettingsController commandTypeController;

    @FXML
    public void initialize() {

    }

    public void configure(ProtocolComponent component) {

    }

}
