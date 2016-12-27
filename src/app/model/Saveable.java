package app.model;

import app.model.protocol.ProtocolComponent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/21/16
 */
public interface Saveable {

    public Element getXML(Document doc);

}
