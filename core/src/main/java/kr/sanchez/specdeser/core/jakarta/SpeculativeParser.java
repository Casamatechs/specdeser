package kr.sanchez.specdeser.core.jakarta;

import kr.sanchez.specdeser.core.jakarta.exception.InputReadException;
import kr.sanchez.specdeser.core.jakarta.metadata.*;
import kr.sanchez.specdeser.core.jakarta.metadata.values.*;
import sun.misc.Unsafe;

import javax.json.stream.JsonLocation;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;

public class SpeculativeParser extends AbstractParser {

    private final InputStream inputStream;

    private int inputSize;

    private final byte[] inputBuffer;

    private final Event[] profiledEvents = ProfileCollection.getParserProfileCollection();

    private final MetadataValue[] profiledMetadata = ProfileCollection.getMetadataProfileCollection();

    private final int[] speculativeTuplePosition;
    private final int[] speculativeTupleSize;

    private final ByteBufferPool byteBufferPool;

    private int eventPtr;
    private int profilePtr;
    private int parsingPtr;
    private int speculativeTypesPtr;

    private static final Unsafe UNSAFE = getUnsafe();

    private static Unsafe getUnsafe() {
        try {
            return Unsafe.getUnsafe();
        } catch (SecurityException e1) {
            try {
                Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
                theUnsafeInstance.setAccessible(true);
                return (Unsafe) theUnsafeInstance.get(Unsafe.class);
            } catch (Exception e2) {
                throw new RuntimeException("exception while trying to get Unsafe.theUnsafe via reflection:", e2);
            }
        }
    }

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
        return this.profiledEvents[this.eventPtr++];
    }

    @Override
    public String getString() {
        if (this.profiledEvents[this.eventPtr - 1] == Event.KEY_NAME) {
            return profiledMetadata[this.profilePtr++].stringValue;
        }
        MetadataValue value = profiledMetadata[this.profilePtr++];
        if (value.type == ValueType.STRING_CONSTANT) {
            return value.stringValue;
        } else if (value.type == ValueType.STRING_TYPE) {
            int initialBufferPosition = this.speculativeTuplePosition[this.speculativeTypesPtr];
            int size = this.speculativeTupleSize[this.speculativeTypesPtr++];
            return new String(this.inputBuffer, initialBufferPosition + 1, size - 2);
        } else {
            throw new RuntimeException("Unexpected behavior");
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
        } else if (value.type == ValueType.INT_TYPE) {
            return calcSpeculativeInt();
        } else {
            throw new RuntimeException("Unexpected behavior");
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
        for (int j = 0; j < vectorizedConstants.length; j++) {
            VectorizedData vectorizedData = vectorizedConstants[j];
            int size = vectorizedData.size;
            final int _size = vectorizedData.size;
            int currentPtr = -1;
            AbstractInt data = vectorizedData.firstPair;
            if (data instanceof Int2) {
                int res = indexOfConstant(this.inputBuffer, this.parsingPtr, this.inputSize, data.i1, data.i2);
                if (res >= 0 && res <= this.inputSize) this.parsingPtr = currentPtr = res;
                if (res < 0 || res > this.inputSize) {
                    throwVectorException(res);
                }
                if (!equalsOfConstant(this.inputBuffer, res, vectorizedData.arrayData, vectorizedData.size)) {
                    throwVectorException(res);
                } else this.parsingPtr = res + vectorizedData.size;
            } else {
                int res = indexOfConstant(this.inputBuffer, this.parsingPtr, this.inputSize, data.i1);
                if (res >= 0 && res <= this.inputSize) this.parsingPtr = currentPtr = res;
                if (res < 0 || res > this.inputSize) {
                    throwVectorException(res);
                }
                if (!equalsOfConstant(this.inputBuffer, res, vectorizedData.arrayData, vectorizedData.size)) {
                    throwVectorException(res);
                } else this.parsingPtr = res + vectorizedData.size;
            }
            if (variableIdx++ != 0) {
                int start = prevInitialPtr + prevSize;
                this.speculativeTuplePosition[variableIdx - 2] = start;
                this.speculativeTupleSize[variableIdx - 2] = currentPtr - start;
                prevInitialPtr = currentPtr;
            }
            prevSize = _size;
        }
    }

    private void throwVectorException(int res) { // We avoid this exception to be inlined if it's not called.
        throw new RuntimeException("Crash during vectorized checking.Index was: " + res + "\n");
    }

    private int indexOfConstant(byte[] input, int startPos, int inputSize, int i1) {
        return indexOfConstant(null, input, 16L, inputSize, 0, false, startPos, i1);
    }

    public static int indexOfConstant(byte[] input, int startPos, int inputSize, int i1, int i2) {
        return indexOfConstant(null, input, 16L, inputSize, 0, false, startPos, i1, i2);
    }

    public static boolean equalsOfConstant(byte[] input, int inputPtr, byte[] constant, int constantLength) {
        return equalsOfConstant(null, input, 16L + inputPtr, false, constant, 16L, false, constantLength, 0);
    }

    // GraalVM intrinsics code

    /**
     * Intrinsic candidate
     */
    private static int indexOfConstant(Object location, byte[] array, long offset, int length, int stride, boolean isNative, int fromIndex, int i1) {
        for (int i = fromIndex; i < length; i++) {
            if (readValue(array, offset, stride, i, isNative) == i1)
                return i;
        }
        return -1;
    }

    /**
     * Intrinsic candidate
     */
    private static int indexOfConstant(Object location, byte[] array, long offset, int length, int stride, boolean isNative, int fromIndex, int i1, int i2) {
        for (int i = fromIndex + 1; i < length; i++) {
            if (readValue(array, offset, stride, i - 1, isNative) == i1 && readValue(array, offset, stride, i, isNative) == i2)
                return i - 1;
        }
        return -1;
    }

    /**
     * Intrinsic candidate
     */
    private static boolean equalsOfConstant(Object location,
                                            byte[] arrayA, long offsetA, boolean isNativeA,
                                            byte[] arrayB, long offsetB, boolean isNativeB, int length, int stubStride) {
        int strideA = stubStrideToStrideA(stubStride);
        int strideB = stubStrideToStrideB(stubStride);
        for (int i = 0; i < length; i++) {
            if (readValue(arrayA, offsetA, strideA, i, isNativeA) != readValue(arrayB, offsetB, strideB, i, isNativeB)) {
                return false;
            }
        }
        return true;
    }

    private static int readValue(byte[] array, long offset, int stride, int i, boolean isNative) {
        return runReadS0Managed(array, offset + i);
    }

    private static int runReadS0Managed(byte[] array, long byteOffset) {
        return uInt(UNSAFE.getByte(array, byteOffset));
    }

    private static int uInt(byte value) {
        return Byte.toUnsignedInt(value);
    }

    private static int stubStrideToStrideA(int stubStride) {
        assert 0 <= stubStride && stubStride < 9 : stubStride;
        return stubStride / 3;
    }

    private static int stubStrideToStrideB(int stubStride) {
        assert 0 <= stubStride && stubStride < 9 : stubStride;
        return stubStride % 3;
    }
}
