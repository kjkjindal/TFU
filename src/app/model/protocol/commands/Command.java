package app.model.protocol.commands;

import app.model.Debugging;
import app.model.Logger;
import app.model.Saveable;
import app.model.protocol.ProtocolComponent;
import app.model.protocol.Status;
import javafx.beans.property.*;
import org.w3c.dom.Element;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/20/16
 */
public abstract class Command implements ProtocolComponent, Saveable {

    private StringProperty name;
    private IntegerProperty duration;
    private ObjectProperty<Status> status;

    public Command() {
        this.name = new SimpleStringProperty("New command");
        this.duration = new SimpleIntegerProperty(0);
        this.status = new SimpleObjectProperty<>(Status.NOT_STARTED);
    }

    public void setName(String newName) {
        if(newName != null)
            this.name.set(newName);

        if(Debugging.VERBOSE)
            Logger.log(this.name.get()+": setting 'name' to : "+newName);
    }

    @Override
    public String getName() {
        return this.name.get();
    }

    @Override
    public StringProperty nameProperty() {
        return this.name;
    }

    @Override
    public int getDuration() { return this.duration.get(); }

    @Override
    public IntegerProperty durationProperty() { return this.duration; }

    @Override
    public void setStatus(Status status) {
        if(status != null)
            this.status.set(status);

        if(Debugging.VERBOSE)
            Logger.log(this.name.get()+": setting 'status' to : "+status);
    }

    @Override
    public ObjectProperty<Status> statusProperty() { return this.status; }

    @Override
    public Status getStatus() { return this.status.get(); }

    @Override
    public abstract void execute();

}
