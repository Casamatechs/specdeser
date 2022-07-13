package kr.sanchez.specdeser.core.jakarta.metadata.values;

import java.util.Objects;

public abstract class AbstractValue<T> {

    final ValueType type;
    final T value;

    public AbstractValue(ValueType type, T value) {
        this.type = type;
        this.value = value;
    }

    public ValueType getType() {
        return type;
    }

    public T getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractValue<?> that = (AbstractValue<?>) o;
        return type == that.type && Objects.equals(value, that.value);
    }
}
