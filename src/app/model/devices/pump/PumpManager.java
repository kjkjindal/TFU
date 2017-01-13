package app.model.devices.pump;

import app.model.devices.MaximumAttemptsException;
import app.model.devices.pump.tecanapi.*;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/23/16
 */
public class PumpManager {

    private ListProperty<Pump> pumpList;
    private TecanPumpManager tecanPumpManager;

    public PumpManager() {
        this.pumpList = new SimpleListProperty<>(FXCollections.observableArrayList());

        this.tecanPumpManager = new TecanPumpManager();

        try {

            this.pumpList.addAll(
                    this.generatePumpsFromAPI(
                            this.tecanPumpManager.getSerialTecanPumps(
                                    this.tecanPumpManager.findSerialTecanPumps()
                            )
                    )
            );

        } catch (SyringeCommandException e) {
            e.printStackTrace();
        } catch (SyringeTimeoutException e) {
            e.printStackTrace();
        } catch (MaximumAttemptsException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private List<Pump> generatePumpsFromAPI(List<TecanPumpManager.Pump> pumps) throws SyringeCommandException, SyringeTimeoutException, MaximumAttemptsException, IOException {
        List<Pump> pumpList = new ArrayList<>();

        for (TecanPumpManager.Pump pump : pumps) {
            Pump p = new Pump(pump.getPortName(), pump.getPump());

            pumpList.add(p);
        }

        return pumpList;
    }

    public ObservableList<Pump> getPumpList() {
        return pumpList.get();
    }

    public ListProperty<Pump> pumpListProperty() {
        return pumpList;
    }

    public void setPumpList(ObservableList<Pump> pumpList) {
        this.pumpList.set(pumpList);
    }
}
