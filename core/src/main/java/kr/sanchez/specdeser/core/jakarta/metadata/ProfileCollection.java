package kr.sanchez.specdeser.core.jakarta.metadata;

import kr.sanchez.specdeser.core.jakarta.AbstractParser;
import kr.sanchez.specdeser.core.jakarta.metadata.values.*;

import javax.json.stream.JsonParser;
import java.util.ArrayList;

public class ProfileCollection {
    private static final ArrayList<AbstractValue<?>> metadataRecord = new ArrayList<>();
    private static final ArrayList<JsonParser.Event> parserEventRecord = new ArrayList<>();

    private ProfileCollection() {

    }

    public static ArrayList<AbstractValue<?>> getMetadataProfileCollection() {
        return (ArrayList<AbstractValue<?>>) metadataRecord.clone();
    }

    public static ArrayList<JsonParser.Event> getParserProfileCollection() {
        return (ArrayList<JsonParser.Event>) parserEventRecord.clone();
    }

    public static void addParserEvent(int execution, int eventStep, JsonParser.Event event) {
        if (execution == 1) {
            parserEventRecord.add(event);
        }
        else {
            if (AbstractParser.speculationEnabled && eventStep < parserEventRecord.size()) {
                if (parserEventRecord.get(eventStep) != event) AbstractParser.speculationEnabled = false;
            }
        }
    }

    public static void addEvent(int execution, int step, AbstractValue<?> value) {
        if (execution == 1) {
            metadataRecord.add(value);
        }
        else {
            if (AbstractParser.speculationEnabled && step < metadataRecord.size()) {
                AbstractValue<?> actualValue = metadataRecord.get(step);
                if (!actualValue.equals(value)) {
                    if (actualValue.getValue() != ValueType.KEY &&
                            actualValue.getType() == value.getType() &&
                            !actualValue.getValue().equals(value.getValue())) { // Here we use equals to cover the String type.
                        updateInternalRecord(step, value.getType());
                    } else {
                        AbstractParser.speculationEnabled = false;
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
        } else {
            newValue = new BooleanType(); // For now we only have these options
        }
        metadataRecord.set(step, newValue);
    }

    // TEST AND DEBUG METHODS //

    public static void resetProfileCollection() {
        ProfileCollection.metadataRecord.clear();
    }
}
