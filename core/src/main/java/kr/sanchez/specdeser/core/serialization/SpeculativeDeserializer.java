package kr.sanchez.specdeser.core.serialization;

import kr.sanchez.specdeser.core.exception.DeserializationException;

public interface SpeculativeDeserializer<T> {

    T deserialize(byte[] inputArray) throws DeserializationException;
}
