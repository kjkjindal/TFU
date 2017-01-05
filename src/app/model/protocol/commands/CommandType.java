package app.model.protocol.commands;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/22/16
 */
public enum CommandType {
    OneWayCommand("OneWayCommand", "One Way Command"),
    WashSyringeCommand("WashSyringeCommand", "Wash Syringe Command"),
    EmailCommand("EmailCommand", "E-mail Command"),
    WaitCommand("WaitComand", "Wait Command");

    private String className;
    private String fullName;

    CommandType(String className, String fullName) {
        this.className = className;
        this.fullName = fullName;
    }

    public String getClassName() {
        return this.className;
    }

    public String getFullName() {
        return this.fullName;
    }

    public static CommandType getByClassName(String className) {
        CommandType type = null;

        for (CommandType cmd : CommandType.values())
            if (cmd.getClassName().equals(className))
                type = cmd;

        return type;
    }

    public static CommandType getByFullName(String fullName) {
        CommandType type = null;

        for (CommandType cmd : CommandType.values())
            if (cmd.getFullName().equals(fullName))
                type = cmd;

        return type;
    }
}
