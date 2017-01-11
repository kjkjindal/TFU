package app.model.devices.pump.tecanapi;

import app.utility.Util;
import jdk.nashorn.internal.runtime.Source;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/27/16
 */
public class TecanXCalibur extends TecanPump {

    private enum ValveDirection {
        CW ("Z"),
        CCW ("Y");

        private String cmd;

        ValveDirection(String cmd) {
            this.cmd = cmd;
        }

        public String getCmd() {
            return this.cmd;
        }
    }

    private ValveDirection direction = ValveDirection.CW;

    private static final Map<Integer, Integer> SPEED_CODES;
    static {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(0,  6000);   map.put(1,  5600);   map.put(2,  5000);   map.put(3,  4400);
        map.put(4,  3800);   map.put(5,  3200);   map.put(6,  2600);   map.put(7,  2200);
        map.put(8,  2000);   map.put(9,  1800);   map.put(10, 1600);   map.put(11, 1400);
        map.put(12, 1200);   map.put(13, 1000);   map.put(14, 800);    map.put(15, 600);
        map.put(16, 400);    map.put(17, 200);    map.put(18, 190);    map.put(19, 180);
        map.put(20, 170);    map.put(21, 160);    map.put(22, 150);    map.put(23, 140);
        map.put(24, 130);    map.put(25, 120);    map.put(26, 110);    map.put(27, 100);
        map.put(28, 90);     map.put(29, 80);     map.put(30, 70);     map.put(31, 60);
        map.put(32, 50);     map.put(33, 40);     map.put(34, 30);     map.put(35, 20);
        map.put(36, 18);     map.put(37, 16);     map.put(38, 14);     map.put(39, 12);
        map.put(40, 10);
        SPEED_CODES = Collections.unmodifiableMap(map);
    }

    private int numPorts = 9;
    private int syringeVolume = 1000;
    private int wastePort = 9;
    private int initForce = 0;
    private boolean microstep = true;

    private String lastCmd = "";
    private String cmdChain = "";
    private int execTime = 0;
    private boolean simSpeedChange = false;

    private Map state;
    private Map simState;

    private boolean debug = false;

    private int cachedStartSpeed;
    private int cachedTopSpeed;
    private int cachedCutoffSpeed;

    public TecanXCalibur(TecanFrameSerialTransporter transporter, boolean debug) throws SyringeCommandException, IOException, MaximumAttemptsException, SyringeTimeoutException {
        super(transporter);

        this.debug = debug;

        this.state = new HashMap();
        this.state.put("valvePort", null);
        this.state.put("plungerPosition", null);
        this.state.put("microstep", true);
        this.state.put("startSpeed", null);
        this.state.put("topSpeed", null);
        this.state.put("cutoffSpeed", null);
        this.state.put("slope", 14);

        this.simState = new HashMap(this.state);

//        this.init();
//
//        this.getConfig();

        this.setMicrostep(true);

        this.updateSpeeds();
        this.getPlungerPosition();
        this.getCurrentPort();
        this.updateSimState();
    }

    //<editor-fold desc="Pump Initialization">
    //************************************************************************************
    //                                Pump Initialization
    //************************************************************************************

    public int init() throws SyringeCommandException, IOException, MaximumAttemptsException, SyringeTimeoutException {
        String cmd = String.format("%s%d,%d,%d", this.direction.getCmd(), this.initForce, 0, this.wastePort);
        this.sendReceive(cmd, true);
        this.waitReady(300, 10000, 0);

        return 0;
    }

    //</editor-fold>



    //<editor-fold desc="Convenience methods">
    //************************************************************************************
    //                                Convenience methods
    //************************************************************************************



    //</editor-fold>



    //<editor-fold desc="Command chain methods">
    //************************************************************************************
    //                                Command chain methods
    //************************************************************************************

