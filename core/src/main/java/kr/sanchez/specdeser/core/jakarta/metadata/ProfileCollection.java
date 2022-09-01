package kr.sanchez.specdeser.core.jakarta.metadata;

import kr.sanchez.specdeser.core.jakarta.AbstractParser;
import kr.sanchez.specdeser.core.jakarta.metadata.values.*;

import javax.json.stream.JsonParser;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class ProfileCollection {
    private static final ArrayList<AbstractValue<?>> metadataRecord = new ArrayList<>();
    private static final ArrayList<JsonParser.Event> parserEventRecord = new ArrayList<>();

    private static final Set<Integer> ret = new TreeSet<>();

    private ProfileCollection() {

    }

    public static AbstractValue<?>[] getMetadataProfileCollection() {
        return metadataRecord.toArray(new AbstractValue[0]);
    }

    public static JsonParser.Event[] getParserProfileCollection() {
        return parserEventRecord.toArray(new JsonParser.Event[0]);
    }

    public static void addParserEvent(int execution, int eventStep, JsonParser.Event event) {
        if (execution == 1) {
            parserEventRecord.add(event);
        }
        else {
            if (AbstractParser.speculationEnabled && eventStep < parserEventRecord.size()) {
                if (event == JsonParser.Event.KEY_NAME && parserEventRecord.get(eventStep) != event) AbstractParser.speculationEnabled = false;
            }
        }
    }

    public static void addEvent(int execution, int step, int eventStep, AbstractValue<?> value) {
        if (execution == 1) {
            metadataRecord.add(value);
        }
        else {
            if (AbstractParser.speculationEnabled && step < metadataRecord.size()) {
                AbstractValue<?> actualValue = metadataRecord.get(step);
                if (!actualValue.equals(value)) {
                    if (actualValue.getType() == ValueType.KEY) {
                        AbstractParser.speculationEnabled = false;
                    }
                    else if (isCompatibleType(value, actualValue) &&
                            !value.getValue().equals(actualValue.getValue())) { // Here we use equals to cover the String type.
                        ret.add(eventStep);
                        updateInternalRecord(step, value.getType());
                    } else {
                        ret.add(eventStep);
                        updateInternalRecord(step, ValueType.ANY);
                    }
                }
            } else {
                AbstractParser.speculationEnabled = false;
            }
        }
    }

    private static void updateInternalRecord(int step, ValueType type) {
        AbstractValue<?> newValue;
        if (type == ValueType.STRING_CONSTANT) {
            newValue = new StringType();
        } else if (type == ValueType.INT_CONSTANT) {
            newValue = new IntegerType();
        } else if (type == ValueType.ANY) {
            newValue = new Any();
        }else {
            newValue = new BooleanType(); // For now we only have these options
        }
        metadataRecord.set(step, newValue);
    }

    public static Integer[] getSpeculativeTypes() {
//        for (int i = 0; i < metadataRecord.size(); i++) {
//            if (isSpeculative(metadataRecord.get(i).getType())) ret.add(i);
//        }
//        return ret.toArray(new Integer[0]);
        return ret.toArray(new Integer[0]);
    }

    public static int byteToInt(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8) |
                ((bytes[3] & 0xFF));
    }

    private static boolean isSpeculative(ValueType type) {
        return type == ValueType.ANY || type == ValueType.INT_TYPE || type == ValueType.BOOLEAN_TYPE || type == ValueType.STRING_TYPE;
    }
    // TEST AND DEBUG METHODS //

    public static void resetProfileCollection() {
        ProfileCollection.metadataRecord.clear();
    }

    private static boolean isCompatibleType(AbstractValue parsedValue, AbstractValue currentValue) {
        if (parsedValue.getType() == ValueType.STRING_CONSTANT &&
                (currentValue.getType() == ValueType.STRING_TYPE || currentValue.getType() == ValueType.STRING_CONSTANT)) {
            return true;
        }
        if (parsedValue.getType() == ValueType.INT_CONSTANT &&
                (currentValue.getType() == ValueType.INT_TYPE || currentValue.getType() == ValueType.INT_CONSTANT)) {
            return true;
        }
        if (parsedValue.getType() == ValueType.BOOLEAN_CONSTANT &&
                (currentValue.getType() == ValueType.BOOLEAN_TYPE || currentValue.getType() == ValueType.BOOLEAN_CONSTANT)) {
            return true;
        }
        return false;
    }
}
