package app.model.devices.pump.tecanapi;

import app.model.devices.MaximumAttemptsException;
import app.utility.Tuple;
import app.utility.Util;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/27/16
 */
public class TecanPump {

    public static final Map<Integer, String> ERROR_CODES;
    static {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "Initialization Error");
        map.put(2, "Invalid Command");
        map.put(3, "Invalid Operand");
        map.put(4, "Invalid Command Sequence");
        map.put(6, "EEPROM Failure");
        map.put(7, "Device Not Initialized");
        map.put(9, "Plunger Overload");
        map.put(10, "Valve Overload");
        map.put(11, "Plunger Move Not Allowed");
        map.put(15, "Command Overflow");
        ERROR_CODES = Collections.unmodifiableMap(map);
    }

    private TecanFrameSerialTransporter serialTransporter;
    private boolean ready;
    private boolean reapeatError;
    private int prevErrorCode;

    protected int numPorts = 9;
    protected int syringeVolume = 1000;
    protected int wastePort = 9;

    protected int initForce = 0;

    public TecanPump(TecanFrameSerialTransporter transporter) {
        this.serialTransporter = transporter;
        this.prevErrorCode = 0;
        this.reapeatError = false;
        this.ready = false;
    }

    public TecanPump(TecanFrameSerialTransporter transporter, int numPorts, int syringeVolume, int wastePort, int initForce) {
        this.serialTransporter = transporter;
        this.prevErrorCode = 0;
        this.reapeatError = false;
        this.ready = false;

        this.numPorts = numPorts;
        this.syringeVolume = syringeVolume;
        this.wastePort = wastePort;
        this.initForce = initForce;
    }

    protected Tuple<String, Integer> sendReceive(String cmd) throws IOException, MaximumAttemptsException, SyringeCommandException {
        TecanFrameContents frameContents = this.serialTransporter.sendReceive(cmd);

        int ready = this.checkStatus(frameContents.getStatusByte()).first;

        String data = frameContents.getData();

        return new Tuple<String, Integer>(data, ready);
    }

    private Tuple<Integer, Integer> checkStatus(byte status) throws SyringeCommandException {
        System.out.println("statusByte: "+status);
        //TODO move to serialtransporter
        String statusBits = String.format("%8s", Integer.toBinaryString(status)).replace(' ', '0');
        System.out.println("statusBits: "+statusBits);
        int errorCode = Integer.parseInt(statusBits.substring(4, 8));
        int ready = Integer.parseInt(statusBits.substring(2,3));

        this.ready = (ready == 1);

        this.reapeatError = (errorCode == this.prevErrorCode);

        this.prevErrorCode = errorCode;

        if (errorCode != 0)
            throw new SyringeCommandException(errorCode, ERROR_CODES);

        return new Tuple<Integer, Integer>(ready, errorCode);
    }

    private boolean checkReady() throws SyringeCommandException {
        System.out.println("IN CHECKREADY()");
//        if (this.ready) {
//            System.out.println("this.raedy = " + this.ready);
//            return true;
//        }

        try {
            int ready = this.sendReceive("Q").second;

            System.out.println("COMMAND 'Q' = " + ready);

            return (ready == 1);

        } catch (MaximumAttemptsException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SyringeCommandException e) {
            if (this.reapeatError)
                return this.ready;
            else
                throw e;
        }
        return false;
    }

    protected void waitUntilReady(int pollingInterval, int timeout, int delay) throws SyringeTimeoutException, SyringeCommandException {
        if (delay > 0)
            Util.sleepMillis(delay);

        long start = System.currentTimeMillis();

        while ((start - System.currentTimeMillis()) < (start + timeout)) {
            boolean ready = this.checkReady();
            System.out.println("READY??????????? " + ready);
            if (!ready)
//                Util.sleepMillis(pollingInterval);
                try {
                    System.out.println("zzzzzzzzzzzzzzzzzzzzzzzz");
                    Thread.sleep(pollingInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            else
                return;
        }

        throw new SyringeTimeoutException("Syringe in timeout for accepting commands");
    }

    public void connect() throws IOException {
        this.serialTransporter.connect();
    }

    public void disconnect() throws IOException {
        this.serialTransporter.disconnect();
    }

    public int getNumPorts() {
        return numPorts;
    }

    public void setNumPorts(int numPorts) {
        this.numPorts = numPorts;
    }

    public int getSyringeVolume() {
        return syringeVolume;
    }

    public void setSyringeVolume(int syringeVolume) {
        this.syringeVolume = syringeVolume;
    }

    public int getWastePort() {
        return wastePort;
    }

    public void setWastePort(int wastePort) {
        this.wastePort = wastePort;
    }

    public int getInitForce() {
        return initForce;
    }

    public void setInitForce(int initForce) {
        this.initForce = initForce;
    }

}
