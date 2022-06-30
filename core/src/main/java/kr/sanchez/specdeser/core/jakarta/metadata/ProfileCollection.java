package kr.sanchez.specdeser.core.jakarta.metadata;

import java.util.ArrayList;

public class ProfileCollection<T> {

    private static ProfileCollection profiler = null;

    private ArrayList<ArrayList<T>> internalRecord;

    private ProfileCollection() {
        this.internalRecord = new ArrayList<>();
    }

    public static ProfileCollection getInstance() {
        if (profiler == null) {
            return (profiler = new ProfileCollection());
        } else {
            return profiler;
        }
    }

    public ArrayList<ArrayList<T>> getAllInternalRecords() {
        return this.internalRecord;
    }

    public ArrayList<T> getInternalRecord(int index) {
        return this.internalRecord.get(index);
    }
}
