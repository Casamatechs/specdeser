package kr.sanchez.specdeser.core.jakarta;

import kr.sanchez.specdeser.core.jakarta.metadata.ProfileCollection;
import kr.sanchez.specdeser.core.jakarta.metadata.values.IntegerConstant;
import kr.sanchez.specdeser.core.jakarta.metadata.values.KeyValue;
import kr.sanchez.specdeser.core.jakarta.metadata.values.LongConstant;
import kr.sanchez.specdeser.core.jakarta.metadata.values.StringConstant;

import javax.json.stream.JsonLocation;
import java.io.InputStream;
import java.math.BigDecimal;

public class ProfilingParser extends AbstractParser {

    private final FallbackParser delegate;

    private int metadataProfilingStep;
    private int eventProfilingStep;

    private Event currentEvent;

    public ProfilingParser(InputStream inputStream) {
        this.delegate = new FallbackParser(inputStream);
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
        return this.currentEvent;
    }

    @Override
    public String getString() {
        String val = delegate.getString();
        if (currentEvent == Event.KEY_NAME) {
            ProfileCollection.addEvent(invocations, metadataProfilingStep++, new KeyValue(val));
        } else {
            ProfileCollection.addEvent(invocations, metadataProfilingStep++, new StringConstant(val));
        }
        return val;
    }

    @Override
    public boolean isIntegralNumber() {
        return delegate.isIntegralNumber();
    }

    @Override
    public int getInt() {
        int val = delegate.getInt();
        ProfileCollection.addEvent(invocations, metadataProfilingStep++, new IntegerConstant(val));
        return val;
    }

    @Override
    public long getLong() {
        long val = delegate.getLong();
        ProfileCollection.addEvent(invocations, metadataProfilingStep++, new LongConstant(val));
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
