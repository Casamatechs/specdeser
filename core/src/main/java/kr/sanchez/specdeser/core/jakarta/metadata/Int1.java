package kr.sanchez.specdeser.core.jakarta.metadata;

import static kr.sanchez.specdeser.core.jakarta.AbstractParser.BITSHIFT;

public class Int1 extends AbstractInt{

    public Int1(byte[] value, int size, int offset) {
//        for (int i = offset; i < size; i++) {
//            i1 = i1 | (value[i] & 0xFF) << BITSHIFT[i % 4];
//        }
        i1 = Byte.toUnsignedInt(value[offset]);
    }
}
