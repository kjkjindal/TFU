package app.model.protocol.commands;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * Created by alexskrynnyk on 12/20/16.
 */
public class CommandFactory {

    public static Command createCommand(CommandType type) {
        Command cmd = null;
        switch (type) {
            case OneWayCommand:
                cmd = new OneWayCommand();
                break;
            default:
                break;
        }
        return cmd;
    }

    public static Command createCommandFromXML(Element element) {
        NamedNodeMap attributes = element.getAttributes();

        CommandType type = CommandType.getByClassName(attributes.getNamedItem("type").getNodeValue());

        switch (type) {
            case OneWayCommand:
                OneWayCommand cmd = (OneWayCommand) CommandFactory.createCommand(type);

                cmd.setName(attributes.getNamedItem("name").getNodeValue());
                cmd.setVolume(Integer.valueOf(attributes.getNamedItem("volume").getNodeValue()));
                cmd.setExtractSpeed(Integer.valueOf(attributes.getNamedItem("extractSpeed").getNodeValue()));
                cmd.setDispenseSpeed(Integer.valueOf(attributes.getNamedItem("dispenseSpeed").getNodeValue()));
//                TODO: create a way to set/get pump objects by name
//                cmd.setPump(attributes.getNamedItem("pump").getNodeValue());
//                cmd.setFromPort(attributes.getNamedItem("fromPort").getNodeValue());
//                cmd.setToPort(attributes.getNamedItem("toPort").getNodeValue());

                return cmd;

            default:
                break;
        }
        return null;
    }

}
