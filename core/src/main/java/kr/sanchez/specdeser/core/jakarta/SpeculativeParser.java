package kr.sanchez.specdeser.core.jakarta;

import kr.sanchez.specdeser.core.jakarta.exception.InputReadException;
import kr.sanchez.specdeser.core.jakarta.metadata.ProfileCollection;
import kr.sanchez.specdeser.core.jakarta.metadata.values.*;

import javax.json.stream.JsonLocation;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public class SpeculativeParser extends AbstractParser {

    private final InputStream inputStream;

    private int inputSize;

    private final byte[] inputBuffer = new byte[BUFFER_SIZE];
    private final byte[] CLOSING_SCOPE = new byte[]{',','}',']'};

    private final List<Event> profiledEvents = ProfileCollection.getParserProfileCollection();
    private final HashMap<Integer, SpeculativeTypeTuple> speculativePositions = new HashMap<>();

    private int eventPtr;
    private int profilePtr;
    private int parsingPtr;

    private final byte QUOTE = '"';
    private final byte BCKSL = '\\';

    public SpeculativeParser(InputStream inputStream) {
        this.inputStream = inputStream;
        this.eventPtr = 0;
        this.profilePtr = 0;
        this.parsingPtr = 0;
        readStream();
        buildStructuralIndex();
    }

    @Override
    public boolean hasNext() {
        return this.eventPtr < this.profiledEvents.size();
    }

    @Override
    public Event next() {
        return this.profiledEvents.get(this.eventPtr++);
    }

    @Override
    public String getString() {
        if (this.profiledEvents.get(this.eventPtr-1) == Event.KEY_NAME) {
            String ret = (String) ProfileCollection.getMetadataProfileCollection()
                    .get(this.profilePtr++).getValue();
            return ret;
        }
        AbstractValue<?> value = ProfileCollection.getMetadataProfileCollection().get(this.profilePtr++);
        if (value instanceof StringConstant) {
            return (String) value.getValue();
        } else if (value instanceof StringType) {
            SpeculativeTypeTuple metadata = this.speculativePositions.get(this.eventPtr-1);
            return new String(this.inputBuffer, metadata.initialBufferPosition+1, metadata.size-2);
        } else if (value instanceof Any) {
            // TODO
            return "";
        } else {
            throw new RuntimeException("Big boom");
        }
    }

    @Override
    public boolean isIntegralNumber() {
        return true;
    }

    @Override
    public int getInt() {
        AbstractValue<?> value = ProfileCollection.getMetadataProfileCollection().get(this.profilePtr++);
        if (value instanceof IntegerConstant) {
            return (Integer) value.getValue();
        } else if (value instanceof IntegerType) {
            SpeculativeTypeTuple metadata = this.speculativePositions.get(this.eventPtr-1);
            int ret = 0;
            for (int i = metadata.initialBufferPosition; i < metadata.initialBufferPosition + metadata.size; i++) {
                ret = ret * 10 + (this.inputBuffer[i] - '0');
            }
            return ret;
        } else if (value instanceof Any) {
            // TODO
            return -1;
        } else {
            throw new RuntimeException("Big boom");
        }
    }

    @Override
    public long getLong() {
        return -1;
    }

    @Override
    public BigDecimal getBigDecimal() {
        return new BigDecimal(-1);
    }

    @Override
    public JsonLocation getLocation() {
        return null;
    }

    @Override
    public void close() {
        try {
            this.inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readStream() {
        try {
            this.inputSize = this.inputStream.read(this.inputBuffer, 0, BUFFER_SIZE);
        } catch (IOException e) {
            throw new InputReadException(e.getMessage());
        }
    }
    private void buildStructuralIndex() {
        for (Event evt : profiledEvents) {
            if (evt == Event.START_OBJECT) {
                if (this.inputBuffer[this.parsingPtr++] != '{') {
                    AbstractParser.speculationEnabled = false;
                    return;
                }
            }
            else if (evt == Event.END_OBJECT) {
                if (this.inputBuffer[this.parsingPtr++] != '}') {
                    AbstractParser.speculationEnabled = false;
                    return;
                }
            }
            else if (evt == Event.START_ARRAY) {
                if (this.inputBuffer[this.parsingPtr++] != '[') {
                    AbstractParser.speculationEnabled = false;
                    return;
                }
            }
            else if (evt == Event.END_ARRAY) {
                if (this.inputBuffer[this.parsingPtr++] != '}') {
                    AbstractParser.speculationEnabled = false;
                    return;
                }
            }
            else if (evt == Event.KEY_NAME) {
                byte[] key = ((String) ProfileCollection.getMetadataProfileCollection()
                        .get(this.profilePtr++).getValue()).getBytes();
                boolean isCorrect = QUOTE == this.inputBuffer[parsingPtr++];
                final int endString = this.parsingPtr + key.length;
                int i = 0;
                while (isCorrect && this.parsingPtr < endString) {
                    isCorrect = key[i++] == this.inputBuffer[this.parsingPtr++];
                }
                if (!isCorrect || this.inputBuffer[this.parsingPtr++] != QUOTE || this.inputBuffer[this.parsingPtr++] != ':') {
                    AbstractParser.speculationEnabled = false; // TODO Fallback parser
                    return;
                }
                // At this point we know the string is correct and the point is on the first byte of the value.
            }
            else if (evt == Event.VALUE_STRING) {
                // Almost same process as for the key when it's constant
                AbstractValue<?> value = ProfileCollection.getMetadataProfileCollection()
                        .get(this.profilePtr++);
                if (value instanceof StringConstant) {
                    byte[] stringValue = value.getByteValue();
                    boolean isCorrect = QUOTE == this.inputBuffer[parsingPtr++];
                    final int endString = this.parsingPtr + stringValue.length;
                    int i = 0;
                    while (isCorrect && this.parsingPtr < endString) {
                        isCorrect = stringValue[i++] == this.inputBuffer[this.parsingPtr++];
                    }
                    if (!isCorrect || this.inputBuffer[this.parsingPtr++] != QUOTE
                            || (this.inputBuffer[this.parsingPtr++] != ',' && this.inputBuffer[this.parsingPtr] != '}' && this.inputBuffer[this.parsingPtr] != ']')) {
                        AbstractParser.speculationEnabled = false; // TODO Fallback parser
                        return;
                    }
                    // At this point we know the string is correct and the point is on the first byte of the next key, value or end of object/array.
                }
                else if (value instanceof StringType) {
                    /*
                        1. We build the string bitmask using SIMD
                        2. We build the backslash bitmask using SIMD
                        3. Compute the size of the string using SIMD
                     */
                    // For now, just will check byte by byte till we find the closing valid quote.
                    int beginPtr = parsingPtr;
                    boolean isCorrect = QUOTE == this.inputBuffer[parsingPtr++];
                    boolean foundBackslash = false;
                    while (isCorrect) {
                        byte readByte = this.inputBuffer[parsingPtr++];
                        if (readByte == BCKSL) {
                            foundBackslash = !foundBackslash;
                        }
                        else if (readByte == QUOTE && !foundBackslash) {
                            break;
                        }
                        else {
                            foundBackslash = false;
                            // TODO Control closing scope
                        }
                    }
                    if (!isCorrect) {
                        AbstractParser.speculationEnabled = false;
                        return;
                    }
                    // Here we have the size of the string. We don't want to process it, just save the pointer values.
                    this.speculativePositions.put(this.profilePtr, new SpeculativeTypeTuple(beginPtr, parsingPtr++ - beginPtr));
                }
                else if (value instanceof Any) {
                    // TODO Have to think what should be done in this case.
                } else {
                    // TODO BIG ERROR
                    throw new RuntimeException("We should never reach this point");
                }
            }
            else if (evt == Event.VALUE_NUMBER) {
                AbstractValue<?> value = ProfileCollection.getMetadataProfileCollection()
                        .get(this.profilePtr++);
                if (value instanceof IntegerConstant) {
                    byte[] byteValue = value.getByteValue();
                    int valuePtr = 0;
                    boolean isCorrect = this.inputBuffer[parsingPtr++] == byteValue[valuePtr++];
                    while (isCorrect && valuePtr < byteValue.length) {
                        isCorrect = this.inputBuffer[parsingPtr++] == byteValue[valuePtr++];
                    }
                    if (!isCorrect || !isByteValid(this.inputBuffer, parsingPtr, CLOSING_SCOPE)) {
                        AbstractParser.speculationEnabled = false;
                        return;
                    }
                    // At this point we know the integer is correct and the point is on the first byte of the next key, value or end of object/array.
                }
                else if (value instanceof IntegerType) {
                    int beginPtr = parsingPtr;
                    byte firstChar = this.inputBuffer[parsingPtr++];
                    boolean isCorrect = firstChar == '-' || (firstChar >= '0' && firstChar <= '9');
                    while (isCorrect && parsingPtr < this.inputSize) {
                        byte readByte = this.inputBuffer[parsingPtr++];
                        isCorrect = readByte >= '0' && readByte <= '9';
                    }
                    if (!isCorrect && !isByteValid(inputBuffer, parsingPtr-1, CLOSING_SCOPE)) {
                        AbstractParser.speculationEnabled = false;
                        return;
                    }
                    this.speculativePositions.put(this.profilePtr, new SpeculativeTypeTuple(beginPtr, parsingPtr-1 - beginPtr));
                }
                else if (value instanceof Any) {
                    // TODO Have to think what should be done in this case.
                }
                else {
                    // TODO BIG ERROR
                    throw new RuntimeException("We should never reach this point");
                }
            }
            else if (evt == Event.VALUE_TRUE) {
                AbstractValue<?> value = ProfileCollection.getMetadataProfileCollection()
                        .get(this.profilePtr++);
                if (value instanceof BooleanConstant) {
                    if (parsingPtr != getPositionOfBytes(inputBuffer, parsingPtr, TRUE) ||
                            !isByteValid(inputBuffer, parsingPtr + 4, CLOSING_SCOPE)) {
                        AbstractParser.speculationEnabled = false;
                        return;
                    }
                    parsingPtr += 5;
                }
                else if (value instanceof BooleanType) {
                    byte[] byteValue = value.getByteValue();
                    int beginPtr = parsingPtr;
                    if (parsingPtr != getPositionOfBytes(inputBuffer, parsingPtr, byteValue) ||
                            !isByteValid(inputBuffer, parsingPtr + byteValue.length, CLOSING_SCOPE)) {
                        AbstractParser.speculationEnabled = true;
                    }
                    parsingPtr += byteValue.length + 1;
                    this.speculativePositions.put(this.profilePtr, new SpeculativeTypeTuple(beginPtr, parsingPtr - beginPtr));
                }
                else if (value instanceof Any) {
                    // TODO Have to think what should be done in this case
                }
                else {
                    // TODO BIG ERROR
                    throw new RuntimeException("We should never reach this point");
                }
            }
            else if (evt == Event.VALUE_FALSE) {
                AbstractValue<?> value = ProfileCollection.getMetadataProfileCollection()
                        .get(this.profilePtr++);
                if (value instanceof BooleanConstant) {
                    if (parsingPtr != getPositionOfBytes(inputBuffer, parsingPtr, FALSE) ||
                            !isByteValid(inputBuffer, parsingPtr + 5, CLOSING_SCOPE)) {
                        AbstractParser.speculationEnabled = false;
                        return;
                    }
                    parsingPtr += 6;
                }
                else if (value instanceof BooleanType) {
                    byte[] byteValue = value.getByteValue();
                    int beginPtr = parsingPtr;
                    if (parsingPtr != getPositionOfBytes(inputBuffer, parsingPtr, byteValue) ||
                            !isByteValid(inputBuffer, parsingPtr + byteValue.length, CLOSING_SCOPE)) {
                        AbstractParser.speculationEnabled = false;
                        return;
                    }
                    parsingPtr += byteValue.length + 1;
                    this.speculativePositions.put(this.profilePtr, new SpeculativeTypeTuple(beginPtr, parsingPtr - beginPtr));
                }
                else if (value instanceof Any) {
                    // TODO Have to think what should be done in this case
                }
                else {
                    // TODO BIG ERROR
                    throw new RuntimeException("We should never reach this point");
                }
            }
            else if (evt == Event.VALUE_NULL) {
                AbstractValue<?> value = ProfileCollection.getMetadataProfileCollection()
                        .get(this.profilePtr++);
                if (value instanceof BooleanConstant) {
                    if (parsingPtr != getPositionOfBytes(inputBuffer, parsingPtr, NULL) ||
                            !isByteValid(inputBuffer, parsingPtr + 4, CLOSING_SCOPE)) {
                        AbstractParser.speculationEnabled = false;
                        return;
                    }
                    parsingPtr += 5;
                }
                else if (value instanceof BooleanType) {
                    byte[] byteValue = value.getByteValue();
                    int beginPtr = parsingPtr;
                    if (parsingPtr != getPositionOfBytes(inputBuffer, parsingPtr, byteValue) ||
                            !isByteValid(inputBuffer, parsingPtr + byteValue.length, CLOSING_SCOPE)) {
                        AbstractParser.speculationEnabled = true;
                    }
                    parsingPtr += byteValue.length + 1;
                    this.speculativePositions.put(this.profilePtr, new SpeculativeTypeTuple(beginPtr, parsingPtr - beginPtr));
                }
                else if (value instanceof Any) {
                    // TODO Have to think what should be done in this case
                }
                else {
                    // TODO BIG ERROR
                    throw new RuntimeException("We should never reach this point");
                }
            }
        }
        if (this.parsingPtr != inputSize && this.parsingPtr != BUFFER_SIZE) {
            AbstractParser.speculationEnabled = false;
            return;
        }
        this.profilePtr = 0;
    }

    private int getPositionOfByte(byte[] input, int startPos, byte symbol) {
        boolean found = input[startPos++] == symbol;
        while (!found && startPos < input.length) {
            found = input[startPos++] == symbol;
        }
        return startPos; // Returns the position of the next byte to the searched one.
    }

    private int getPositionOfBytes(byte[] input, int startPos, byte[] bytes) {
        int found = -1;
        byte firstByte = bytes[0];
        while (startPos < input.length) {
            startPos = getPositionOfByte(input, startPos, firstByte);
            int ptr = startPos-1;
            boolean match = true;
            for (int i  = 1; match && i < bytes.length && startPos < input.length; i++) {
                match = bytes[i] == input[startPos++];
            }
            if (match) {
                found = ptr;
                break;
            }
        }
        return found;
    }

    private boolean isByteValid(byte[] input, int pos, byte[] validBytes) {
        if (pos >= input.length) return false;
        for (byte b : validBytes) {
            if (input[pos] == b) return true;
        }
        return false;
    }
}
