package kr.sanchez.specdeser.core.jakarta.metadata;

public class Int3 extends Int2{
    public int i3 = 0;

    public Int3(byte[] value, int size) {
        super(value, 8);
        for (int i = 8; i < size; i++) {
            i3 = i3 | value[i] & 0xFF << BITSHIFT[i % 4];
        }
    }
}
