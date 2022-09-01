package kr.sanchez.specdeser.core.jakarta.metadata;

public class Int4 extends Int3 {
    public int i4 = 0;

    public Int4(byte[] value, int size) {
        super(value, 12);
        for (int i = 12; i < size; i++) {
            i4 = i4 | value[i] & 0xFF << BITSHIFT[i % 4];
        }
    }

}
