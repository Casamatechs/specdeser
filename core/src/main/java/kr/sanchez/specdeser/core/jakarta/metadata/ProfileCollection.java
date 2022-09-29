package kr.sanchez.specdeser.core.jakarta.metadata;

import kr.sanchez.specdeser.core.jakarta.AbstractParser;
import kr.sanchez.specdeser.core.jakarta.metadata.values.*;

import javax.json.stream.JsonParser;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class ProfileCollection {
    private static final ArrayList<MetadataValue> metadataRecord = new ArrayList<>();
    private static final ArrayList<JsonParser.Event> parserEventRecord = new ArrayList<>();

    private static final Set<Integer> ret = new TreeSet<>();

    private ProfileCollection() {

    }

    public static MetadataValue[] getMetadataProfileCollection() {
        return metadataRecord.toArray(new MetadataValue[0]);
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

    public static void addEvent(int execution, int step, int eventStep, MetadataValue value) {
        if (execution == 1) {
            metadataRecord.add(value);
        }
        else {
            if (AbstractParser.speculationEnabled && step < metadataRecord.size()) {
                MetadataValue actualValue = metadataRecord.get(step);
                if (!actualValue.equals(value)) {
                    if (actualValue.type == ValueType.KEY) {
                        AbstractParser.speculationEnabled = false;
                    }
                    else if (isCompatibleType(value, actualValue)) { // Here we use equals to cover the String type.
                        ret.add(eventStep);
                        updateInternalRecord(step, value.type);
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
        MetadataValue newValue;
        if (type == ValueType.STRING_CONSTANT) {
            newValue = new MetadataValue(ValueType.STRING_TYPE);
        } else if (type == ValueType.INT_CONSTANT) {
            newValue = new MetadataValue(ValueType.INT_TYPE);
        } else if (type == ValueType.ANY) {
            newValue = new MetadataValue(ValueType.ANY);
        }else {
            newValue = new MetadataValue(ValueType.BOOLEAN_TYPE); // For now we only have these options
        }
        metadataRecord.set(step, newValue);
    }

    public static Integer[] getSpeculativeTypes() {
        return ret.toArray(new Integer[0]);
    }

    private static boolean isSpeculative(ValueType type) {
        return type == ValueType.ANY || type == ValueType.INT_TYPE || type == ValueType.BOOLEAN_TYPE || type == ValueType.STRING_TYPE;
    }
    // TEST AND DEBUG METHODS //

    public static void resetProfileCollection() {
        ProfileCollection.metadataRecord.clear();
        ProfileCollection.parserEventRecord.clear();
        ProfileCollection.ret.clear();
    }

    private static boolean isCompatibleType(MetadataValue parsedValue, MetadataValue currentValue) {
        if (parsedValue.type == ValueType.STRING_CONSTANT &&
                (currentValue.type == ValueType.STRING_TYPE || currentValue.type == ValueType.STRING_CONSTANT) &&
                !parsedValue.stringValue.equals(currentValue.stringValue)) {
            return true;
        }
        if (parsedValue.type == ValueType.INT_CONSTANT &&
                (currentValue.type == ValueType.INT_TYPE || currentValue.type == ValueType.INT_CONSTANT) &&
                parsedValue.intValue != currentValue.intValue) {
            return true;
        }
        if (parsedValue.type == ValueType.BOOLEAN_CONSTANT &&
                (currentValue.type == ValueType.BOOLEAN_TYPE || currentValue.type == ValueType.BOOLEAN_CONSTANT) &&
                parsedValue.booleanValue != currentValue.booleanValue) {
            return true;
        }
        if (parsedValue.type == ValueType.NULL_CONSTANT && currentValue.type == ValueType.NULL_CONSTANT) {
            return true;
        }
        return false;
    }
}
