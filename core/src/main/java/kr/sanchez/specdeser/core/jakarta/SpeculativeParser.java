package kr.sanchez.specdeser.core.jakarta;

import kr.sanchez.specdeser.core.jakarta.exception.InputReadException;
import kr.sanchez.specdeser.core.jakarta.metadata.*;
import kr.sanchez.specdeser.core.jakarta.metadata.values.*;

import javax.json.stream.JsonLocation;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

public class SpeculativeParser extends AbstractParser {

    private final InputStream inputStream;

    private int inputSize;

    private final byte[] inputBuffer;

    private final byte[] BITSHIFT = new byte[]{24,16,8,0};

    private final Event[] profiledEvents = ProfileCollection.getParserProfileCollection();

    private final MetadataValue[] profiledMetadata = ProfileCollection.getMetadataProfileCollection();

    private final int[] speculativeTuplePosition;
    private final int[] speculativeTupleSize;

    private final ByteBufferPool byteBufferPool;

    private int eventPtr;
    private int profilePtr;
    private int parsingPtr;
    private int speculativeTypesPtr;

    public SpeculativeParser(InputStream inputStream, ByteBufferPool bufferPool) {
        speculativeTuplePosition = new int[speculationPointers.length];
        speculativeTupleSize = new int[speculationPointers.length];
        this.inputBuffer = bufferPool.take();
        this.inputStream = inputStream;
        this.eventPtr = 0;
        this.profilePtr = 0;
        this.parsingPtr = 0;
        this.speculativeTypesPtr = 0;
        this.byteBufferPool = bufferPool;
        readStream();
        buildVectorizedIndex();
    }

    @Override
    public boolean hasNext() {
        return this.eventPtr < this.profiledEvents.length;
    }

    @Override
    public Event next() {
        Event evt = this.profiledEvents[this.eventPtr++];
        if (evt == Event.VALUE_STRING || evt == Event.VALUE_FALSE || evt == Event.VALUE_NULL || evt == Event.VALUE_TRUE || evt == Event.VALUE_NUMBER) {
            MetadataValue value = this.profiledMetadata[this.profilePtr];
            if (value.type == ValueType.ANY) {
                Event realEvt = detectRealEvent(this.speculativeTuplePosition[this.speculativeTypesPtr], this.speculativeTupleSize[this.speculativeTypesPtr]);
                if (realEvt == null) throwParsingException();
                if (realEvt == Event.VALUE_NULL || realEvt == Event.VALUE_FALSE || realEvt == Event.VALUE_TRUE) {
                    this.speculativeTypesPtr++;
                    this.profilePtr++;
                }
                return realEvt;
            }
        }
        return evt;
    }

    @Override
    public String getString() {
        if (this.profiledEvents[this.eventPtr-1] == Event.KEY_NAME) {
            String ret = profiledMetadata[this.profilePtr++].stringValue;
            return ret;
        }
        MetadataValue value = profiledMetadata[this.profilePtr++];
        if (value.type == ValueType.STRING_CONSTANT) {
            return value.stringValue;
        } else if (value.type == ValueType.STRING_TYPE || value.type == ValueType.ANY) {
            int initialBufferPosition = this.speculativeTuplePosition[this.speculativeTypesPtr];
            int size = this.speculativeTupleSize[this.speculativeTypesPtr++];
            return new String(this.inputBuffer, initialBufferPosition+1, size-2);
        }
        else {
            throw new RuntimeException("Big boom");
        }
    }

    @Override
    public boolean isIntegralNumber() {
        return true;
    }

    @Override
    public int getInt() {
        MetadataValue value = profiledMetadata[this.profilePtr++];
        if (value.type == ValueType.INT_CONSTANT) {
            return value.intValue;
        } else if (value.type == ValueType.INT_TYPE || value.type == ValueType.ANY) {
            return calcSpeculativeInt();
        }
        else {
            throw new RuntimeException("Big boom");
        }
    }