    public double executeChain() throws SyringeCommandException, IOException, MaximumAttemptsException, SyringeTimeoutException {

        long tic = System.currentTimeMillis();

        this.sendReceive(this.cmdChain, true);
        double execTime = this.execTime;
        Util.sleep(200);
        this.resetChain(true, false);

        long toc = System.currentTimeMillis();

//        double waitTime = execTime*1000 - ((int) (toc-tic));
        double waitTime = execTime*1000;
        System.out.println("TOC-TIC: " + ((int) (toc-tic)));
        System.out.println("EXEC TIME: " + execTime);
        System.out.println("WAIT TIME: " + waitTime);

        if (waitTime < 0)
            waitTime = 0;

        return waitTime;
    }

    public void resetChain(boolean onExecute, boolean minReset) throws SyringeCommandException, IOException, MaximumAttemptsException, SyringeTimeoutException {
        this.cmdChain = "";
        this.execTime = 0;

        if (onExecute && this.simSpeedChange) {
            if (minReset) {
                this.state = new HashMap(this.simState);
            } else {
                this.state.put("slope", this.simState.get("slope"));
                this.state.put("microstep", this.simState.get("microstep"));
                this.updateSpeeds();
                Util.sleep(200);
                this.getCurrentPort();
                Util.sleep(200);
                this.getPlungerPosition();
            }
        }
        this.simSpeedChange = false;

        this.updateSimState();
    }

    private void updateSimState() {
        this.simState = new HashMap(this.state);
    }

    private void cacheSimSpeeds() {
        this.cachedStartSpeed = (int) this.simState.get("startSpeed");
        this.cachedStartSpeed = (int) this.simState.get("startSpeed");
        this.cachedStartSpeed = (int) this.simState.get("startSpeed");
    }

    private void restoreSimSpeeds() {
        this.simState.put("startSpeed", this.cachedStartSpeed);
        this.simState.put("topSpeed", this.cachedTopSpeed);
        this.simState.put("cutoffSpeed", this.cachedCutoffSpeed);

        if (50 <= this.cachedStartSpeed && this.cachedStartSpeed <= 1000)
            this.setStartSpeed(this.cachedStartSpeed);
        if (50 <= this.cachedCutoffSpeed && this.cachedCutoffSpeed <= 2700)
            this.setCutoffSpeed(this.cachedCutoffSpeed);
    }

    //</editor-fold>



    //<editor-fold desc="Chainable high-level">
    //************************************************************************************
    //                                Chainable high-level
    //************************************************************************************

    public void extract(int fromPort, int volume) {
        int steps = this.ulToSteps(volume);
        this.changePort(fromPort, 0);
        this.movePlungerRelative(steps);
    }

    public void dispense(int toPort, int volume) {
        int steps = this.ulToSteps(volume);
        this.changePort(toPort, 0);
        this.movePlungerRelative(-1 * steps);
    }

    //</editor-fold>



    //<editor-fold desc="Chainable low-level">
    //************************************************************************************
    //                                Chainable low-level
    //************************************************************************************

    public void changePort(int toPort, int fromPort) {
        if (0 < toPort && toPort <= this.numPorts) {
            if (fromPort == 0) {
                fromPort = (this.simState.get("valvePort") != null) ? (int) this.simState.get("valvePort") : 1;
                System.out.println("fromport = " + fromPort);
            }

            int delta = toPort - fromPort;
            int diff = (Math.abs(delta) >= 7) ? -1 * delta : delta;
            ValveDirection direction = (diff < 1) ? ValveDirection.CCW : ValveDirection.CW;

            String cmd = String.format("%s%d", direction.getCmd(), toPort);

            this.simState.put("valvePort", toPort);
            this.cmdChain += cmd;
            this.execTime += 0.2;
        } else {
            throw new ValueException("'To port' must be in the range 1 - "+this.numPorts);
        }
    }

    public void movePlungerAbsolute(int position) {
        if ((boolean) this.simState.get("microstep"))
            if (!(0 <= position && position <= 24000))
                throw new ValueException("'Position' must be in the range 0 - 24000");
        else
            if (!(0 <= position && position <= 3000))
                throw new ValueException("'Position' must be in the range 0 - 3000");

        String cmd = String.format("A%d", position);
        int currentPosition = (int) this.simState.get("plungerPosition");
        int delta = currentPosition - position;
        this.simState.put("plungerPossition", position);

        this.cmdChain += cmd;

        this.execTime += this.getPlungerMoveDuration(Math.abs(delta));
        System.out.println("CALCULATED: " + this.getPlungerMoveDuration(Math.abs(delta)));
    }

