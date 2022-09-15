package kr.sanchez.specdeser.core.jakarta.metadata;

import java.util.Arrays;

public class VectorizedData {

    public final AbstractInt[] data;

    public final int size;

    public VectorizedData(AbstractInt[] data, int size) {
        this.data = data;
        this.size = size;
    }

    @Override
    public String toString() {
        return "VectorizedData{" +
                "data=" + Arrays.toString(data) +
                ", size=" + size +
                '}';
    }
}
