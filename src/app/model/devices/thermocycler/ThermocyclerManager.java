package app.model.devices.thermocycler;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 1/8/17
 */

import app.model.devices.thermocycler.arduinoapi.ArduinoThermocyclerManager;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ThermocyclerManager {

    private ListProperty<Thermocycler> thermocyclerList;
    private ArduinoThermocyclerManager arduinoThermocyclerManager;

    public ThermocyclerManager() {
        this.thermocyclerList = new SimpleListProperty<>(FXCollections.observableArrayList());

        this.arduinoThermocyclerManager = new ArduinoThermocyclerManager();

        try {

            this.thermocyclerList.addAll(
                    this.generateThermocyclersFromAPI(
                            this.arduinoThermocyclerManager.getSerialArduinoThermocyclers(
                                    this.arduinoThermocyclerManager.findSerialArduinoThermocyclers()
                            )
                    )
            );

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private List<Thermocycler> generateThermocyclersFromAPI(List<ArduinoThermocyclerManager.Thermocycler> thermocyclers) {
        List<Thermocycler> thermocyclerList = new ArrayList<>();

        for (ArduinoThermocyclerManager.Thermocycler thermocycler : thermocyclers)
            thermocyclerList.add(new Thermocycler(thermocycler.getPortName(), thermocycler.getThermocycler()));

        return thermocyclerList;
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
}

