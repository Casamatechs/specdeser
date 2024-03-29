package kr.sanchez.specdeser.core.jakarta;

import kr.sanchez.specdeser.core.jakarta.metadata.ProfileCollection;
import kr.sanchez.specdeser.core.jakarta.metadata.values.*;

import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;

public class ProfilingParser extends AbstractParser {

    private final FallbackParser delegate;

    private int metadataProfilingStep;
    private int eventProfilingStep;

    private Event currentEvent;

    public ProfilingParser(InputStream inputStream, ByteBufferPool bufferPool) {
        this.delegate = new FallbackParser(inputStream, bufferPool);
        this.metadataProfilingStep = 0;
        this.currentEvent = null;
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    @Override
    public Event next() {
        this.currentEvent = delegate.next();
        ProfileCollection.addParserEvent(invocations, eventProfilingStep++, this.currentEvent);
        // We have to do this here because there are not specific functions to get boolean values.
        if (this.currentEvent == JsonParser.Event.VALUE_TRUE) {
            ProfileCollection.addEvent(invocations, metadataProfilingStep++, eventProfilingStep-1,new MetadataValue(true, TRUE));
        } else if (this.currentEvent == JsonParser.Event.VALUE_FALSE) {
            ProfileCollection.addEvent(invocations, metadataProfilingStep++, eventProfilingStep-1,new MetadataValue(false, FALSE));
        } else if (this.currentEvent == JsonParser.Event.VALUE_NULL) {
            ProfileCollection.addEvent(invocations, metadataProfilingStep++, eventProfilingStep-1,new MetadataValue());
        }
        return this.currentEvent;
    }

    @Override
    public String getString() {
        byte[] byteVal = Arrays.copyOfRange(delegate.inputBuffer, delegate.beginValuePtr, delegate.endValuePtr);
        String val = delegate.getString();
        if (currentEvent == Event.KEY_NAME) {
            ProfileCollection.addEvent(invocations, metadataProfilingStep++, eventProfilingStep-1,new MetadataValue(ValueType.KEY, val, byteVal));
        } else {
            ProfileCollection.addEvent(invocations, metadataProfilingStep++, eventProfilingStep-1,new MetadataValue(ValueType.STRING_CONSTANT, val, byteVal));
        }
        return val;
    }

    @Override
    public boolean isIntegralNumber() {
        return delegate.isIntegralNumber();
    }

    @Override
    public int getInt() {
        byte[] byteVal = Arrays.copyOfRange(delegate.inputBuffer, delegate.beginValuePtr, delegate.endValuePtr);
        int val = delegate.getInt();
        ProfileCollection.addEvent(invocations, metadataProfilingStep++, eventProfilingStep-1,new MetadataValue(val, byteVal));
        return val;
    }

    @Override
    public long getLong() {
        byte[] byteVal = Arrays.copyOfRange(delegate.inputBuffer, delegate.beginValuePtr, delegate.endValuePtr);
        long val = delegate.getLong();
        ProfileCollection.addEvent(invocations, metadataProfilingStep++, eventProfilingStep-1,new MetadataValue((int) val, byteVal));
        return val;
    }

    @Override
    public BigDecimal getBigDecimal() {
        return delegate.getBigDecimal();
    }

    @Override
    public JsonLocation getLocation() {
        return delegate.getLocation();
    }

    @Override
    public void close() {
        delegate.close();
    }
}
