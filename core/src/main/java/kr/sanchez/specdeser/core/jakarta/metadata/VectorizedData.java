package kr.sanchez.specdeser.core.jakarta.metadata;

import java.util.Arrays;

public class VectorizedData {

    public final AbstractInt firstPair;

    public final byte[] arrayData;

    public final int size;

    public VectorizedData(AbstractInt firstPair, byte[] arrayData) {
        this.firstPair = firstPair;
        this.arrayData = arrayData;
        this.size = arrayData.length;
    }

    @Override
    public String toString() {
        return "VectorizedData{" +
                "firstPair=" + firstPair +
                ", arrayData=" + Arrays.toString(arrayData) +
                ", size=" + size +
                '}';
    }
}