    public void movePlungerRelative(int position) {
        String cmd = (position < 0) ? String.format("D%d", Math.abs(position)) : String.format("P%d", position);
        this.simState.put("plungerPosition", (int) this.simState.get("plungerPosition") + position);

        this.cmdChain += cmd;
        this.execTime += this.getPlungerMoveDuration(Math.abs(position));
    }

    //</editor-fold>



    //<editor-fold desc="Command set commands">
    //************************************************************************************
    //                                Command set commands
    //************************************************************************************

    public void setSpeed(int speedCode) {
        if (0 <= speedCode && speedCode <= 40) {
            String cmd = String.format("S%d", speedCode);

            this.simSpeedChange = true;
            this.simIncrementToPulses(speedCode);

            this.cmdChain += cmd;
        } else {
            throw new ValueException("'Speed code' must be in the range 0 - 40");
        }
    }

    public void setStartSpeed(int pulsesPerSec) {
        if (50 <= pulsesPerSec && pulsesPerSec <= 1000) {
            String cmd = String.format("v%d", pulsesPerSec);

            this.simSpeedChange = true;

            this.cmdChain += cmd;
        } else {
            throw new ValueException("'Pulses per second' must be in the range 50 - 1000");
        }
    }

    public void setTopSpeed(int pulsesPerSec) {
        if (5 <= pulsesPerSec && pulsesPerSec <= 6000) {
            String cmd = String.format("V%d", pulsesPerSec);

            this.simSpeedChange = true;

            this.cmdChain += cmd;
        } else {
            throw new ValueException("'Pulses per second' must be in the range 5 - 6000");
        }
    }

    public void setCutoffSpeed(int pulsesPerSec) {
        if (50 <= pulsesPerSec && pulsesPerSec <= 2700) {
            String cmd = String.format("c%d", pulsesPerSec);

            this.simSpeedChange = true;

            this.cmdChain += cmd;
        } else {
            throw new ValueException("'Pulses per second' must be in the range 50 - 2700");
        }
    }

    public void setSlope(int slopeCode) {
        if (1 <= slopeCode && slopeCode <= 20) {
            String cmd = String.format("L%d", slopeCode);

            this.simSpeedChange = true;

            this.cmdChain += cmd;
        } else {
            throw new ValueException("'Slope code' must be in the range 1 - 20");
        }
    }

    public void repeatCmdSeq(int numRepeats) {
        if (0 <= numRepeats && numRepeats <= 30000) {
            String cmd = String.format("G%d", numRepeats);

            this.cmdChain += cmd;

            this.execTime *= numRepeats;
        } else {
            throw new ValueException("'Number of repeats' must be in the range 0 - 30000");
        }
    }

    public void markRepeatStart() {
        String cmd = "g";

        this.cmdChain += cmd;
    }

    public void delayExecution(int msDelay) {
        if (0 < msDelay && msDelay < 30000) {
            String cmd = String.format("M%d", msDelay);

            this.cmdChain += cmd;
        } else {
            throw new ValueException("'Delay' must be in the range 0 - 30000 ms");
        }
    }

    public void haltExecution(int inputPin) throws SyringeCommandException, SyringeTimeoutException, MaximumAttemptsException, IOException {
        if (0 <= inputPin && inputPin <= 2) {
            String cmd = String.format("H%d", inputPin);

            this.sendReceive(cmd, false);
        } else {
            throw new ValueException("'Input pin' must be 0, 1, or 2");
        }
    }

    public void terminateExecution() throws SyringeCommandException, SyringeTimeoutException, MaximumAttemptsException, IOException {
        String cmd = "T";

        this.sendReceive(cmd, false);

        this.init();
    }

    //</editor-fold>



    //<editor-fold desc="Report commands">=//************************************************************************************
    //                                Report commands
    //************************************************************************************

    private void updateSpeeds() throws SyringeCommandException, IOException, MaximumAttemptsException, SyringeTimeoutException {
        this.getStartSpeed();
        this.getTopSpeed();
        this.getCutoffSpeed();
    }

