package kr.sanchez.specdeser.core.serialization;

import kr.sanchez.specdeser.core.exception.DeserializationException;

import java.nio.charset.Charset;

/**
 * This deserializer can return boolean values or null
 * If the byte array can't be deserialized an exception will be thrown
 */
public class LiteralDeserializer extends AbstractSpeculativeDeserializer<Boolean> {

    private final byte[][] literals;

    public LiteralDeserializer() {
        super(); // By default, we assume UTF-8 is the decoder used.
        literals = new byte[][]{"true".getBytes(this.charset),
                "false".getBytes(this.charset),
                "null".getBytes(this.charset)};
    }

    public LiteralDeserializer(String charset) {
        super(charset);
        literals = new byte[][]{"true".getBytes(this.charset),
                "false".getBytes(this.charset),
                "null".getBytes(this.charset)};
    }

    public LiteralDeserializer(Charset charset) {
        super(charset);
        literals = new byte[][]{"true".getBytes(this.charset),
                "false".getBytes(this.charset),
                "null".getBytes(this.charset)};
    }

    @Override
    public Boolean deserialize(byte[] inputArray) throws DeserializationException {
            switch (inputArray[0]) {
                case 't' -> {
                    if (inputArray.length == 4) return true;
                    throw new DeserializationException("The expected literal was true but the length did not match");
                }
                case 'f' -> {
                    if (inputArray.length == 5) return false;
                    throw new DeserializationException("The expected literal was false but the length did not match");
                }
                case 'n' -> {
                    if (inputArray.length == 4) return null;
                    throw new DeserializationException("The expected literal was null but the length did not match");
                }
                default -> throw new DeserializationException("The received byte array does not represent a literal value");
            }
    }

    public Boolean deserializeSafe(byte[] inputArray) throws DeserializationException {
        switch (inputArray[0]) {
            case 't' -> {
                return trueDeserializer(inputArray);
            }
            case 'f' -> {
                return falseDeserializer(inputArray);
            }
            case 'n' -> {
                return nullDeserializer(inputArray);
            }
            default -> throw new DeserializationException("The received byte array does not represent a literal value");
        }

    }

    private Boolean trueDeserializer(byte[] inputArray) throws DeserializationException {
        int idx = 1;
        if (inputArray.length == 4 &&
            inputArray[idx++] == 'r' &&
            inputArray[idx++] == 'u' &&
            inputArray[idx] == 'e') {
                return true;
        }
        throw new DeserializationException("The received byte array does not represent a literal value");
    }

    private Boolean falseDeserializer(byte[] inputArray) throws DeserializationException {
        int idx = 1;
        if (inputArray.length == 5 &&
                inputArray[idx++] == 'a' &&
                inputArray[idx++] == 'l' &&
                inputArray[idx++] == 's' &&
                inputArray[idx] == 'e') {
            return false;
        }
        throw new DeserializationException("The received byte array does not represent a literal value");
    }

    private Boolean nullDeserializer(byte[] inputArray) throws DeserializationException {
        int idx = 1;
        if (inputArray.length == 4 &&
                inputArray[idx++] == 'u' &&
                inputArray[idx++] == 'l' &&
                inputArray[idx] == 'l') {
            return null;
        }
        throw new DeserializationException("The received byte array does not represent a literal value");
    }

}
