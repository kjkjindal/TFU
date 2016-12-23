package app.model.protocol.commands;

/**
 * Created by alexskrynnyk on 12/22/16.
 */
public enum CommandType {
    OneWayCommand("OneWayCommand", "One Way Command");

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
        for (CommandType cmd : CommandType.values()) {
            if (cmd.getClassName().equals(className))
                return cmd;
        }
        return null;
    }

    public static CommandType getByFullName(String fullName) {
        for (CommandType cmd : CommandType.values()) {
            if (cmd.getFullName().equals(fullName))
                return cmd;
        }
        return null;
    }
}