    public String getConfig() throws SyringeCommandException, SyringeTimeoutException, MaximumAttemptsException, IOException {
        String cmd = "&";

        return this.sendReceive(cmd, false);
    }

    public int getPlungerPosition() throws SyringeCommandException, IOException, MaximumAttemptsException, SyringeTimeoutException {
        String cmd = "?";
        String data = this.sendReceive(cmd, false);

        int value = Integer.parseInt(data);

        this.state.put("plungerPosition", value);

        return (int) this.state.get("plungerPosition");
    }

    public int getStartSpeed() throws SyringeCommandException, IOException, MaximumAttemptsException, SyringeTimeoutException {
        String cmd = "?1";
        String data = this.sendReceive(cmd, false);

        int value = Integer.parseInt(data);

        this.state.put("startSpeed", value);

        return (int) this.state.get("startSpeed");
    }

    public int getTopSpeed() throws SyringeCommandException, IOException, MaximumAttemptsException, SyringeTimeoutException {
        String cmd = "?2";
        String data = this.sendReceive(cmd, false);

        int value = Integer.parseInt(data);

        this.state.put("topSpeed", value);

        return (int) this.state.get("topSpeed");
    }

    public int getCutoffSpeed() throws SyringeCommandException, IOException, MaximumAttemptsException, SyringeTimeoutException {
        String cmd = "?3";
        String data = this.sendReceive(cmd, false);

        int value = Integer.parseInt(data);

        this.state.put("cutoffSpeed", value);

        return (int) this.state.get("cutoffSpeed");
    }

    public int getEncoderPosition() throws SyringeCommandException, IOException, MaximumAttemptsException, SyringeTimeoutException {
        String cmd = "?4";
        String data = this.sendReceive(cmd, false);

        int value = Integer.parseInt(data);

        return value;
    }

    public int getCurrentPort() throws SyringeCommandException, IOException, MaximumAttemptsException, SyringeTimeoutException {
        String cmd = "?6";
        String data = this.sendReceive(cmd, false);

        int value = (data.equals("?")) ? 0 : Integer.parseInt(data);

        this.state.put("valvePort", value);
        return (int) this.state.get("valvePort");
    }

    public int getBufferStatus() throws SyringeCommandException, IOException, MaximumAttemptsException, SyringeTimeoutException {
        String cmd = "?10";
        String data = this.sendReceive(cmd, false);

        int value = Integer.parseInt(data);

        return value;
    }

    //</editor-fold>



    //<editor-fold desc="Config commands">
    //************************************************************************************
    //                                Config commands
    //************************************************************************************

    public void setMicrostep(boolean microstep) throws SyringeCommandException, IOException, MaximumAttemptsException, SyringeTimeoutException {
        String cmd = String.format("N%d", (microstep) ? 1 : 0);
        this.sendReceive(cmd, true);
        this.microstep = microstep;
    }

    //</editor-fold>



    //<editor-fold desc="Control commands">
    //************************************************************************************
    //                                Control commands
    //************************************************************************************

    public String terminateCmd() throws SyringeCommandException, IOException, MaximumAttemptsException, SyringeTimeoutException {
        String cmd = "T";
        return this.sendReceive(cmd, true);
    }

    //</editor-fold>



    //<editor-fold desc="Comm + special commands">
    //************************************************************************************
    //                                Comm + special commands
    //************************************************************************************

    private void handleSyringeExceptions(SyringeCommandException ex) throws SyringeCommandException, IOException, MaximumAttemptsException, SyringeTimeoutException {
        switch (ex.getErrorCode()) {
            case 7:
            case 9:
            case 10:
                String lastCmd = this.lastCmd;

                this.resetChain(false, false);

                try {
                    this.init();
                } catch (SyringeCommandException e) {
                    switch (e.getErrorCode()) {
                        case 7:
                        case 9:
                        case 10:
                            break;
                        default:
                            throw e;
                    }
                }

                super.waitUntilReady(300, 10000, 0);
                this.sendReceive(lastCmd, false);
                break;

            default:
                this.resetChain(false, false);
                throw ex;
        }
    }

