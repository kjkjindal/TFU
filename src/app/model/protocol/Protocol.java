package app.model.protocol;

import app.model.Debugging;
import app.model.Logger;
import app.model.Saveable;
import app.model.protocol.commands.Command;
import app.model.protocol.commands.CommandExecutionException;
import app.model.protocol.commands.CommandFactory;
import app.model.protocol.commands.CommandType;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/20/16
 */
public class Protocol implements ProtocolComponent, Saveable {

    private ListProperty<Cycle> cycleList;
    private StringProperty name;
    private IntegerProperty duration;
    private ObjectProperty<Status> status;
    private ObjectProperty<CommandFactory> commandFactory;

    private ProtocolTaskThread taskThread;

    private IntegerProperty currentCycleIndex;

    private static Protocol _instance = null;

    private Protocol() {
        this.cycleList = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.name = new SimpleStringProperty("New protocol");
        this.duration = new SimpleIntegerProperty(0);
        this.status = new SimpleObjectProperty<>(Status.NOT_STARTED);

        this.taskThread = new ProtocolTaskThread();
        this.taskThread.start();

        this.currentCycleIndex = new SimpleIntegerProperty(0);

        /*
        Cycle cycle = new Cycle();
        cycle.setName("T2S seq primer anneal");

        Command cmd1 = this.commandFactory.get().createCommand(CommandType.OneWayCommand);
        cmd1.setName("Dispense '1x instrument buffer' to 'sample'");
        cmd1.setStatus(Status.COMPLETE);
        Command cmd2 = this.commandFactory.get().createCommand(CommandType.OneWayCommand);
        cmd2.setStatus(Status.IN_PROGRESS);
        cmd2.setName("Extract 'sample' to 'waste'");
        Command cmd3 = this.commandFactory.get().createCommand(CommandType.OneWayCommand);
        cmd3.setName("Wait for 15 min");

        cycle.addCommand(cmd1);
        cycle.addCommand(cmd2);
        cycle.addCommand(cmd3);

        this.cycleList.add(cycle);
        */
    }

    public static Protocol getInstance() {
        if(_instance == null){
            synchronized (Protocol.class) {
                if(_instance == null){
                    _instance = new Protocol();
                }
            }
        }
        return _instance;
    }

    @Override
    public void execute() throws CommandExecutionException {
        //this.setStatus(Status.IN_PROGRESS);
        try {
            if (this.currentCycleIndex.get() < this.cycleList.size()) {
                this.taskThread.run(this.cycleList.get(this.currentCycleIndex.get()));
                this.currentCycleIndex.set(this.currentCycleIndex.get() + 1);
            }
        } catch (IllegalStateException e) {
            throw new CommandExecutionException("A cycle is currently being executed!", e);
        }
        //this.setStatus(Status.COMPLETE);
    }

    public void resetCycleIndex() {
        this.currentCycleIndex.set(0);
    }

    public void addCycle(Cycle cycle) {
        this.cycleList.add(cycle);
    }

    public boolean removeCycle(Cycle cycle) {
        return this.cycleList.remove(cycle);
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

    public ObservableList<Cycle> getCycleList() {
        return this.cycleList.get();
    }

    public ListProperty<Cycle> cycleListProperty() {
        return this.cycleList;
    }

    @Override
    public Element getXML(Document doc) {
        Element experimentElement = doc.createElement("protocol");

        Attr name = doc.createAttribute("name");
        name.setValue(this.getName());
        experimentElement.setAttributeNode(name);

        for (Cycle cycle : this.cycleList)
            experimentElement.appendChild(cycle.getXML(doc));
        return experimentElement;
    }

    private void constructFromXML(Element element) {

    }

}
