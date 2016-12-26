package app.model.pump;

import javafx.beans.property.*;

/**
 * Created by alexskrynnyk on 12/20/16.
 */
public class PumpPort {

    private IntegerProperty portNum;
    private StringProperty portName;
    private ObjectProperty<PortType> portType;

    public PumpPort(int portNum) {
        this.portNum = new SimpleIntegerProperty(portNum);
        this.portName = new SimpleStringProperty("");
        this.portType = new SimpleObjectProperty<>(PortType.EMPTY);
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
}
