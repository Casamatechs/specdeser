package kr.sanchez.specdeser.core.jakarta.metadata.values;

public class StringConstant extends AbstractValue<String> {
    public StringConstant(String value) {
        super(ValueType.STRING_CONSTANT, value);
    }
}
