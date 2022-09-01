package kr.sanchez.specdeser.core.jakarta.metadata;

public class Int2 extends Int1{
    public int i2 = 0;

    public Int2(byte[] value, int size) {
        super(value, 4);
        for (int i = 4; i < size; i++) {
            i2 = i2 | value[i] & 0xFF << BITSHIFT[i % 4];
        }
    }
}
