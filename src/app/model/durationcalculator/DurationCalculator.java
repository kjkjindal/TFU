package app.model.durationcalculator;

import app.model.durationcalculator.strategies.DurationStrategy;
import app.model.durationcalculator.strategies.OneWayCommandDurationStrategy;
import app.model.protocol.Cycle;
import app.model.protocol.Protocol;
import app.model.protocol.commands.Command;
import app.model.protocol.commands.CommandType;

/**
 * Created by alexskrynnyk on 12/23/16.
 */
public class DurationCalculator {

    private DurationType type;

    public DurationCalculator(DurationType type) {
        this.type = type;
    }

    public Integer getProtocolDuration(Protocol p) {
        int totalDuration = 0;

        for (Cycle c : p.getCycleList())
            totalDuration += this.getCycleDuration(c);

        return totalDuration;
    }

    public Integer getCycleDuration(Cycle c) {
        int totalDuration = 0;

        for (Command cmd : c.getCommands())
            totalDuration += this.getCommandDuration(cmd);

        return totalDuration;
    }

    public Integer getCommandDuration(Command cmd) {
        DurationStrategy strategy = this.getStrategy(cmd);
        return strategy.getDuration(cmd, this.type);
    }

    public DurationStrategy getStrategy(Command cmd) {
        CommandType type = CommandType.getByClassName(cmd.getClass().getSimpleName());
        switch (type) {
            case OneWayCommand:
                return new OneWayCommandDurationStrategy();

            default:
                break;
        }
        return null;
    }

}
