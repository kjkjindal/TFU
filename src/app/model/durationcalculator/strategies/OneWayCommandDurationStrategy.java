package app.model.durationcalculator.strategies;

import app.model.durationcalculator.DurationType;
import app.model.protocol.commands.Command;

/**
 * Created by alexskrynnyk on 12/23/16.
 */
public class OneWayCommandDurationStrategy implements DurationStrategy{

    @Override
    public Integer getDuration(Command cmd, DurationType type) {
        int duration = 69;

        return duration;
    }

}
