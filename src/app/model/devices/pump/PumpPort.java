package app.model.devices.pump;

import javafx.beans.property.*;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/20/16
 */
public class PumpPort {

    private IntegerProperty portNum;
    private StringProperty portName;
    private ObjectProperty<PortType> portType;
    private ObjectProperty<Pump> pump;

    public PumpPort(Pump pump, int portNum) {
        this.portNum = new SimpleIntegerProperty(portNum);
        this.portName = new SimpleStringProperty("");
        this.portType = new SimpleObjectProperty<>(PortType.EMPTY);
        this.pump = new SimpleObjectProperty<>(pump);
    }

    public int getPortNum() {
        return portNum.get();
    }

    public IntegerProperty portNumProperty() {
        return portNum;
    }

    public void setPortNum(int portNum) {
        this.portNum.set(portNum);
    }

    public String getPortName() {
        return portName.get();
    }

    public StringProperty portNameProperty() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName.set(portName);
    }

    public PortType getPortType() {
        return portType.get();
    }

    public ObjectProperty<PortType> portTypeProperty() {
        return portType;
    }

    public void setPortType(PortType portType) {
        this.portType.set(portType);
    }

    public Pump getPump() { return pump.get(); }

    public ObjectProperty<Pump> pumpProperty() { return pump; }

    public void setPump(Pump pump) { this.pump.set(pump); }
}
