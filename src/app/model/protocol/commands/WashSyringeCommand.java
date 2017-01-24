package app.model.protocol.commands;

import app.model.Logger;
import app.model.devices.MaximumAttemptsException;
import app.model.devices.pump.Pump;
import app.model.devices.pump.PumpPort;
import app.model.devices.pump.tecanapi.SyringeCommandException;
import app.model.devices.pump.tecanapi.SyringeTimeoutException;
import app.model.protocol.Status;
import app.utility.Util;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 1/1/17
 */
public class WashSyringeCommand extends Command{

    private IntegerProperty volume;
    private IntegerProperty extractSpeed;
    private IntegerProperty dispenseSpeed;
    private ListProperty<Pump> pumpList;
    private ObjectProperty<Pump> pump;
    private ObjectProperty<PumpPort> fromPort;
    private ObjectProperty<PumpPort> toPort;

    public WashSyringeCommand(ListProperty<Pump> pumpList) {
        super();

        this.pumpList = pumpList;

        this.volume = new SimpleIntegerProperty(0);
        this.extractSpeed = new SimpleIntegerProperty(15);
        this.dispenseSpeed = new SimpleIntegerProperty(15);
        this.pump = new SimpleObjectProperty<>(null);
        this.fromPort = new SimpleObjectProperty<>(null);
        this.toPort = new SimpleObjectProperty<>(null);
    }

    @Override
    public void execute() throws CommandExecutionException{
        this.setStatus(Status.IN_PROGRESS);

        try {
            Logger.log("EXECUTING COMMAND: " + this.getName());
            Logger.log(" - Using pump " + this.pump.get().getPumpName());
            Logger.log(" - Setting extraction speed to " + this.extractSpeed.get());
            Logger.log(" - Extracting " + this.volume.get() + " uL from " + this.fromPort);
            Logger.log(" - Setting dispense speed to " + this.dispenseSpeed.get());
            Logger.log(" - Dispensing " + this.volume.get() + " uL to " + this.toPort);
            Logger.log("DONE EXECUTING");

            this.pump.get().getPumpAPI().connect();

            /*
            this.pump.get().getPumpAPI().setSpeed(this.extractSpeed.get());
            this.pump.get().getPumpAPI().extract(this.fromPort.get().getPortNum(), this.volume.get());
            this.pump.get().getPumpAPI().setSpeed(this.dispenseSpeed.get());
            this.pump.get().getPumpAPI().dispense(this.toPort.get().getPortNum(), this.volume.get());
            */

            double duration = this.pump.get().getPumpAPI().executeChain();
            this.pump.get().getPumpAPI().disconnect();


            Util.sleepMillis((int) duration);

        } catch (MaximumAttemptsException | SyringeTimeoutException | IOException | SyringeCommandException e) {
            this.setStatus(Status.ERROR);
            throw new CommandExecutionException("Failed to properly execute command!",e);
        }

        this.setStatus(Status.COMPLETE);
    }

    @Override
    public Element getXML(Document doc) {
        return null;
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