    public void waitReady(int pollingInterval, int timeout, int delay) throws SyringeCommandException, IOException, MaximumAttemptsException, SyringeTimeoutException {
        try {
            super.waitUntilReady(pollingInterval, timeout, delay);
        }
        catch (SyringeCommandException e) {
            this.handleSyringeExceptions(e);
        }
    }

    public String sendReceive(String cmd, boolean execute) throws SyringeCommandException, IOException, MaximumAttemptsException, SyringeTimeoutException {
        if (execute)
            cmd += "R";

        this.lastCmd = cmd;

        try {
            return super.sendReceive(cmd).first;
        } catch (SyringeCommandException e) {
            this.handleSyringeExceptions(e);
        }

        return "";
    }

    private double getPlungerMoveDuration(int steps) {
        double moveTime = 0;

        int startSpeed = (int) this.simState.get("startSpeed");
        int topSpeed = (int) this.simState.get("topSpeed");
        int cutoffSpeed = (int) this.simState.get("cutoffSpeed");
        int slope = (int) this.simState.get("slope");
        boolean microstep = (boolean) this.simState.get("microstep");

        slope *= 2500.0;

        if (microstep)
            steps /= 8.0;

        int theoreticalTopSpeed = (int) Math.sqrt((4 * steps * slope) + startSpeed ^ 2);

        if (theoreticalTopSpeed < cutoffSpeed)
            moveTime = theoreticalTopSpeed - (startSpeed/slope);
        else
            theoreticalTopSpeed = (int) Math.sqrt(((2 * steps * slope) + ((startSpeed ^ 2 + cutoffSpeed ^ 2)/2.0)));

        if (cutoffSpeed < theoreticalTopSpeed && theoreticalTopSpeed < topSpeed)
            moveTime = ((1.0/slope) * (2.0 * theoreticalTopSpeed - startSpeed - cutoffSpeed));
        else if (startSpeed == topSpeed && topSpeed == cutoffSpeed)
            moveTime = ((2.0 * steps) / topSpeed);
        else {
            int rampUpHalfsteps = (int) ((topSpeed ^ 2 - startSpeed ^ 2) / (2.0 * slope));
            int rampDownHalfsteps = (int) ((topSpeed ^ 2 - cutoffSpeed ^ 2) / (2.0 * slope));

            if ((rampUpHalfsteps + rampDownHalfsteps) < (2.0 * topSpeed)){
                double rampUpTime = (topSpeed - startSpeed) / slope;
                double rampDownTime = (topSpeed - cutoffSpeed) / slope;
                double constantHalfsteps = (2 * steps - rampUpHalfsteps - rampDownHalfsteps);
                double constantTime = constantHalfsteps / topSpeed;
                moveTime = rampUpTime + rampDownTime + constantTime;
            }
        }
        System.out.println("getPlungerMoveDuration() : " + moveTime);
        return moveTime;
    }

    private int ulToSteps(int microliters) {
        return (this.microstep) ? microliters * (24000/this.syringeVolume) : microliters * (3000/this.syringeVolume);
    }

    private void simIncrementToPulses(int speedIncrement) {
        int topSpeed = SPEED_CODES.get(speedIncrement);

        this.simState.put("topSpeed", topSpeed);

        if (((int) this.simState.get("startSpeed")) > topSpeed)
            this.simState.put("startSpeed", topSpeed);
        if (((int) this.simState.get("cutoffSpeed")) > topSpeed)
            this.simState.put("cutoffSpeed", topSpeed);
    }

    //</editor-fold>


    public static void main(String[] args) {
        try {

            TecanFrameSerialTransporter transport = new TecanFrameSerialTransporter(0, "/dev/tty.usbserial", 9600, 200, 2);

            TecanXCalibur pump = new TecanXCalibur(transport, false);

//            pump.init();

            transport.disconnect();

        } catch (SyringeCommandException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MaximumAttemptsException e) {
            e.printStackTrace();
        } catch (SyringeTimeoutException e) {
            e.printStackTrace();
        }
    }

}
