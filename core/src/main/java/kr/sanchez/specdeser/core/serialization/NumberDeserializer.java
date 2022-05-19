package kr.sanchez.specdeser.core.serialization;

import kr.sanchez.specdeser.core.exception.DeserializationException;

public class NumberDeserializer extends AbstractSpeculativeDeserializer<Number>{

    @Override
    public Number deserialize(byte[] inputArray) throws DeserializationException {
        return _deserialize(inputArray, 0,false);
    }

    private Number _deserialize(byte[] inputArray, int ptr, final boolean isExp) throws DeserializationException {
        int firstChar = inputArray[ptr++];
        if (firstChar == '0') {
            if (ptr == inputArray.length) return 0; // Don't assume this
            if (inputArray[ptr++] == '.') return deserializeDoubleNumber(inputArray, ptr, 0,0.1);
        }
        if (firstChar > '0' && firstChar <= '9') { // TODO We could optimize the order of these if statements using Truffle in the future.
            return deserializeDecNumber(inputArray, ptr, firstChar - '0', isExp);
        }
        if (firstChar == '-') {
            return deserializeNegativeNumber(inputArray, ptr, isExp);
        }
        throw new DeserializationException("The byte array doesn't contain a valid number");
    }

    private Number deserializeNegativeNumber(byte[] inputArray, int ptr, boolean isExp) throws DeserializationException {
        Number positiveValue = deserializeDecNumber(inputArray, ptr, 0, isExp);
        if (positiveValue instanceof Integer) return -positiveValue.intValue();
        if (positiveValue instanceof Long) return -positiveValue.longValue();
        if (positiveValue instanceof Double) return -positiveValue.doubleValue();
        throw new DeserializationException("The byte array doesn't represent a valid number");
    }

    private Number deserializeDecNumber(byte[] inputArray, int ptr, int accum, boolean isExp) throws DeserializationException {
        if (ptr == inputArray.length) return accum;
        if (ptr == 9) return deserializeLongDecNumber(inputArray, ptr, accum);
        int digit = inputArray[ptr++];
        if (digit >= '0' && digit <= '9') return deserializeDecNumber(inputArray, ptr, (accum * 10) + (digit - '0'), isExp);
        if (digit == '.' && !isExp) return deserializeDoubleNumber(inputArray, ptr, accum, 0.1);
        if (digit == 'e' || digit == 'E') {
            Number exp = expPow(_deserialize(inputArray, ptr,true));
            if (exp instanceof Double) return accum * exp.doubleValue();
            return accum * exp.longValue();
        }
        throw new DeserializationException("The byte array doesn't contain a valid dec number");
    }

    private Number deserializeLongDecNumber(byte[] inputArray, int ptr, long accum) throws DeserializationException {
        if (ptr == inputArray.length) return accum;
        int digit = inputArray[ptr++];
        if (digit >= '0' && digit <= '9') return deserializeLongDecNumber(inputArray, ptr, (accum * 10) + (digit - '0'));
        throw new DeserializationException("The byte array doesn't contain a valid dec number");

    }

    private Number deserializeDoubleNumber(byte[] inputArray, int ptr, double accum, double divisor) throws DeserializationException {
        if (ptr == inputArray.length) return accum;
        int digit = inputArray[ptr++];
        if (digit >= '0' && digit<= '9') return deserializeDoubleNumber(inputArray, ptr, accum + ((digit - '0') * divisor), divisor/10);
        throw new DeserializationException("The byte array doesn't contain a valid double number");
    }

    private Number expPow(Number b) {// Exponentation by squaring https://stackoverflow.com/questions/29996070/using-int-double-and-long-in-calculation-of-powers
        long _b = b.longValue();
        if (_b < 0) {
            double res = 1;
            while (_b++ < 0) {
                res /= 10;
            }
            return res;
        }
        long res = 1;
        long sq = 10;
        while (_b > 0) {
            if (_b % 2 == 1) {
                res *= sq;
            }
            sq = sq * sq;
            _b /= 2;
        }
        return res;
    }
}
