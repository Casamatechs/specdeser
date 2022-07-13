package kr.sanchez.specdeser.core.jakarta.metadata.values;

public class KeyValue extends AbstractValue<String>{

    public KeyValue(String value) {
        super(ValueType.KEY, value);
    }
}
