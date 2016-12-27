package app.model.pump.tecan;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/23/16
 */
public class TecanAPI {

    private final static byte START_BYTE = 0x02;
    private final static byte STOP_BYTE = 0x03;
    private final static byte SEQ_NUM = 0b111;
    private byte addr;
    private int _cmd;

    public TecanAPI(int addr) {
        this.addr = Byte.parseByte(Integer.toHexString(addr));
    }

//    public String emitFrame(int cmd) {
//        this._cmd = cmd;
//        return this._buildFrame();
//    }
//
//    public emitRepeat() {
//        return this._buildFrame(true);
//    }
//
//    public parseFrame(frame) {
//        return this._analyzeFrame(frame);
//    }
//
//    public _analyzeFrame(rawFrame) {
//        try {
//
//        } catch (Exception e) {
//
//        }
//    }

}
