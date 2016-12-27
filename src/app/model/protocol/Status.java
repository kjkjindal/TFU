package app.model.protocol;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/20/16
 */
public enum Status {
    NOT_STARTED("NotStarted", "Not started"),
    IN_PROGRESS("InProgress", "In progress"),
    ERROR("Error", "Error"),
    COMPLETE("Complete", "Complete");

    private String compoundName;
    private String fullName;

    Status(String compoundName, String fullName) {
        this.compoundName = compoundName;
        this.fullName = fullName;
    }

    public String getCompoundName() {
        return compoundName;
    }

    public String getFullName() {
        return fullName;
    }
}
