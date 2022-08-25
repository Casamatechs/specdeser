package kr.sanchez.specdeser.core.jakarta.metadata.values;

public class KeyValue extends AbstractValue<String>{

    public KeyValue(String value) {
        super(ValueType.KEY, value, value.getBytes());
    }
    public KeyValue(String value, byte[] byteValue) {
        super(ValueType.KEY, value, byteValue);
    }
}
