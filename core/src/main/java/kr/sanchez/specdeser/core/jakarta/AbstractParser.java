package kr.sanchez.specdeser.core.jakarta;

import kr.sanchez.specdeser.core.jakarta.metadata.ProfileCollection;
import kr.sanchez.specdeser.core.jakarta.metadata.values.AbstractValue;
import kr.sanchez.specdeser.core.jakarta.metadata.values.ValueType;

import javax.json.stream.JsonParser;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractParser implements JsonParser {

    static final int TH = 1000; // TODO For the final implementation, capture this value from an ENV variable

    static List<String> speculativeKeys = new ArrayList<>();
    static List<String> speculativeStrings = new ArrayList<>();
    static List<Integer> speculativeIntegers = new ArrayList<>();
    static List<Boolean> speculativeLiterals = new ArrayList<>();

    final static int BUFFER_SIZE = 10 * 1024 * 1024;

    static int invocations = 0;
    public static boolean speculationEnabled = true;

    public static AbstractParser create(InputStream inputStream) {
        invocations++;
        if (invocations < TH) {
            return new ProfilingParser(inputStream);
        }
        if (invocations == TH) {
            speculationEnabled = canUseSpeculation();
            buildSpeculativeConstants();
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

    private static void buildSpeculativeConstants() {
        for (AbstractValue<?> value : ProfileCollection.getMetadataProfileCollection()) {
            if (value.getType() == ValueType.KEY) speculativeKeys.add((String) value.getValue());
            else if (value.getType() == ValueType.INT_CONSTANT) speculativeIntegers.add((Integer) value.getValue());
            else if (value.getType() == ValueType.STRING_CONSTANT) speculativeStrings.add((String) value.getValue());
            else if (value.getType() == ValueType.BOOLEAN_CONSTANT) speculativeLiterals.add((Boolean) value.getValue());
        }
    }

    // TEST AND DEBUG FUNCTIONS //

    public static void resetAbstractParser() {
        invocations = 0;
        speculationEnabled = true;
    }
}
