package kr.sanchez.specdeser.core.jakarta.metadata;

public class MetadataPair<T,V> {

    private final String key;
    private MetadataState state;

    private T type;

    private V value;

    public MetadataPair(String key, MetadataState state, T type) {
        assert key != null;

        this.key = key;
        this.state = state;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public MetadataState getState() {
        return state;
    }

    public void nextState(T type, V value) { //TODO Finish this method
        if (state == MetadataState.CONSTANT_VALUE) {
            if (type != this.type) {
                this.state = MetadataState.ANY;
            }
        } else if (state == MetadataState.CONSTANT_TYPE) {

        }
    }
}
