package kr.sanchez.specdeser.core.jakarta;

import kr.sanchez.specdeser.core.jakarta.metadata.AbstractInt;
import kr.sanchez.specdeser.core.jakarta.metadata.ProfileCollection;
import kr.sanchez.specdeser.core.jakarta.metadata.VectorizedData;
import kr.sanchez.specdeser.core.jakarta.metadata.values.*;

import javax.json.stream.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public abstract class AbstractParser implements JsonParser {
    public static final byte[] BITSHIFT = new byte[]{24,16,8,0};

    static final int TH = 1000; // TODO For the final implementation, capture this value from an ENV variable

    static Integer[] speculationPointers;

    static VectorizedData[] vectorizedConstants;
    final static int BUFFER_SIZE = 10 * 1024 * 1024;

    static int invocations = 0;
    public static boolean speculationEnabled = true;

    final byte[] TRUE = new byte[]{'t', 'r', 'u', 'e'};
    final byte[] FALSE = new byte[]{'f', 'a', 'l', 's', 'e'};
    final byte[] NULL = new byte[]{'n', 'u', 'l', 'l'};

    public static AbstractParser create(InputStream inputStream) {
        invocations++;
        if (invocations < TH) {
            return new ProfilingParser(inputStream);
        }
        if (invocations == TH) {
            speculationEnabled = canUseSpeculation();
            if (speculationEnabled) {
                speculationPointers = ProfileCollection.getSpeculativeTypes();
                vectorizedConstants = buildSpeculativeConstants();
            }
        }
        if (speculationEnabled) {
            return new SpeculativeParser(inputStream);
        } else {
            return new FallbackParser(inputStream);
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
        String constantStr = "";
        Event[] profiledEvents = ProfileCollection.getParserProfileCollection();
        MetadataValue[] profiledMetadata = ProfileCollection.getMetadataProfileCollection();
        ArrayList<VectorizedData> provisionalArrayList = new ArrayList<>();
        boolean keepConstant = true;
        while (evtPtr < profiledEvents.length) {
            if (profiledEvents[evtPtr] == Event.START_OBJECT) {
                constantStr += "{";
            }
            else if (profiledEvents[evtPtr] == Event.END_OBJECT) {
                constantStr = evtPtr == profiledEvents.length -1 && constantStr.equals(",") ? "}" : constantStr + "}";
                VectorizedData vectorizedData = new VectorizedData(buildVectorizedValue(constantStr), constantStr.length());
                provisionalArrayList.add(vectorizedData);
                break; // TODO Remove this when we have real
            }
            else if (profiledEvents[evtPtr] == Event.START_ARRAY) {
                constantStr += "[";
            }
            else if (profiledEvents[evtPtr] == Event.END_ARRAY) {
                constantStr += "]";
            }
            else if (profiledEvents[evtPtr] == Event.KEY_NAME) {
                MetadataValue value = profiledMetadata[metaPtr++];
                if (value.type == ValueType.KEY) {
                    constantStr += "\"" + value.stringValue + "\":";
                }
            }
            else if (profiledEvents[evtPtr] == Event.VALUE_NUMBER) {
                MetadataValue value = profiledMetadata[metaPtr++];
                if (value.type == ValueType.INT_CONSTANT) {
                    constantStr += evtPtr < profiledEvents.length - 2 ? value.intValue + "," : value.intValue;
                } else keepConstant = false;
            }
            else if (profiledEvents[evtPtr] == Event.VALUE_STRING) {
                MetadataValue value = profiledMetadata[metaPtr++];
                if (value.type == ValueType.STRING_CONSTANT) {
                    constantStr += evtPtr < profiledEvents.length - 2 ? "\"" + value.stringValue + "\"," : "\"" + value.stringValue + "\"";
                } else keepConstant = false;
            }
            else if (profiledEvents[evtPtr] == Event.VALUE_TRUE) {
                MetadataValue value = profiledMetadata[metaPtr++];
                if (value.type == ValueType.BOOLEAN_CONSTANT && value.booleanValue) {
                    constantStr += evtPtr < profiledEvents.length - 2 ? "true," : "true";
                } else keepConstant = false;
            }
            else if (profiledEvents[evtPtr] == Event.VALUE_FALSE) {
                MetadataValue value = profiledMetadata[metaPtr++];
                if (value.type == ValueType.BOOLEAN_CONSTANT && !value.booleanValue) {
                    constantStr += evtPtr < profiledEvents.length - 2 ? "false," : "false";
                } else keepConstant = false;
            }
            else if (profiledEvents[evtPtr] == Event.VALUE_NULL) {
                MetadataValue value = profiledMetadata[metaPtr++];
                if (value.type == ValueType.NULL_CONSTANT) {
                    constantStr += evtPtr < profiledEvents.length - 2 ? "null," : "null";
                } else keepConstant = false;
            }
            if (!keepConstant) {
                VectorizedData vectorizedData = new VectorizedData(buildVectorizedValue(constantStr), constantStr.length());
                provisionalArrayList.add(vectorizedData);
                constantStr = ","; // TODO Check if we are at the end of an object/array or not
                keepConstant = true;
            }
            evtPtr++;
        }
        return provisionalArrayList.toArray(new VectorizedData[0]);
    }

    private static AbstractInt[] buildVectorizedValue(String input) {
        int size = input.length();
        byte[] byteArray = input.getBytes(StandardCharsets.UTF_8);
        AbstractInt[] res = size % 16 == 0 ? new AbstractInt[size/16] : new AbstractInt[size/16 + 1];
        int arrPtr, resPtr;
        arrPtr = resPtr = 0;
        while (arrPtr < size -4) {
            res[resPtr++] = AbstractInt.create(byteArray, arrPtr);
            arrPtr += 16;
        }
        if (resPtr < res.length) res[resPtr] = AbstractInt.create(byteArray, arrPtr);
        return res;
    }

    // TEST AND DEBUG FUNCTIONS //

    public static void resetAbstractParser() {
        invocations = 0;
        speculationEnabled = true;
    }
}
