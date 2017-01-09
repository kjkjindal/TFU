package app.model.devices.pump;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/23/16
 */
public enum PortType {
    EMPTY("Empty"),
    REAGENT("Reagent"),
    WASH("Wash"),
    OUTPUT("Output"),
    WASTE("Waste");

    private String name;

    PortType(String name) {
        this.name = name;
    }

    public String getName() { return this.name; }

    public static PortType getByName(String name) {
        PortType type = null;

        for (PortType port : PortType.values())
            if (port.getName().equals(name))
                type = port;

        return type;
    }

}
