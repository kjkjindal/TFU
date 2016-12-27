package app.model.pump;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/23/16
 */
public class PumpManager {

    private ListProperty<Pump> pumpList;

    public PumpManager() {
        this.pumpList = new SimpleListProperty<>(FXCollections.observableArrayList());

        Pump pump1 = new Pump();
        for(int i = 0; i < 9; i++) {
            PumpPort port = new PumpPort(pump1, i);
            port.setPortName("Pump ONE port "+i);
            pump1.addPumpPort(port);
        }

        this.pumpList.add(pump1);

        Pump pump2 = new Pump();
        for(int i = 0; i < 9; i++) {
            PumpPort port = new PumpPort(pump2, i);
            port.setPortName("Pump TWO port " + i);
            pump2.addPumpPort(port);
        }

        this.pumpList.add(pump2);
    }

    public ObservableList<Pump> getPumpList() {
        return pumpList.get();
    }

    public ListProperty<Pump> pumpListProperty() {
        return pumpList;
    }

    public void setPumpList(ObservableList<Pump> pumpList) { this.pumpList.set(pumpList); }
}
