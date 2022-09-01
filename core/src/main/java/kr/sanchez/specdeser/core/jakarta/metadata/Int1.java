package kr.sanchez.specdeser.core.jakarta.metadata;

public class Int1 extends AbstractInt{

    public Int1(byte[] value, int size) {
        for (int i = 0; i < size; i++) {
            i1 = i1 | value[i] & 0xFF << BITSHIFT[i % 4];
        }
    }
}
