package kr.sanchez.specdeser.core.jakarta;

import javax.json.stream.JsonLocation;
import java.math.BigDecimal;

public class SpeculativeParser extends AbstractParser{

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Event next() {
        return null;
    }

    @Override
    public String getString() {
        return null;
    }

    @Override
    public boolean isIntegralNumber() {
        return false;
    }

    @Override
    public int getInt() {
        return 0;
    }

    @Override
    public long getLong() {
        return 0;
    }

    @Override
    public BigDecimal getBigDecimal() {
        return null;
    }

    @Override
    public JsonLocation getLocation() {
        return null;
    }

    @Override
    public void close() {

    }
}
