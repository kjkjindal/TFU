package app.controller.protocol.commands;

import app.controller.protocol.SettingsController;
import app.model.protocol.ProtocolComponent;
import app.model.protocol.commands.WaitCommand;
import app.utility.Util;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 1/24/17
 */
public class WaitCommandSettingsController implements SettingsController{

    @FXML private TextField hours;
    @FXML private TextField minutes;
    @FXML private TextField seconds;

    private WaitCommand command;

    @Override
    public void configure(ProtocolComponent component) {
        this.command = (WaitCommand) component;

        this.setupControls();

        this.bindModelToControls();
    }

    private void setupControls() {

        this.hours.setText(String.valueOf(this.command.getHours()));
        this.minutes.setText(String.valueOf(this.command.getMinutes()));
        this.seconds.setText(String.valueOf(this.command.getSeconds()));

    }

    private void bindModelToControls() {
        Util.restrictTextFieldLength(this.hours, 2);
        Util.confineTextFieldToNumericAndBind(this.hours, this.command::setHours);

        Util.restrictTextFieldLength(this.minutes, 2);
        Util.confineTextFieldToNumericAndBind(this.minutes, this.command::setMinutes);

        Util.restrictTextFieldLength(this.seconds, 2);
        Util.confineTextFieldToNumericAndBind(this.seconds, this.command::setSeconds);
    }


}
