package kr.sanchez.specdeser.core.jakarta.metadata;

import static kr.sanchez.specdeser.core.jakarta.AbstractParser.BITSHIFT;

public class Int3 extends Int2{
//    public int i3 = 0;

    public Int3(byte[] value, int size, int offset) {
        super(value, offset + 8, offset);
        for (int i = 8 + offset; i < size; i++) {
            i3 = i3 | (value[i] & 0xFF) << BITSHIFT[i % 4];
        }
    }
}
