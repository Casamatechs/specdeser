package kr.sanchez.specdeser.core.jakarta;

import kr.sanchez.specdeser.core.jakarta.metadata.AbstractInt;
import kr.sanchez.specdeser.core.jakarta.metadata.ProfileCollection;
import kr.sanchez.specdeser.core.jakarta.metadata.VectorizedData;
import kr.sanchez.specdeser.core.jakarta.metadata.values.*;

import javax.json.stream.JsonParser;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class AbstractParser implements JsonParser {
    public static final byte[] BITSHIFT = new byte[]{24,16,8,0};

    static final int TH = System.getenv("PROFILING_EXECUTIONS") != null ? Integer.parseInt(System.getenv("PROFILING_EXECUTIONS")) : 10;

    static Integer[] speculationPointers;

    static VectorizedData[] vectorizedConstants;
    public final static int BUFFER_SIZE = 10 * 1024 * 1024;

    public static int invocations = 0;
    public static boolean speculationEnabled = true;

    final byte[] TRUE = new byte[]{'t', 'r', 'u', 'e'};
    final byte[] FALSE = new byte[]{'f', 'a', 'l', 's', 'e'};
    final byte[] NULL = new byte[]{'n', 'u', 'l', 'l'};

    public static AbstractParser create(InputStream inputStream, ByteBufferPool bufferPool) {
        invocations++;
        if (invocations < TH) {
            return new ProfilingParser(inputStream, bufferPool);
        }
        if (invocations == TH) {
            speculationEnabled = canUseSpeculation();
            if (speculationEnabled) {
                speculationPointers = ProfileCollection.getSpeculativeTypes();
                vectorizedConstants = buildSpeculativeConstants();
            }
        }
        if (speculationEnabled) {
            return new SpeculativeParser(inputStream, bufferPool);
        } else {
            return new FallbackParser(inputStream, bufferPool);
        }
    }

    private static boolean canUseSpeculation() { // TODO Build final speculative metadata and return true if success.
        return true;
    }

    /**
     * This function will build the constants to execute along with SIMD intrinsics.
     */
    private static VectorizedData[] buildSpeculativeConstants() {
        int evtPtr = 0;
        int metaPtr = 0;
        Event[] profiledEvents = ProfileCollection.getParserProfileCollection();
        MetadataValue[] profiledMetadata = ProfileCollection.getMetadataProfileCollection();
        final byte[] readVectorArray = new byte[BUFFER_SIZE];
        int readVectorPtr = 0;
        ArrayList<VectorizedData> provisionalArrayList = new ArrayList<>();
        boolean keepConstant = true;
        while (evtPtr < profiledEvents.length) {
            if (profiledEvents[evtPtr] == Event.START_OBJECT) {
                readVectorArray[readVectorPtr++] = '{';
            }
            else if (profiledEvents[evtPtr] == Event.END_OBJECT) {
                if (evtPtr == profiledEvents.length -1 && readVectorPtr == 1 && readVectorArray[0] == ',') {
                    readVectorArray[0] = '}';
                } else readVectorArray[readVectorPtr++] = '}';
                VectorizedData vectorizedData = new VectorizedData(AbstractInt.create(readVectorArray, 0, readVectorPtr > 1 ? 2 : 1), Arrays.copyOfRange(readVectorArray, 0, readVectorPtr));
                provisionalArrayList.add(vectorizedData);
                break;
            }
            else if (profiledEvents[evtPtr] == Event.START_ARRAY) {
                readVectorArray[readVectorPtr++] = '[';
            }
            else if (profiledEvents[evtPtr] == Event.END_ARRAY) {
                readVectorArray[readVectorPtr++] = ']';
            }
            else if (profiledEvents[evtPtr] == Event.KEY_NAME) {
                MetadataValue value = profiledMetadata[metaPtr++];
                if (value.type == ValueType.KEY) {
                    readVectorPtr = copyStringBytes(value.byteValue, readVectorArray, readVectorPtr);
                    readVectorArray[readVectorPtr++] = ':';
                }
            }
            else if (profiledEvents[evtPtr] == Event.VALUE_NUMBER) {
                MetadataValue value = profiledMetadata[metaPtr++];
                if (value.type == ValueType.INT_CONSTANT) {
                    readVectorPtr = copyVectorBytes(value.byteValue, readVectorArray, readVectorPtr);
                    if (evtPtr < profiledEvents.length - 2) readVectorArray[readVectorPtr++] = ',';
                } else keepConstant = false;
            }
            else if (profiledEvents[evtPtr] == Event.VALUE_STRING) {
                MetadataValue value = profiledMetadata[metaPtr++];
                if (value.type == ValueType.STRING_CONSTANT) {
                    readVectorPtr = copyStringBytes(value.byteValue, readVectorArray, readVectorPtr);
                    if (evtPtr < profiledEvents.length - 2) {
                        readVectorArray[readVectorPtr++] = ',';
                    }
                } else keepConstant = false;
            }
            else if (profiledEvents[evtPtr] == Event.VALUE_TRUE) {
                MetadataValue value = profiledMetadata[metaPtr++];
                if (value.type == ValueType.BOOLEAN_CONSTANT && value.booleanValue) {
                    readVectorPtr = copyVectorBytes(value.byteValue, readVectorArray, readVectorPtr);
                    if (evtPtr < profiledEvents.length - 2) readVectorArray[readVectorPtr++] = ',';
                } else keepConstant = false;
            }
            else if (profiledEvents[evtPtr] == Event.VALUE_FALSE) {
                MetadataValue value = profiledMetadata[metaPtr++];
                if (value.type == ValueType.BOOLEAN_CONSTANT && !value.booleanValue) {
                    readVectorPtr = copyVectorBytes(value.byteValue, readVectorArray, readVectorPtr);
                    if (evtPtr < profiledEvents.length - 2) readVectorArray[readVectorPtr++] = ',';
                } else keepConstant = false;
            }
            else if (profiledEvents[evtPtr] == Event.VALUE_NULL) {
                MetadataValue value = profiledMetadata[metaPtr++];
                if (value.type == ValueType.NULL_CONSTANT) {
                    readVectorPtr = copyVectorBytes(value.byteValue, readVectorArray, readVectorPtr);
                    if (evtPtr < profiledEvents.length - 2) readVectorArray[readVectorPtr++] = ',';
                } else keepConstant = false;
            }
            if (!keepConstant) {
                VectorizedData vectorizedData = new VectorizedData(AbstractInt.create(readVectorArray, 0, 2), Arrays.copyOfRange(readVectorArray, 0, readVectorPtr));
                provisionalArrayList.add(vectorizedData);
                readVectorPtr = 0;
                readVectorArray[readVectorPtr++] = ',';
                keepConstant = true;
            }
            evtPtr++;
        }
        return provisionalArrayList.toArray(new VectorizedData[0]);
    }

    private static int copyStringBytes(byte[] input, byte[] vectorArray, int vectorPtr) {
        vectorArray[vectorPtr++] = '\"';
        for (byte b : input) {
            vectorArray[vectorPtr++] = b;
        }
        vectorArray[vectorPtr++] = '\"';
        return vectorPtr;
    }

    private static int copyVectorBytes(byte[] input, byte[] vectorArray, int vectorPtr) {
        for (byte b : input) {
            vectorArray[vectorPtr++] = b;
        }
        return vectorPtr;
    }

    // TEST AND DEBUG FUNCTIONS //

    public static void resetAbstractParser() {
        invocations = 0;
        speculationEnabled = true;
    }
}
