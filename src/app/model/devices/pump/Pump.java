package app.model.devices.pump;

import app.model.devices.pump.tecanapi.TecanXCalibur;
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
    private IntegerProperty numPorts;
    private StringProperty pumpName;
    private TecanXCalibur pumpAPI;

    public Pump(String portName, TecanXCalibur pump) {
        this.pumpAPI = pump;

        this.pumpPortList = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.syringeVolume = new SimpleIntegerProperty(1000);
        this.numPorts = new SimpleIntegerProperty(9);
        this.pumpName = new SimpleStringProperty(portName);

        this.generatePumpPorts();
    }

    private void generatePumpPorts() {
        for (int i = 0; i < this.numPorts.get(); i++)
            this.addPumpPort(new PumpPort(this, i+1));
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

    public int getNumPorts() { return numPorts.get(); }

    public IntegerProperty numPortsProperty() { return numPorts; }

    public void setNumPorts(int numPorts) { this.numPorts.set(numPorts); }

    public String getPumpName() { return pumpName.get(); }

    public StringProperty pumpNameProperty() { return pumpName; }

    public void setPumpName(String pumpName) { this.pumpName.set(pumpName); }

    public TecanXCalibur getPumpAPI() {
        return pumpAPI;
    }

    public void setPumpAPI(TecanXCalibur pumpAPI) {
        this.pumpAPI = pumpAPI;
    }
}
