package app.controller.protocol;

import app.model.protocol.ProtocolComponent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/22/16
 */
public class CycleSettingsController  implements SettingsController {

    @FXML private TextField name;
    private ProtocolComponent component;

    @FXML
    public void initialize() {

    }

    public void configure(ProtocolComponent component) {
        this.name.setText(component.getName());

        this.component = component;

        this.component.nameProperty().bind(this.name.textProperty());
    }

    public void unbind() {
        this.component.nameProperty().unbind();
    }
}
