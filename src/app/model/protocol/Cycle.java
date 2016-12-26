package app.model.protocol;

import app.model.Debugging;
import app.model.Logger;
import app.model.Saveable;
import app.model.protocol.commands.Command;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created by alexskrynnyk on 12/20/16.
 */
public class Cycle implements ProtocolComponent, Saveable {

    private ListProperty<Command> commandList;
    private StringProperty name;
    private IntegerProperty duration;
    private ObjectProperty<Status> status;

    public Cycle() {
        this.commandList = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.name = new SimpleStringProperty("New cycle");
        this.duration = new SimpleIntegerProperty(0);
        this.status = new SimpleObjectProperty<>(Status.NOT_STARTED);
    }

    public Cycle(Element element) {
        this.constructFromXML(element);
    }

    @Override
    public void setStatus(Status status) {
        if(status != null)
            this.status.set(status);

        if(Debugging.VERBOSE)
            Logger.log(this.name.get()+": setting 'status' to : "+status);
    }

    @Override
    public Status getStatus() { return this.status.get(); }

    @Override
    public ObjectProperty<Status> statusProperty() { return this.status; }

    @Override
    public void setName(String newName) {
        if(newName != null)
            this.name.set(newName);
    }

    @Override
    public String getName() {
        return this.name.get();
    }

    @Override
    public StringProperty nameProperty() {
        return this.name;
    }

    @Override
    public int getDuration() { return this.duration.get(); }

    @Override
    public IntegerProperty durationProperty() { return this.duration; }

    public void addCommand(Command cmd, int index) {
        if(cmd != null && index >= 0 && index < this.commandList.size())
            this.commandList.add(index, cmd);
    }

    public void addCommand(Command cmd) {
        if(cmd != null)
            this.commandList.add(cmd);
    }

    public void addAllCommands(List<Command> cmdList) {
        this.commandList.addAll(cmdList);
    }

    public boolean removeCommand(Command cmd) {
        return this.commandList.remove(cmd);
    }

    public boolean removeAllCommands(List<Command> cmdList) {
        return this.commandList.removeAll(cmdList);
    }

    public ObservableList<Command> getCommands() {
        return this.commandList.get();
    }

    public ListProperty<Command> commandsProperty() {
        return this.commandList;
    }

    public void execute() {
        this.setStatus(Status.IN_PROGRESS);
        try {
            for (Command c : this.commandList)
                if (c != null)
                    c.execute();
        } catch (Exception e) {
            this.setStatus(Status.ERROR);
            throw e;
        }
        this.setStatus(Status.COMPLETE);
    }

    @Override
    public Element getXML(Document doc) {
        Element cycleElement = doc.createElement("cycle");

        Attr name = doc.createAttribute("name");
        name.setValue(this.getName());
        cycleElement.setAttributeNode(name);

        for (Command cmd : this.commandList)
            cycleElement.appendChild(cmd.getXML(doc));
        return cycleElement;
    }

    private void constructFromXML(Element element) {

    }

}
