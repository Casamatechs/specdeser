package kr.sanchez.specdeser.core.jakarta;

class SpeculativeTypeTuple {

    final int initialBufferPosition;
    final int size;

    public SpeculativeTypeTuple(int initialBufferPosition, int size) {
        this.initialBufferPosition = initialBufferPosition;
        this.size = size;
    }
}
