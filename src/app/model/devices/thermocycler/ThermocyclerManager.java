//package app.model.devices.thermocycler;
//
///**
// * Project: FluidXMan
// * Author: alexskrynnyk
// * Date: 1/8/17
// */
//
//import app.model.devices.pump.Pump;
//import app.model.devices.MaximumAttemptsException;
//import app.model.devices.pump.tecanapi.SyringeCommandException;
//import app.model.devices.pump.tecanapi.SyringeTimeoutException;
//import app.model.devices.pump.tecanapi.TecanPumpManager;
//import app.model.devices.thermocycler.arduinoapi.ArduinoThermocyclerManager;
//import javafx.beans.property.ListProperty;
//import javafx.beans.property.SimpleListProperty;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ThermocyclerManager {
//
//    private ListProperty<Thermocycler> thermocyclerList;
//    private ArduinoThermocyclerManager arduinoThermocyclerManager;
//
//    public ThermocyclerManager() {
//        this.thermocyclerList = new SimpleListProperty<>(FXCollections.observableArrayList());
//
//        this.arduinoThermocyclerManager = new ArduinoThermocyclerManager();
//
//        try {
//
//            this.thermocyclerList.addAll(
//                    this.generateThermocyclersFromAPI(
//                            this.arduinoThermocyclerManager.getSerialTecanPumps(
//                                    this.arduinoThermocyclerManager.findSerialTecanPumps()
//                            )
//                    )
//            );
//
//        } catch (SyringeCommandException e) {
//            e.printStackTrace();
//        } catch (SyringeTimeoutException e) {
//            e.printStackTrace();
//        } catch (MaximumAttemptsException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private List<Pump> generateThermocyclersFromAPI(List<TecanPumpManager.Pump> pumps) {
//        List<Pump> pumpList = new ArrayList<>();
//
//        for (TecanPumpManager.Pump pump : pumps)
//            pumpList.add(new Pump(pump.getPortName(), pump.getPump()));
//
//        return pumpList;
//    }
//
//    public ObservableList<Pump> getPumpList() {
//        return pumpList.get();
//    }
//
//    public ListProperty<Pump> pumpListProperty() {
//        return pumpList;
//    }
//
//    public void setPumpList(ObservableList<Pump> pumpList) {
//        this.pumpList.set(pumpList);
//    }
//}
//
