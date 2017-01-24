package app.model.protocol;

import app.model.protocol.commands.CommandExecutionException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/20/16
 */
public interface ProtocolComponent {

    Status getStatus();
    ObjectProperty<Status> statusProperty();
    void setStatus(Status status);

    String getName();
    StringProperty nameProperty();
    void setName(String newName);

    int getDuration();
    IntegerProperty durationProperty();

    void execute() throws CommandExecutionException;
}
