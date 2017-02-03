package app.model.protocol.commands;

import app.model.Logger;
import app.model.devices.MaximumAttemptsException;
import app.model.devices.pump.tecanapi.SyringeCommandException;
import app.model.devices.thermocycler.Thermocycler;
import app.model.protocol.Status;
import app.utility.Util;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 1/18/17
 */
public class TemperatureCommand extends Command  {

    private ListProperty<Thermocycler> thermocyclerList;
    private ObjectProperty<Thermocycler> thermocycler;
    private IntegerProperty setpoint;
    private BooleanProperty reachTemp;

    public TemperatureCommand(ListProperty<Thermocycler> thermocyclerList) {
        super();

        this.thermocyclerList = thermocyclerList;
        this.thermocycler = new SimpleObjectProperty<>(null);
        this.setpoint = new SimpleIntegerProperty(25);
        this.reachTemp = new SimpleBooleanProperty(false);
    }

    @Override
    public void execute() throws CommandExecutionException {
        this.setStatus(Status.IN_PROGRESS);

        try {
            Logger.log("EXECUTING COMMAND: " + this.getName());
            Logger.log(" - Using thermocycler " + this.thermocycler.get().getThermocyclerName());
            Logger.log(" - Setting setpoint to " + this.setpoint.get());
            Logger.log(" - Waiting to reach setpoint: " + this.reachTemp.get());

            this.thermocycler.get().getThermocyclerAPI().connect();
            this.thermocycler.get().getThermocyclerAPI().setSetpoint(this.setpoint.get());
            this.thermocycler.get().getThermocyclerAPI().startTemperatureControl();
            if (this.reachTemp.get()) {
                float temp = 0;
                while (temp < this.setpoint.get()) {
                    temp = this.thermocycler.get().getThermocyclerAPI().getTemperature();
                    System.out.println("------------------------------- T: " + temp);
                    Util.sleepMillis(200);
                }
            }
            this.thermocycler.get().getThermocyclerAPI().disconnect();

            Logger.log("DONE EXECUTING");

        } catch (Exception e) {
            this.setStatus(Status.ERROR);
            System.out.println("msg: "+e.getMessage());
            throw new CommandExecutionException(e.getMessage(), e);
        }

        this.setStatus(Status.COMPLETE);
    }

    @Override
    public Element getXML(Document doc) {
        return null;
    }

    public ObservableList<Thermocycler> getThermocyclerList() {
        return thermocyclerList.get();
    }

    public ListProperty<Thermocycler> thermocyclerListProperty() {
        return thermocyclerList;
    }

    public void setThermocyclerList(ObservableList<Thermocycler> thermocyclerList) {
        this.thermocyclerList.set(thermocyclerList);
    }

    public int getSetpoint() {
        return setpoint.get();
    }

    public IntegerProperty setpointProperty() {
        return setpoint;
    }

    public void setSetpoint(int setpoint) {
        this.setpoint.set(setpoint);
    }

    public boolean isReachTemp() {
        return reachTemp.get();
    }

    public BooleanProperty reachTempProperty() {
        return reachTemp;
    }

    public void setReachTemp(boolean reachTemp) {
        this.reachTemp.set(reachTemp);
    }

    public Thermocycler getThermocycler() {
        return thermocycler.get();
    }

    public ObjectProperty<Thermocycler> thermocyclerProperty() {
        return thermocycler;
    }

    public void setThermocycler(Thermocycler thermocycler) {
        this.thermocycler.set(thermocycler);
    }
}
