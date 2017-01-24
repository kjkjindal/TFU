package app.model.protocol.commands;

import app.model.Logger;
import app.model.protocol.Status;
import app.utility.Util;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 1/1/17
 */
public class WaitCommand extends Command {

    private IntegerProperty hours;
    private IntegerProperty minutes;
    private IntegerProperty seconds;

    public WaitCommand() {
        this.hours = new SimpleIntegerProperty(0);
        this.minutes = new SimpleIntegerProperty(0);
        this.seconds = new SimpleIntegerProperty(0);
    }

    @Override
    public void execute() throws CommandExecutionException{
        this.setStatus(Status.IN_PROGRESS);

        Logger.log("EXECUTING COMMAND: " + this.getName());
        Logger.log(String.format(" - Waiting for %d hours %d minutes %d seconds", this.hours.get(), this.minutes.get(), this.seconds.get()));
        Logger.log("DONE EXECUTING");

        int totalSeconds = this.seconds.get() + this.minutes.get()*60 + this.hours.get()*60*60;

        Util.sleepSecs(totalSeconds);

        this.setStatus(Status.COMPLETE);
    }

    @Override
    public Element getXML(Document doc) {
        Element cmdElement = doc.createElement("command");

        Attr type = doc.createAttribute("type");
        type.setValue(this.getClass().getSimpleName());
        cmdElement.setAttributeNode(type);

        Attr name = doc.createAttribute("name");
        name.setValue(this.getName());
        cmdElement.setAttributeNode(name);

        Attr hours = doc.createAttribute("hours");
        hours.setValue(String.valueOf(this.getHours()));
        cmdElement.setAttributeNode(hours);

        Attr minutes = doc.createAttribute("minutes");
        minutes.setValue(String.valueOf(this.getMinutes()));
        cmdElement.setAttributeNode(minutes);

        Attr seconds = doc.createAttribute("seconds");
        seconds.setValue(String.valueOf(this.getSeconds()));
        cmdElement.setAttributeNode(seconds);

        return cmdElement;
    }

    public int getHours() {
        return hours.get();
    }

    public IntegerProperty hoursProperty() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours.set(hours);
    }

    public int getMinutes() {
        return minutes.get();
    }

    public IntegerProperty minutesProperty() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes.set(minutes);
    }

    public int getSeconds() {
        return seconds.get();
    }

    public IntegerProperty secondsProperty() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds.set(seconds);
    }
}
