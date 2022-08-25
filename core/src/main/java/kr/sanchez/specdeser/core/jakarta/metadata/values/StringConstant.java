package kr.sanchez.specdeser.core.jakarta.metadata.values;

public class StringConstant extends AbstractValue<String> {
    public StringConstant(String value) {
        super(ValueType.STRING_CONSTANT, value, value.getBytes());
    }
    public StringConstant(String value, byte[] byteValue) {
        super(ValueType.STRING_CONSTANT, value, byteValue);
    }
}
