package kr.sanchez.specdeser.core.jakarta.metadata;

import static kr.sanchez.specdeser.core.jakarta.AbstractParser.BITSHIFT;

public class Int2 extends Int1{
//    public int i2 = 0;

    public Int2(byte[] value, int size, int offset) {
        super(value, offset + 4, offset);
        for (int i = 4 + offset; i < size; i++) {
            i2 = i2 | (value[i] & 0xFF) << BITSHIFT[i % 4];
        }
    }
}
