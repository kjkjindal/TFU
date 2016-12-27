package app.model.protocol.commands;

import app.model.Logger;
import app.model.protocol.Status;
import app.model.pump.Pump;
import app.model.pump.PumpPort;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/20/16
 */
public class OneWayCommand extends Command {

    private IntegerProperty volume;
    private IntegerProperty extractSpeed;
    private IntegerProperty dispenseSpeed;
    private ObjectProperty<Pump> pump;
    private ObjectProperty<PumpPort> fromPort;
    private ObjectProperty<PumpPort> toPort;

    public OneWayCommand() {
        super();

        this.volume = new SimpleIntegerProperty(0);
        this.extractSpeed = new SimpleIntegerProperty(15);
        this.dispenseSpeed = new SimpleIntegerProperty(15);
        this.pump = new SimpleObjectProperty<>(new Pump());
        this.fromPort = new SimpleObjectProperty<>(new PumpPort(this.pump.get(), 2));
        this.toPort = new SimpleObjectProperty<>(new PumpPort(this.pump.get(), 3));
    }

    public void setVolume(int newVolume) { this.volume.set(newVolume); }

    public int getVolume() { return  this.volume.get(); }

    public IntegerProperty volumeProperty() { return this.volume; }

    public void setExtractSpeed(int extractSpeed) { this.extractSpeed.set(extractSpeed); }

    public int getExtractSpeed() { return  this.extractSpeed.get(); }

    public IntegerProperty extractSpeedProperty() { return this.extractSpeed; }

    public void setDispenseSpeed(int dispenseSpeed) { this.dispenseSpeed.set(dispenseSpeed); }

    public int getDispenseSpeed() { return  this.dispenseSpeed.get(); }

    public IntegerProperty dispenseSpeedProperty() { return this.dispenseSpeed; }

    public void setPump(Pump pump) { this.pump.set(pump); }

    public Pump getPump() { return this.pump.get(); }

    public ObjectProperty<Pump> pumpProperty() { return this.pump; }

    public void setFromPort(PumpPort fromPort) { this.fromPort.set(fromPort); }

    public PumpPort getFromPort() { return this.fromPort.get(); }

    public ObjectProperty<PumpPort> fromPortProperty() { return this.fromPort; }

    public void setToPort(PumpPort toPort) { this.toPort.set(toPort); }

    public PumpPort getToPort() { return this.toPort.get(); }

    public ObjectProperty<PumpPort> toPortProperty() { return this.toPort; }

    @Override
    // TODO: make execution on separate thread
    public void execute() {
        this.setStatus(Status.IN_PROGRESS);
        try {
            Logger.log("Using pump " + this.pump);

            Logger.log("Setting extraction speed to " + this.extractSpeed.get());

            Logger.log("Extracting " + this.volume.get() + " uL from " + this.fromPort);

            Logger.log("Setting dispense speed to " + this.dispenseSpeed.get());

            Logger.log("Dispensing " + this.volume.get() + " uL to " + this.toPort);
        } catch (Exception e) {
            this.setStatus(Status.ERROR);
            throw e;
        }
        this.setStatus(Status.COMPLETE);
    }

    @Override
    public Element getXML(Document doc) {
        Element noteElement = doc.createElement("command");

        Attr type = doc.createAttribute("type");
        type.setValue(this.getClass().getSimpleName());
        noteElement.setAttributeNode(type);

        Attr name = doc.createAttribute("name");
        name.setValue(this.getName());
        noteElement.setAttributeNode(name);

        Attr volume = doc.createAttribute("volume");
        volume.setValue(String.valueOf(this.getVolume()));
        noteElement.setAttributeNode(volume);

        Attr extractSpeed = doc.createAttribute("extractSpeed");
        extractSpeed.setValue(String.valueOf(this.getExtractSpeed()));
        noteElement.setAttributeNode(extractSpeed);

        Attr dispenseSpeed = doc.createAttribute("dispenseSpeed");
        dispenseSpeed.setValue(String.valueOf(this.getDispenseSpeed()));
        noteElement.setAttributeNode(dispenseSpeed);

        Attr pump = doc.createAttribute("pump");
        pump.setValue("test pump string");
        noteElement.setAttributeNode(pump);

        Attr fromPort = doc.createAttribute("fromPort");
        fromPort.setValue(String.valueOf(this.getFromPort()));
        noteElement.setAttributeNode(fromPort);

        Attr toPort = doc.createAttribute("toPort");
        toPort.setValue(String.valueOf(this.getToPort()));
        noteElement.setAttributeNode(toPort);

        return noteElement;
    }
}
