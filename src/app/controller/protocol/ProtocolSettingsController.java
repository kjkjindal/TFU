package app.controller.protocol;

import app.model.protocol.Protocol;
import app.model.protocol.ProtocolComponent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/22/16
 */
public class ProtocolSettingsController implements SettingsController {

    @FXML private TextField name;

    private Protocol protocol;

    @FXML
    public void initialize() {

    }

    public void configure(ProtocolComponent component) {
        this.protocol = (Protocol) component;

        this.name.setText(this.protocol.getName());

        this.protocol.nameProperty().bind(this.name.textProperty());
    }

    public void unbind() {
        this.protocol.nameProperty().unbind();
    }

}
