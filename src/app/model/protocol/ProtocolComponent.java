package app.model.protocol;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by alexskrynnyk on 12/20/16.
 */
public interface ProtocolComponent {

    public Status getStatus();
    public ObjectProperty<Status> statusProperty();
    public void setStatus(Status status);

    public String getName();
    public StringProperty nameProperty();
    public void setName(String newName);

    public int getDuration();
    public IntegerProperty durationProperty();

    public void execute();
}
