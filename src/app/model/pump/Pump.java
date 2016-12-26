package app.model.pump;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by alexskrynnyk on 12/20/16.
 */
public class Pump {

    private ListProperty<PumpPort> pumpPortList;
    private IntegerProperty syringeVolume;

    public Pump() {
        this.pumpPortList = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.syringeVolume = new SimpleIntegerProperty(1000);
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

    public void setPumpPortList(ObservableList<PumpPort> pumpPortList) {
        this.pumpPortList.set(pumpPortList);
    }

    public int getSyringeVolume() {
        return syringeVolume.get();
    }

    public IntegerProperty syringeVolumeProperty() {
        return syringeVolume;
    }

    public void setSyringeVolume(int syringeVolume) {
        this.syringeVolume.set(syringeVolume);
    }
}
