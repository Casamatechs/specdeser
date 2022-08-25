package kr.sanchez.specdeser.core.jakarta.metadata.values;

public class IntegerConstant extends AbstractValue<Integer> {
    public IntegerConstant(Integer value, byte[] byteValue) {
        super(ValueType.INT_CONSTANT, value, byteValue);
    }
}
