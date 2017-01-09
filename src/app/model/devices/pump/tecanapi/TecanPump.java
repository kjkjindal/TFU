package app.model.devices.pump.tecanapi;

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

    public TecanPump(TecanFrameSerialTransporter transporter) {
        this.serialTransporter = transporter;
        this.prevErrorCode = 0;
        this.reapeatError = false;
        this.ready = false;
    }

    protected Tuple<byte [], Integer> sendReceive(String cmd) throws IOException, MaximumAttemptsException, SyringeCommandException {
        TecanFrameContents frameContents = this.serialTransporter.sendReceive(cmd);

        int ready = this.checkStatus(frameContents.getStatusByte()).first;

        byte[] data = frameContents.getData();

        return new Tuple<byte[], Integer>(data, ready);
    }

    private Tuple<Integer, Integer> checkStatus(byte status) throws SyringeCommandException {
        String statusBits = Integer.toBinaryString(status);
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
        if (this.ready)
            return true;

        try {
            int ready = this.sendReceive("Q").second;
            return (ready == 1) ? true : false;
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
            Util.sleep(delay);

        long start = System.currentTimeMillis();

        while ((start - System.currentTimeMillis()) < (start + timeout)) {
            boolean ready = this.checkReady();

            if (ready == false)
                Util.sleep(pollingInterval);
            else
                return;
        }

        throw new SyringeTimeoutException("Syringe in timeout for accepting commands");
    }



}
