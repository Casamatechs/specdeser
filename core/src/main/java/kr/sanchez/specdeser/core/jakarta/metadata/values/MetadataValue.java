package kr.sanchez.specdeser.core.jakarta.metadata.values;

import java.util.Arrays;
import java.util.Objects;

public class MetadataValue {

    private static final byte[] NULL = new byte[]{'n','u','l','l'};

    public final ValueType type;

    public final int intValue;

    public final String stringValue;

    public final boolean booleanValue;
    public final byte[] byteValue;

    private MetadataValue(ValueType type, int intValue, String stringValue, boolean booleanValue, byte[] byteValue) {
        this.type = type;
        this.intValue = intValue;
        this.stringValue = stringValue;
        this.booleanValue = booleanValue;
        this.byteValue = byteValue;
    }

    public MetadataValue(int value, byte[] byteValue) {
        this(ValueType.INT_CONSTANT, value, null, false, byteValue);
    }

    public MetadataValue(ValueType type, String value, byte[] byteValue){
        this(type, 0, value, false, byteValue);
    }

    public MetadataValue(boolean value, byte[] byteValue) {
        this(ValueType.BOOLEAN_CONSTANT, 0, null, value, byteValue);
    }

    public MetadataValue() {
        this(ValueType.NULL_CONSTANT, 0, null, false, NULL);
    }

    public MetadataValue(ValueType type) {
        this(type, 0, null, false, null);
    }

    public boolean equals(MetadataValue that) {
        if (this == that) return true;
        if (that == null) return false;
        return type == that.type &&
                intValue == that.intValue &&
                booleanValue == that.booleanValue &&
                Objects.equals(stringValue, that.stringValue) &&
                Arrays.equals(byteValue, that.byteValue);
    }

    // Only for testing purposes
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetadataValue that = (MetadataValue) o;
        return intValue == that.intValue && booleanValue == that.booleanValue && type == that.type && Objects.equals(stringValue, that.stringValue) && Arrays.equals(byteValue, that.byteValue);
    }

    @Override
    public String toString() {
        return "MetadataValue{" +
                "type=" + type +
                ", intValue=" + intValue +
                ", stringValue='" + stringValue + '\'' +
                ", booleanValue=" + booleanValue +
                ", byteValue=" + Arrays.toString(byteValue) +
                '}';
    }
}
