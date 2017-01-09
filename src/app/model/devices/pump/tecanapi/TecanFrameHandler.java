package app.model.devices.pump.tecanapi;

import java.util.*;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/23/16
 */
public class TecanFrameHandler {

    private final static byte STX = 0x02;
    private final static byte ETX = 0x03;
    private static final List<Byte> seqNums = new ArrayList<>();
    static {
        seqNums.add((byte) 0b001);
        seqNums.add((byte) 0b010);
        seqNums.add((byte) 0b011);
        seqNums.add((byte) 0b100);
        seqNums.add((byte) 0b101);
        seqNums.add((byte) 0b110);
        seqNums.add((byte) 0b111);
    }

    private byte seqByte = 0b111;
    private byte address;
    private String cmd;

    public TecanFrameHandler(int addr) {
        this.address = (byte) (addr + 0x31);
    }

    public byte[] emitFrame(String cmd) {
        this.cmd = cmd;
        return this.buildFrame(false);
    }

    public byte[] emitRepeat() {
        return this.buildFrame(true);
    }

    public TecanFrameContents parseFrame(byte[] frame) {
        return this.analyzeFrame(frame);
    }

    private TecanFrameContents analyzeFrame(byte[] rawFrame) {

        List<Byte> frameList = new ArrayList<>();
        for (byte b : rawFrame) frameList.add(b);
        List<Byte> frame = frameList.subList(frameList.indexOf(TecanFrameHandler.STX), frameList.indexOf(TecanFrameHandler.ETX)+2);

        if (frame.size() < 5)
            return null;

        int etx = frame.indexOf(TecanFrameHandler.ETX);
        int lenData = etx - 3;

        if (!this.verifyChecksum(frame))
            return null;

        byte[] data = new byte[lenData];

        if (lenData > 0) {
            for (int i = 0; i < lenData; i++)
                data[i] = frameList.get(3+i);
        } else {
            data = null;
        }

        byte status = frameList.get(2);

        return new TecanFrameContents(status, data);
    }

    private byte[] buildFrame(boolean repeat) {
        byte seqByte;

        if (repeat) {
            seqByte = (byte) ((0b00111 << 3) | this.seqByte);
        } else {
            this.rotateSeqNums();
            seqByte = (byte) ((0b00110 << 3 ) | this.seqByte);
        }

        List<Byte> frameList = new ArrayList<>();
        frameList.add(TecanFrameHandler.STX);
        frameList.add(this.address);
        frameList.add(seqByte);
        frameList.addAll(this.assembleCmd());
        frameList.add(TecanFrameHandler.ETX);

        byte checksum = (byte) this.buildChecksum(frameList);

        frameList.add(checksum);

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

    private int buildChecksum(List<Byte> partialFrame) {
        int checksum = 0;
        for(byte b : partialFrame) {
            checksum ^= b;
        }

//        System.out.println("built checksum: "+checksum);

        return checksum;
    }

    private boolean verifyChecksum(List<Byte> frame) {
        List<Byte> partialFrame = frame.subList(0, frame.size()-1);

//        System.out.println("partial frame: "+partialFrame);

        byte checksum = frame.get(frame.size()-1);

//        System.out.println("checksum: "+checksum);

        return (checksum == this.buildChecksum(partialFrame));
    }

    public void rotateSeqNums() {
//        System.out.println("seqByte before rotate: " + this.seqByte);
//        System.out.println("seqNums before rotate: " + this.seqNums);

        this.seqByte = this.seqNums.get(0);
        Collections.rotate(this.seqNums, -1);

//        System.out.println("seqByte after rotate: " + this.seqByte);
//        System.out.println("seqNums after rotate: " + this.seqNums);
    }

    public static void main(String[] args) {
        TecanFrameHandler tecanFrameHandler = new TecanFrameHandler(0);

        List<Byte> frame = new ArrayList<>();
        // 45, 78, 98, 56, 57
        frame.add((byte) 45);
        frame.add((byte) 78);
        frame.add((byte) 98);
        frame.add((byte) 56);
        frame.add((byte) 57);

        byte[] frame2 = tecanFrameHandler.emitFrame("?76");

        System.out.println(Arrays.toString(frame2));

        frame2 = tecanFrameHandler.emitFrame("?72");

        System.out.println(Arrays.toString(frame2));

        frame2 = tecanFrameHandler.emitFrame("?79");

        System.out.println(Arrays.toString(frame2));

        frame2 = tecanFrameHandler.emitFrame("?79");

        System.out.println(Arrays.toString(frame2));

        frame2 = tecanFrameHandler.emitFrame("?79");

        System.out.println(Arrays.toString(frame2));

        frame2 = tecanFrameHandler.emitFrame("?79");

        System.out.println(Arrays.toString(frame2));

        frame2 = tecanFrameHandler.emitFrame("?79");

        System.out.println(Arrays.toString(frame2));

        frame2 = tecanFrameHandler.emitFrame("?79");

        System.out.println(Arrays.toString(frame2));

        frame2 = tecanFrameHandler.emitFrame("?79");

        System.out.println(Arrays.toString(frame2));

        frame2 = tecanFrameHandler.emitFrame("?79");

        System.out.println(Arrays.toString(frame2));

        frame2 = tecanFrameHandler.emitFrame("?79");

        System.out.println(Arrays.toString(frame2));

        String str = new String(tecanFrameHandler.parseFrame(frame2).getData());
        System.out.println(str);
        System.out.println(tecanFrameHandler.parseFrame(frame2).getStatusByte());
    }

}
