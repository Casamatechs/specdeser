package kr.sanchez.specdeser.core.jakarta;

import kr.sanchez.specdeser.core.jakarta.exception.InputReadException;
import kr.sanchez.specdeser.core.jakarta.metadata.ProfileCollection;
import kr.sanchez.specdeser.core.jakarta.metadata.values.*;

import javax.json.stream.JsonLocation;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SpeculativeParser extends AbstractParser {

    private final InputStream inputStream;

    private int inputSize;

    private final byte[] inputBuffer = new byte[BUFFER_SIZE];
    private final byte[] CLOSING_SCOPE = new byte[]{',','}',']'};

    private final byte[] BITSHIFT = new byte[]{24,16,8,0};

    private final Event[] profiledEvents = ProfileCollection.getParserProfileCollection();

    private final AbstractValue<?>[] profiledMetadata = ProfileCollection.getMetadataProfileCollection();
    private final SpeculativeTypeTuple[] speculativeTypeTuples;

    private int eventPtr;
    private int profilePtr;
    private int parsingPtr;
    private int speculativeTypesPtr;

    private final byte QUOTE = '"';
    private final byte BCKSL = '\\';

    public SpeculativeParser(InputStream inputStream) {
        speculativeTypeTuples = new SpeculativeTypeTuple[speculationPointers.length];
        this.inputStream = inputStream;
        this.eventPtr = 0;
        this.profilePtr = 0;
        this.parsingPtr = 0;
        this.speculativeTypesPtr = 0;
        readStream();
        buildStructuralIndex();
    }

    @Override
    public boolean hasNext() {
        return this.eventPtr < this.profiledEvents.length;
    }

    @Override
    public Event next() {
        return this.profiledEvents[this.eventPtr++];
    }

    @Override
    public String getString() {
        if (this.profiledEvents[this.eventPtr-1] == Event.KEY_NAME) {
            String ret = (String) profiledMetadata[this.profilePtr++].getValue();
            return ret;
        }
        AbstractValue<?> value = profiledMetadata[this.profilePtr++];
        if (value instanceof StringConstant) {
            return (String) value.getValue();
        } else if (value instanceof StringType) {
            SpeculativeTypeTuple metadata = this.speculativeTypeTuples[this.speculativeTypesPtr++];
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
        AbstractValue<?> value = profiledMetadata[this.profilePtr++];
        if (value instanceof IntegerConstant) {
            return (Integer) value.getValue();
        } else if (value instanceof IntegerType) {
            while (speculationPointers[this.speculativeTypesPtr] < this.eventPtr -1) {
                this.speculativeTypesPtr++;
            }
            SpeculativeTypeTuple metadata = this.speculativeTypeTuples[this.speculativeTypesPtr++];
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
                if (this.parsingPtr == this.inputSize && this.inputBuffer[this.parsingPtr-1] == '}') {
                }
                else if (this.inputBuffer[this.parsingPtr++] != '}') {
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
                byte[] key = ((String) profiledMetadata[this.profilePtr++].getValue()).getBytes();
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
                AbstractValue<?> value = profiledMetadata[this.profilePtr++];
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
                    this.speculativeTypeTuples[this.speculativeTypesPtr++] = new SpeculativeTypeTuple(beginPtr, parsingPtr++ - beginPtr);
                }
                else if (value instanceof Any) {
                    // TODO Have to think what should be done in this case.
                } else {
                    // TODO BIG ERROR
                    throw new RuntimeException("We should never reach this point");
                }
            }
            else if (evt == Event.VALUE_NUMBER) {
                AbstractValue<?> value = profiledMetadata[this.profilePtr++];
                if (value instanceof IntegerConstant) {
                    byte[] byteValue = value.getByteValue();
                    int valuePtr = 0;
                    boolean isCorrect = this.inputBuffer[parsingPtr++] == byteValue[valuePtr++];
                    while (isCorrect && valuePtr < byteValue.length) {
                        isCorrect = this.inputBuffer[parsingPtr++] == byteValue[valuePtr++];
                    }
                    if (!isCorrect || !isByteValid(this.inputBuffer, parsingPtr++, CLOSING_SCOPE)) {
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
                    this.speculativeTypeTuples[this.speculativeTypesPtr++] = new SpeculativeTypeTuple(beginPtr, parsingPtr-1 - beginPtr);
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
                AbstractValue<?> value = profiledMetadata[this.profilePtr++];
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
                    this.speculativeTypeTuples[this.speculativeTypesPtr++] = new SpeculativeTypeTuple(beginPtr, parsingPtr - beginPtr);
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
                AbstractValue<?> value = profiledMetadata[this.profilePtr++];
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
                    this.speculativeTypeTuples[this.speculativeTypesPtr++] = new SpeculativeTypeTuple(beginPtr, parsingPtr - beginPtr);
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
                AbstractValue<?> value = profiledMetadata[this.profilePtr++];
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
                    this.speculativeTypeTuples[this.speculativeTypesPtr++] = new SpeculativeTypeTuple(beginPtr, parsingPtr - beginPtr);
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
        // We reset pointers that will be used while retrieving data.
        this.profilePtr = 0;
        this.speculativeTypesPtr = 0;
    }

    private int getPositionOfByte(byte[] input, int startPos, byte symbol) {
        boolean found = input[startPos++] == symbol;
        while (!found && startPos < input.length) {
            found = input[startPos++] == symbol;
        }
        return startPos; // Returns the position of the next byte to the searched one.
    }

    private int getPositionOfBytes(byte[] input, int startPos, byte[] bytes) { //TODO Change to int v1, int v2...
        int found = -1;
        byte firstByte = bytes[0];
        while (startPos < this.inputSize) {
            startPos = getPositionOfByte(input, startPos, firstByte);
            int ptr = startPos-1;
            boolean match = true;
            for (int i  = 1; match && i < bytes.length && startPos < this.inputSize; i++) {
                match = bytes[i] == input[startPos++];
            }
            if (match) {
                found = ptr;
                break;
            }
        }
        return found;
    }

    /**
     * Intrinsic candidate
     * @param input
     * @param startPos
     * @param i1
     * @return
     */

    private int indexOfConstant(byte[] input, int startPos, int i1) {
        int found = -1;
        byte firstByte = (byte) (i1 >> BITSHIFT[0]);
        while (startPos < this.inputSize) {
            startPos = getPositionOfByte(input, startPos, firstByte);
            int ptr = startPos -1;
            boolean match = true;
            for (int i = 1; match && i < 3 && startPos < this.inputSize; i++) {
                byte b = (byte) (i1 >> BITSHIFT[i]);
                if (b == 0) break;
                match = b == input[startPos++];
            }
            if (match) {
                found = ptr;
                break;
            }
        }
        return found;
    }

    /**
     * Intrinsic candidate
     * @param input
     * @param startPos
     * @param i1
     * @param i2
     * @return
     */
    private int indexOfConstant(byte[] input, int startPos, int i1, int i2) {
        int found = -1;
        byte firstByte = (byte) (i1 >> BITSHIFT[0]);
        while (startPos < this.inputSize) {
            startPos = getPositionOfByte(input, startPos, firstByte);
            int ptr = startPos -1;
            boolean match = true;
            for (int i = 1; match && i < 3 && startPos < this.inputSize; i++) {
                byte b = (byte) (i1 >> BITSHIFT[i]);
                match = b == input[startPos++];
            }
            for (int i = 0; match && i < 3 && startPos < this.inputSize; i++) {
                byte b = (byte) (i2 >> BITSHIFT[i]);
                if (b == 0) break;
                match = b == input[startPos++];
            }
            if (match) {
                found = ptr;
                break;
            }
        }
        return found;
    }

    private int indexOfConstant(byte[] input, int startPos, int i1, int i2, int i3) {
        int found = -1;
        byte firstByte = (byte) (i1 >> BITSHIFT[0]);
        while (startPos < this.inputSize) {
            startPos = getPositionOfByte(input, startPos, firstByte);
            int ptr = startPos -1;
            boolean match = true;
            for (int i = 1; match && i < 3 && startPos < this.inputSize; i++) {
                byte b = (byte) (i1 >> BITSHIFT[i]);
                match = b == input[startPos++];
            }
            for (int i = 0; match && i < 3 && startPos < this.inputSize; i++) {
                byte b = (byte) (i2 >> BITSHIFT[i]);
                match = b == input[startPos++];
            }
            for (int i = 0; match && i < 3 && startPos < this.inputSize; i++) {
                byte b = (byte) (i3 >> BITSHIFT[i]);
                if (b == 0) break;
                match = b == input[startPos++];
            }
            if (match) {
                found = ptr;
                break;
            }
        }
        return found;
    }

    private int indexOfConstant(byte[] input, int startPos, int i1, int i2, int i3, int i4) {
        int found = -1;
        byte firstByte = (byte) (i1 >> BITSHIFT[0]);
        while (startPos < this.inputSize) {
            startPos = getPositionOfByte(input, startPos, firstByte);
            int ptr = startPos -1;
            boolean match = true;
            for (int i = 1; match && i < 3 && startPos < this.inputSize; i++) {
                byte b = (byte) (i1 >> BITSHIFT[i]);
                match = b == input[startPos++];
            }
            for (int i = 0; match && i < 3 && startPos < this.inputSize; i++) {
                byte b = (byte) (i2 >> BITSHIFT[i]);
                match = b == input[startPos++];
            }
            for (int i = 0; match && i < 3 && startPos < this.inputSize; i++) {
                byte b = (byte) (i3 >> BITSHIFT[i]);
                match = b == input[startPos++];
            }
            for (int i = 0; match && i < 3 && startPos < this.inputSize; i++) {
                byte b = (byte) (i4 >> BITSHIFT[i]);
                if (b == 0) break;
                match = b == input[startPos++];
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
