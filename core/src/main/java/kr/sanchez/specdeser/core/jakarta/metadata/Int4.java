package kr.sanchez.specdeser.core.jakarta.metadata;

import static kr.sanchez.specdeser.core.jakarta.AbstractParser.BITSHIFT;

public class Int4 extends Int3 {
//    public int i4 = 0;

    public Int4(byte[] value, int size, int offset) {
        super(value, offset + 12, offset);
        for (int i = 12 + offset; i < size; i++) {
            i4 = i4 | (value[i] & 0xFF) << BITSHIFT[i % 4];
        }
    }
}
