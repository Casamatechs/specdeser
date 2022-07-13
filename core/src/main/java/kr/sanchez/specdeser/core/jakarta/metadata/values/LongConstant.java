package kr.sanchez.specdeser.core.jakarta.metadata.values;

public class LongConstant extends AbstractValue<Long> {
    public LongConstant(Long value) {
        super(ValueType.INT_CONSTANT, value); // For now we will always go with INT constant for natural numbers
    }
}
