package kr.sanchez.specdeser.core.jakarta.metadata.values;

public class BooleanConstant extends AbstractValue<Boolean> {
    public BooleanConstant(Boolean value) {
        super(ValueType.BOOLEAN_CONSTANT, value);
    }
}
