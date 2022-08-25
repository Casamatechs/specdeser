package kr.sanchez.specdeser.core.jakarta.metadata.values;

import java.util.Objects;

public abstract class AbstractValue<T> {

    final ValueType type;
    final T value;
    final byte[] byteValue;

    public AbstractValue(ValueType type, T value, byte[] byteValue) {
        this.type = type;
        this.value = value;
        this.byteValue = byteValue;
    }

    public ValueType getType() {
        return type;
    }

    public T getValue() {
        return this.value;
    }

    public byte[] getByteValue() {
        return this.byteValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractValue<?> that = (AbstractValue<?>) o;
        return type == that.type && Objects.equals(value, that.value);
    }
}
