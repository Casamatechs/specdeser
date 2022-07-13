package kr.sanchez.specdeser.core.jakarta;

import javax.json.stream.JsonParser;
import java.io.InputStream;

public abstract class AbstractParser implements JsonParser {

    static final int TH = 1000; // TODO For the final implementation, capture this value from an ENV variable

    static int invocations = 0;
    public static boolean speculationEnabled = true;

    public static AbstractParser create(InputStream inputStream) {
        invocations++;
        if (invocations < TH) {
            return new ProfilingParser(inputStream);
        }
        if (invocations == TH) {
            speculationEnabled = canUseSpeculation();
        }
        if (speculationEnabled) {
            return new SpeculativeParser();
        } else {
            return new FallbackParser(inputStream);
        }
    }

    private static boolean canUseSpeculation() { // TODO Build final speculative metadata and return true if success.
        return true;
    }

    // TEST AND DEBUG FUNCTIONS //

    public static void resetAbstractParser() {
        invocations = 0;
        speculationEnabled = true;
    }
}
