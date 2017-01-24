package app.model.devices.thermocycler;

import app.model.devices.thermocycler.arduinoapi.ArduinoThermocycler;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 1/8/17
 */
public class Thermocycler {
    private IntegerProperty setpoint;
    private StringProperty thermocyclerName;
    private ArduinoThermocycler thermocyclerAPI;

    public Thermocycler(String name, ArduinoThermocycler thermocycler) {
        this.thermocyclerName = new SimpleStringProperty(name);
        this.thermocyclerAPI = thermocycler;
        this.setpoint = new SimpleIntegerProperty(25);
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

    public String getThermocyclerName() {
        return thermocyclerName.get();
    }

    public StringProperty thermocyclerNameProperty() {
        return thermocyclerName;
    }

    public void setThermocyclerName(String thermocyclerName) {
        this.thermocyclerName.set(thermocyclerName);
    }

    public ArduinoThermocycler getThermocyclerAPI() {
        return thermocyclerAPI;
    }

    public void setThermocyclerAPI(ArduinoThermocycler thermocyclerAPI) {
        this.thermocyclerAPI = thermocyclerAPI;
    }
}
