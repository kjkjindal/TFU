package app.model.devices.pump;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/20/16
 */
public class Pump {

    private ListProperty<PumpPort> pumpPortList;
    private IntegerProperty syringeVolume;
    private IntegerProperty numPortrs;
    private StringProperty pumpName;

    public Pump() {
        this.pumpPortList = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.syringeVolume = new SimpleIntegerProperty(1000);
        this.numPortrs = new SimpleIntegerProperty(9);
        this.pumpName = new SimpleStringProperty("New pump");
    }

    public void addPumpPort(PumpPort port) {
        this.pumpPortList.add(port);
    }

    public PumpPort getPumpPort(int index) { return this.pumpPortList.get(index); }

    public ObservableList<PumpPort> getPumpPortList() {
        return pumpPortList.get();
    }

    public ListProperty<PumpPort> pumpPortListProperty() {
        return pumpPortList;
    }

    public void setPumpPortList(ObservableList<PumpPort> pumpPortList) { this.pumpPortList.set(pumpPortList); }

    public int getSyringeVolume() {
        return syringeVolume.get();
    }

    public IntegerProperty syringeVolumeProperty() {
        return syringeVolume;
    }

    public void setSyringeVolume(int syringeVolume) { this.syringeVolume.set(syringeVolume); }

    public int getNumPortrs() { return numPortrs.get(); }

    public IntegerProperty numPortrsProperty() { return numPortrs; }

    public void setNumPortrs(int numPortrs) { this.numPortrs.set(numPortrs); }

    public String getPumpName() { return pumpName.get(); }

    public StringProperty pumpNameProperty() { return pumpName; }

    public void setPumpName(String pumpName) { this.pumpName.set(pumpName); }
}
