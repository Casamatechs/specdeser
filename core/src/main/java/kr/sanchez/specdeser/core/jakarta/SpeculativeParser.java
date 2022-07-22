package kr.sanchez.specdeser.core.jakarta;

import kr.sanchez.specdeser.core.jakarta.exception.InputReadException;
import kr.sanchez.specdeser.core.jakarta.metadata.ParsingConstants;
import kr.sanchez.specdeser.core.jakarta.metadata.ProfileCollection;

import javax.json.stream.JsonLocation;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class SpeculativeParser extends AbstractParser{

    private final InputStream inputStream;

    private int inputSize;

    private final byte[] inputBuffer = new byte[BUFFER_SIZE];

    private static final ParsingConstants[] constantTokens = ParsingConstants.values();

    private final boolean[] colonTape = new boolean[BUFFER_SIZE];

    private final boolean[] OK = new boolean[BUFFER_SIZE];
    private final boolean[] CK = new boolean[BUFFER_SIZE];
    private final boolean[] COLON = new boolean[BUFFER_SIZE];
    private final boolean[] STR = new boolean[BUFFER_SIZE];
    private final boolean[] BACKSLASH = new boolean[BUFFER_SIZE];

    private final List<Event> profiledEvents = ProfileCollection.getParserProfileCollection();

    private int eventPtr;
    private int keysPtr;
    private int stringPtr;
    private int integerPtr;
    private int literalPtr;

    public SpeculativeParser(InputStream inputStream) {
        this.inputStream = inputStream;
        this.eventPtr = 0;
        this.keysPtr = 0;
        this.stringPtr = 0;
        this.integerPtr = 0;
        this.literalPtr = 0;
        readStream();

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
        return speculativeStrings.get(stringPtr);
    }

    @Override
    public boolean isIntegralNumber() {
        return false;
    }

    @Override
    public int getInt() {
        return speculativeIntegers.get(integerPtr++);
    }

    @Override
    public long getLong() {
        return 0;
    }

    @Override
    public BigDecimal getBigDecimal() {
        return null;
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
            this.inputSize = this.inputStream.read(this.inputBuffer);
        } catch (IOException e) {
            throw new InputReadException(e.getMessage());
        }
    }

    /**
     * Intrinsic candidate
     */
    private void buildColonTape() {
        int foundIndex = -1;
        for (int i = 0; i < inputSize; i++) {
            foundIndex = indexOf(inputBuffer, (byte) ':', foundIndex + 1, inputSize);
            if (foundIndex > 0) colonTape[foundIndex] = true;
        }
    }

    /**
     * Intrinsic candidate
     */
    private int indexOf(byte[] array, byte value, int offset, int bufferSize) {
        for (int i = offset; i < bufferSize; i++) {
            if (array[i] == value) return i;
        }
        return -1;
    }
}
