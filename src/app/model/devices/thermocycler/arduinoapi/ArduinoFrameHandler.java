package app.model.devices.thermocycler.arduinoapi;

import app.model.devices.pump.tecanapi.TecanFrameContents;
import app.model.devices.pump.tecanapi.TecanFrameHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 1/12/17
 */
public class ArduinoFrameHandler {
    private String cmd;

    public ArduinoFrameHandler() {

    }

    public byte[] emitFrame(String cmd) {
        this.cmd = cmd;
        return this.buildFrame(false);
    }

    public byte[] emitRepeat() {
        return this.buildFrame(true);
    }

    public String parseFrame(byte[] frame) {
        return this.analyzeFrame(frame);
    }

    private String analyzeFrame(byte[] rawFrame) {

        System.out.println("analyzeFrame() rawFrame = " + Arrays.toString(rawFrame));

        List<Byte> frame = new ArrayList<>();
        for (byte b : rawFrame)
            frame.add(b);

        if (frame.size() < 1)
            return null;

        char[] data = new char[frame.size()];

        for (int i = 0; i < frame.size(); i++)
            data[i] = (char) frame.get(i).byteValue();

        return new String(data);
    }

    private byte[] buildFrame(boolean repeat) {
        List<Byte> frameList = new ArrayList<>();
        frameList.addAll(this.assembleCmd());

        byte[] frame = new byte[frameList.size()];

        for (int i = 0; i < frame.length; i++)
            frame[i] = frameList.get(i);

        return frame;
    }

    private List<Byte> assembleCmd() {
        List<Byte> result = new ArrayList<>();

        for (int i = 0; i < this.cmd.length(); i++)
            result.add((byte) this.cmd.charAt(i));

        return result;
    }

    public static void main(String[] args) {
        ArduinoFrameHandler api = new ArduinoFrameHandler();

        System.out.println(Arrays.toString(api.emitFrame("S50/")));
        System.out.println(api.parseFrame(api.emitFrame("S50/")));
    }

}
