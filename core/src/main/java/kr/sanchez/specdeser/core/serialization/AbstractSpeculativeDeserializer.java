package kr.sanchez.specdeser.core.serialization;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

public abstract class AbstractSpeculativeDeserializer<T> implements SpeculativeDeserializer<T> {

    final Charset charset;

    /**
     * By default, the deserializer will make use of the UTF-8 encoding.
     */
    public AbstractSpeculativeDeserializer() {
        this.charset = StandardCharsets.UTF_8;
    }

    /**
     * Constructor to build a deserializer using a given encoding class
     * @param charset Encoding class
     */
    public AbstractSpeculativeDeserializer(Charset charset) {
        this.charset = charset;
    }

    /**
     *
     * @param charset
     * @throws UnsupportedCharsetException
     */
    public AbstractSpeculativeDeserializer(String charset) throws UnsupportedCharsetException{
        if (Charset.isSupported(charset)) {
            this.charset = Charset.forName(charset);
        } else {
            throw new UnsupportedCharsetException(charset);
        }
    }
}
