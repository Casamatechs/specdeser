package kr.sanchez.specdeser.core.jakarta.metadata;

import static kr.sanchez.specdeser.core.jakarta.AbstractParser.BITSHIFT;

public abstract class AbstractInt {

    public int i1 = 0;
    public int i2 = 0;
    public int i3 = 0;
    public int i4 = 0;

    public static AbstractInt create(byte[] byteValue, int offset, int len) {
        if (len - offset < 5) {
            return new Int1(byteValue, len, offset);
        }
        else if (len - offset < 9) {
            return new Int2(byteValue, len, offset);
        }
        else if (len - offset < 13) {
            return new Int3(byteValue, len, offset);
        }
        else return new Int4(byteValue, Math.min(offset + 16, len), offset);
    }

    @Override
    public String toString() {
        return intToString(i1) + intToString(i2) + intToString(i3) + intToString(i4);
    }

    private static String intToString(int value) {
        StringBuilder rt = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            byte b = (byte) (value >> BITSHIFT[i]);
            if (b != 0) rt.append((char) b);
            else break;
        }
        return rt.toString();
    }
}
