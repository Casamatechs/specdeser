package kr.sanchez.specdeser.core.jakarta.metadata;

public abstract class AbstractInt {

    final byte[] BITSHIFT = new byte[]{24,16,8,0};

    public int i1 = 0;

    public static AbstractInt create(byte[] byteValue) {
        int len = byteValue.length;
        if (len < 5) {
            return new Int1(byteValue, len);
        }
        else if (len < 9) {
            return new Int2(byteValue, len);
        }
        else if (len < 13) {
            return new Int3(byteValue, len);
        }
        else return new Int4(byteValue, 16);
    }
}
