package kr.sanchez.specdeser.core.jakarta.metadata.values;

public class IntegerConstant extends AbstractValue<Integer> {
    public IntegerConstant(Integer value) {
        super(ValueType.INT_CONSTANT, value);
    }
}
