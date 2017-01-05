package app.model.durationcalculator.strategies;

import app.model.durationcalculator.DurationType;
import app.model.protocol.commands.Command;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/23/16
 */
public interface DurationStrategy {

    Integer getDuration(Command cmd, DurationType type);

}
