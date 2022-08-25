package kr.sanchez.specdeser.core.jakarta.metadata.values;

public class LongConstant extends AbstractValue<Long> {
    public LongConstant(Long value, byte[] byteValue) {
        super(ValueType.INT_CONSTANT, value, byteValue); // For now we will always go with INT constant for natural numbers
    }
}
