package kr.sanchez.specdeser.core.jakarta.metadata;

import kr.sanchez.specdeser.core.jakarta.AbstractParser;
import kr.sanchez.specdeser.core.jakarta.metadata.values.*;

import java.util.ArrayList;

public class ProfileCollection {
    private static final ArrayList<AbstractValue<?>> internalRecord = new ArrayList<>();

    private ProfileCollection() {

    }

    public static ArrayList getProfileCollection() {
        return (ArrayList) internalRecord.clone();
    }

    public static void addEvent(int execution, int step, AbstractValue<?> value) {
        if (execution == 1) {
            internalRecord.add(value);
        }
        else {
            if (AbstractParser.speculationEnabled && step < internalRecord.size()) {
                AbstractValue<?> actualValue = internalRecord.get(step);
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
        internalRecord.set(step, newValue);
    }

    // TEST AND DEBUG METHODS //

    public static void resetProfileCollection() {
        ProfileCollection.internalRecord.clear();
    }
}