    private int calcSpeculativeInt() {
        int initialBufferPosition = this.speculativeTuplePosition[this.speculativeTypesPtr];
        int size = initialBufferPosition + this.speculativeTupleSize[this.speculativeTypesPtr++];
        int ret = 0;
        while (initialBufferPosition < size) {
            ret = ret * 10 + (this.inputBuffer[initialBufferPosition++] - '0');
        }
        return ret;
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
        this.byteBufferPool.recycle(this.inputBuffer);
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

    private void buildVectorizedIndex() {
        this.parsingPtr = 0; // TODO Remove this line when the implementation is completed.
        int variableIdx = 0;
        int prevInitialPtr = 0;
        int prevSize = 0;
        int metaPtr = 0;
        for (int j = 0; j < vectorizedConstants.length; j++) {
            VectorizedData vectorizedData = vectorizedConstants[j];
            int size = vectorizedData.size;
            final int _size = vectorizedData.size;
            int currentPtr = -1;
            for (int i = 0; i < vectorizedData.data.length; i++) {
                AbstractInt data = vectorizedData.data[i];
                if (data instanceof Int4) {
                    int res = indexOfConstant(this.inputBuffer, this.parsingPtr, data.i1, data.i2, data.i3, data.i4);
                    if (i == 0 && res >= 0 && res <= this.inputSize) this.parsingPtr = currentPtr = res;
                    if (res < 0 || res > this.inputSize) {
                        throwVectorException(res);
                    }
                    else {
                        if (i < vectorizedData.data.length -1) {
                            this.parsingPtr += 16;
                            size -= 16;
                        }
                        else this.parsingPtr = res + size;
                    }
                }
                else if (data instanceof Int3) {
                    int res = indexOfConstant(this.inputBuffer, this.parsingPtr, data.i1, data.i2, data.i3);
                    if (i == 0 && res >= 0 && res <= this.inputSize) this.parsingPtr = currentPtr = res;
                    if (res < 0 || res > this.inputSize) {
                        throwVectorException(res);
                    }
                    else this.parsingPtr = res + size;
                }
                else if (data instanceof Int2) {
                    int res = indexOfConstant(this.inputBuffer, this.parsingPtr, data.i1, data.i2);
                    if (i == 0 && res >= 0 && res <= this.inputSize) this.parsingPtr = currentPtr = res;
                    if (res < 0 || res > this.inputSize) {
                        throwVectorException(res);
                    }
                    else this.parsingPtr = res + size;
                }
                else {
                    int res = indexOfConstant(this.inputBuffer, this.parsingPtr, data.i1);
                    if (i == 0 && res >= 0 && res <= this.inputSize) this.parsingPtr = currentPtr = res;
                    if (res < 0 || res > this.inputSize) {
                        throwVectorException(res);
                    }
                    else this.parsingPtr = res + size;
                }
            }
            if (variableIdx++ != 0) {
                int start = prevInitialPtr + prevSize;
                metaPtr = findNoConstantMeta(metaPtr);
                ValueType type = profiledMetadata[metaPtr].type;
                this.speculativeTuplePosition[variableIdx-2] = start;
                this.speculativeTupleSize[variableIdx-2] = currentPtr - start;
                checkCorrectType(type, start, currentPtr - start, metaPtr);
                prevInitialPtr = currentPtr;
            }
            prevSize = _size;
        }
    }

    private int findNoConstantMeta(int idx) {
        for (; idx < this.profiledMetadata.length;) {
            ValueType type = profiledMetadata[idx].type;
            if (type == ValueType.BOOLEAN_TYPE || type == ValueType.INT_TYPE || type == ValueType.STRING_TYPE || type == ValueType.ANY) {
                return idx;
            }
            idx++;
        }
        return -1;
    }

    private void checkCorrectType(ValueType type, int buffPtr, int size, int metaPtr) {
        if (type == ValueType.STRING_TYPE) {
            if (this.inputBuffer[buffPtr] != '"' || this.inputBuffer[buffPtr+size-1] != '"') {
                AbstractParser.speculationEnabled = false;
            }
            // Don't do anything? Convert to constant?
        }
        else if (type == ValueType.INT_TYPE) {
            if (this.inputBuffer[buffPtr] < '0' || this.inputBuffer[buffPtr] > '9' || this.inputBuffer[buffPtr] != '-') {
                AbstractParser.speculationEnabled = false;
            }
        }
        else if (type == ValueType.BOOLEAN_TYPE) {
            if (size < 4 || size > 5) {
                AbstractParser.speculationEnabled = false;
            }
        }
    }

    private void throwVectorException(int res) { // We avoid this code to be inlined, hopefully boosting performance.
        throw new RuntimeException("Crash during vectorized checking. Index was: " + res);
    }

    private int getPositionOfByte(byte[] input, int startPos, byte symbol) {
        boolean found = input[startPos++] == symbol;
        while (!found && startPos < input.length) {
            found = input[startPos++] == symbol;
        }
        return startPos; // Returns the position of the next byte to the searched one.
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
            for (int i = 1; match && i < 4 && startPos < this.inputSize; i++) {
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
            for (int i = 1; match && i < 4 && startPos < this.inputSize; i++) {
                byte b = (byte) (i1 >> BITSHIFT[i]);
                match = b == input[startPos++];
            }
            for (int i = 0; match && i < 4 && startPos < this.inputSize; i++) {
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

    /**
     *
     * @param input
     * @param startPos
     * @param i1
     * @param i2
     * @param i3
     * @return
     */
    private int indexOfConstant(byte[] input, int startPos, int i1, int i2, int i3) {
        int found = -1;
        byte firstByte = (byte) (i1 >> BITSHIFT[0]);
        while (startPos < this.inputSize) {
            startPos = getPositionOfByte(input, startPos, firstByte);
            int ptr = startPos -1;
            boolean match = true;
            for (int i = 1; match && i < 4 && startPos < this.inputSize; i++) {
                byte b = (byte) (i1 >> BITSHIFT[i]);
                match = b == input[startPos++];
            }
            for (int i = 0; match && i < 4 && startPos < this.inputSize; i++) {
                byte b = (byte) (i2 >> BITSHIFT[i]);
                match = b == input[startPos++];
            }
            for (int i = 0; match && i < 4 && startPos < this.inputSize; i++) {
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

    /**
     *
     * @param input
     * @param startPos
     * @param i1
     * @param i2
     * @param i3
     * @param i4
     * @return
     */
    private int indexOfConstant(byte[] input, int startPos, int i1, int i2, int i3, int i4) {
        int found = -1;
        byte firstByte = (byte) (i1 >> BITSHIFT[0]);
        while (startPos < this.inputSize) {
            startPos = getPositionOfByte(input, startPos, firstByte);
            int ptr = startPos -1;
            boolean match = true;
            for (int i = 1; match && i < 4 && startPos < this.inputSize; i++) {
                byte b = (byte) (i1 >> BITSHIFT[i]);
                match = b == input[startPos++];
            }
            for (int i = 0; match && i < 4 && startPos < this.inputSize; i++) {
                byte b = (byte) (i2 >> BITSHIFT[i]);
                match = b == input[startPos++];
            }
            for (int i = 0; match && i < 4 && startPos < this.inputSize; i++) {
                byte b = (byte) (i3 >> BITSHIFT[i]);
                match = b == input[startPos++];
            }
            for (int i = 0; match && i < 4 && startPos < this.inputSize; i++) {
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

    private Event detectRealEvent(int initialPos, int size) {
        byte b1 = this.inputBuffer[initialPos++];
        switch (b1) {
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-' -> {
                boolean isNumber = true;
                for (int i = initialPos; isNumber && i < initialPos + size-1; i++) {
                    isNumber = this.inputBuffer[i] >= '0' && this.inputBuffer[i] <= '9';
                }
                if (isNumber) return Event.VALUE_NUMBER;
            }
            case '"' -> {
                if (this.inputBuffer[initialPos + size -2] == '"') return Event.VALUE_STRING; // Assumption that this is a correct string.
            }
            case 'f' -> {
                if (size == 5 && this.inputBuffer[initialPos++] == 'a' && this.inputBuffer[initialPos++] == 'l' &&
                        this.inputBuffer[initialPos++] == 's' && this.inputBuffer[initialPos++] == 'e') return Event.VALUE_FALSE;

            }
            case 't' -> {
                if (size == 4 && this.inputBuffer[initialPos++] == 'r' && this.inputBuffer[initialPos++] == 'u' &&
                        this.inputBuffer[initialPos++] == 'e') return Event.VALUE_TRUE;
            }
            case 'n' -> {
                if (size == 4 && this.inputBuffer[initialPos++] == 'u' && this.inputBuffer[initialPos++] == 'l' &&
                        this.inputBuffer[initialPos++] == 'l') return Event.VALUE_NULL;
            }
        }
        return null;
    }

    private void throwParsingException() {
        throw new RuntimeException("Parsing Exception");
    }
}
