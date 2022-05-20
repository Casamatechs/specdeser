package kr.sanchez.specdeser.core.jakarta;

import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParsingException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@SuppressWarnings("JavadocReference")
public class IntrinsicJakartaParser implements JsonParser {

    private final static int BUFFER_SIZE = 65536; // TODO

    private Event currentEvent;
    private InputStream inputStream;
    private byte[] inputBuffer = new byte[BUFFER_SIZE];
//    private final byte[] auxBuffer = new byte[BUFFER_SIZE]; // TODO: This buffer will be used to store values when reading the stream again.
    private final ContextStack<ContextParser> contextStack;
    private int ptrBuff;
    private int endBuff;

    private int beginValuePtr;
    private int endValuePtr;

    private long col;
    private long line;

    private NumberType numType;

    private enum NumberType {
        INT,
        DOUBLE,
        EXP
    }

    public IntrinsicJakartaParser(byte[] inputBuffer) {
        this.inputStream = null;
        this.ptrBuff = 0;
        this.endBuff = inputBuffer.length;
        this.inputBuffer = inputBuffer;
        this.col = 1L;
        this.line = 1L;
        this.contextStack = new ContextStack<>();
    }

    public IntrinsicJakartaParser(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        this.ptrBuff = 0;
        this.endBuff = 0;
        this.col = 1L;
        this.line = 1L;
        this.contextStack = new ContextStack<>();
        this.loadIntoBuffer();
    }

    public boolean stackEmpty() {
        return this.contextStack.isEmpty();
    }

    /**
     * Returns {@code true} if there are more parsing states. This method returns
     * {@code false} if the parser reaches the end of the JSON text.
     *
     * @return {@code true} if there are more parsing states.
     * @throws JsonException        if an i/o error occurs (IOException
     *                              would be cause of JsonException)
     * @throws JsonParsingException if the parser encounters invalid JSON
     *                              when advancing to next state.
     */
    @Override
    public boolean hasNext() {
        if (this.ptrBuff < this.endBuff) {
            skipWS();
            return this.ptrBuff != this.endBuff - 1 || !checkWS(this.inputBuffer[this.ptrBuff]); // This means the last char is a WS
        }
        if (this.inputStream != null) {
            try {
                return loadIntoBuffer();
            } catch (IOException e) {
                throwParseException();
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Returns the event for the next parsing state.
     *
     * @return the event for the next parsing state
     * @throws JsonException          if an i/o error occurs (IOException
     *                                would be cause of JsonException)
     * @throws JsonParsingException   if the parser encounters invalid JSON
     *                                when advancing to next state.
     * @throws NoSuchElementException if there are no more parsing
     *                                states.
     */
    @Override
    public Event next() {
        if (this.inputBuffer[this.ptrBuff] == '{') {
            this.contextStack.push(ContextParser.OBJECT_CONTEXT);
            this.ptrBuff++;
            skipWS();
            return (this.currentEvent = Event.START_OBJECT);
        }
        if (this.inputBuffer[this.ptrBuff] == '}') {
            if (this.contextStack.peek() == ContextParser.OBJECT_CONTEXT) {
                this.contextStack.pop();
                this.ptrBuff++;
                if (this.ptrBuff < this.endBuff) {
                    checkLastValueContext();
                }
                skipWS();
                return (this.currentEvent = Event.END_OBJECT);
            }
            throwParseException();
        }
        if (this.inputBuffer[this.ptrBuff] == '[') {
            this.contextStack.push(ContextParser.ARRAY_CONTEXT);
            this.ptrBuff++;
            skipWS();
            return (this.currentEvent = Event.START_ARRAY);
        }
        if (this.inputBuffer[this.ptrBuff] == ']') {
            if (this.contextStack.peek() == ContextParser.ARRAY_CONTEXT) {
                this.contextStack.pop();
                this.ptrBuff++;
                if (this.ptrBuff < this.endBuff) {
                    checkLastValueContext();
                }
                skipWS();
                return (this.currentEvent = Event.END_ARRAY);
            }
            throwParseException();
        }
        if (this.inputBuffer[this.ptrBuff] == ':') {
            if (this.currentEvent == Event.KEY_NAME) {
                this.contextStack.push(ContextParser.VALUE_CONTEXT);
                this.ptrBuff++;
                skipWS();
                return next();
            }
        }
        if (this.inputBuffer[this.ptrBuff] == ',') {
            if (this.contextStack.peek() == ContextParser.VALUE_CONTEXT && isValueEvent()) {
                this.contextStack.pop();
                this.ptrBuff++;
                this.currentEvent = null;
                skipWS();
                return next(); // Current event set to null
            }
            if (this.contextStack.peek() == ContextParser.ARRAY_CONTEXT && isValueEvent()) {
                this.ptrBuff++;
                skipWS();
                return next();
            }
            throwParseException();
        }
        switch (this.inputBuffer[this.ptrBuff]) {
            case '0','1','2','3','4','5','6','7','8','9' -> {
                this.beginValuePtr = this.ptrBuff;
                processNumber();
                this.currentEvent = Event.VALUE_NUMBER;
            }
            case '-' -> {
                this.beginValuePtr = this.ptrBuff;
                processNegativeNumber();
                this.currentEvent = Event.VALUE_NUMBER;
            }
            case 't' -> {
                processTrue();
                this.ptrBuff++; // We want to set the pointer in the next char after the read value.
                this.currentEvent = Event.VALUE_TRUE;
            }
            case 'f' -> {
                processFalse();
                this.ptrBuff++;
                this.currentEvent = Event.VALUE_FALSE;
            }
            case 'n' -> {
                processNull();
                this.ptrBuff++;
                this.currentEvent = Event.VALUE_NULL;
            }
            case '"' -> {
                this.ptrBuff++; // We want to skip the " character as is only a delimiter
                processString();
                if (this.contextStack.peek() == ContextParser.OBJECT_CONTEXT) {
                    this.currentEvent = Event.KEY_NAME;
                } else {
                    this.currentEvent = Event.VALUE_STRING;
                }
            }
            default -> {
                skipWS();
                if (this.ptrBuff < this.endBuff-1) throwParseException();
            }
        }
        skipWS();
        if (this.contextStack.peek() == ContextParser.VALUE_CONTEXT && this.inputBuffer[this.ptrBuff] != ',') {
            this.contextStack.pop();
        }
        return this.currentEvent;
    }

    /**
     * Returns a {@code String} for the name in a name/value pair,
     * for a string value or a number value. This method should only be called
     * when the parser state is {@link Event#KEY_NAME}, {@link Event#VALUE_STRING},
     * or {@link Event#VALUE_NUMBER}.
     *
     * @return a name when the parser state is {@link Event#KEY_NAME}
     * a string value when the parser state is {@link Event#VALUE_STRING}
     * a number value when the parser state is {@link Event#VALUE_NUMBER}
     * @throws IllegalStateException when the parser state is not
     *                               {@code KEY_NAME}, {@code VALUE_STRING}, or {@code VALUE_NUMBER}
     */
    @Override
    public String getString() {
        if (this.currentEvent != Event.KEY_NAME && this.currentEvent != Event.VALUE_STRING && this.currentEvent != Event.VALUE_NUMBER) {
            throw new IllegalStateException();
        }
        return new String(this.inputBuffer, this.beginValuePtr, this.endValuePtr - this.beginValuePtr);
    }

    /**
     * Returns true if the JSON number at the current parser state is a
     * integral number. A {@link BigDecimal} may be used to store the value
     * internally and this method semantics are defined using its
     * {@code scale()}. If the scale is zero, then it is considered integral
     * type. This integral type information can be used to invoke an
     * appropriate accessor method to obtain a numeric value as in the
     * following example:
     *
     * <pre>
     * <code>
     * JsonParser parser = ...
     * if (parser.isIntegralNumber()) {
     *     parser.getInt();     // or other methods to get integral value
     * } else {
     *     parser.getBigDecimal();
     * }
     * </code>
     * </pre>
     *
     * @return true if this number is a integral number, otherwise false
     * @throws IllegalStateException when the parser state is not
     *                               {@code VALUE_NUMBER}
     */
    @Override
    public boolean isIntegralNumber() {
        return this.numType == NumberType.INT;
    }

    /**
     * Returns a JSON number as an integer. The returned value is equal
     * to {@code new BigDecimal(getString()).intValue()}. Note that
     * this conversion can lose information about the overall magnitude
     * and precision of the number value as well as return a result with
     * the opposite sign. This method should only be called when the parser
     * state is {@link Event#VALUE_NUMBER}.
     *
     * @return an integer for a JSON number
     * @throws IllegalStateException when the parser state is not
     *                               {@code VALUE_NUMBER}
     * @see BigDecimal#intValue()
     */
    @Override
    public int getInt() {
        if (this.currentEvent != Event.VALUE_NUMBER) {
            throw new IllegalStateException();
        }
        return new BigDecimal(this.getString()).intValue();
    }

    /**
     * Returns a JSON number as a long. The returned value is equal
     * to {@code new BigDecimal(getString()).longValue()}. Note that this
     * conversion can lose information about the overall magnitude and
     * precision of the number value as well as return a result with
     * the opposite sign. This method is only called when the parser state is
     * {@link Event#VALUE_NUMBER}.
     *
     * @return a long for a JSON number
     * @throws IllegalStateException when the parser state is not
     *                               {@code VALUE_NUMBER}
     * @see BigDecimal#longValue()
     */
    @Override
    public long getLong() {
        if (this.currentEvent != Event.VALUE_NUMBER) {
            throw new IllegalStateException();
        }
        return new BigDecimal(this.getString()).longValue();
    }

    /**
     * Returns a JSON number as a {@code BigDecimal}. The {@code BigDecimal}
     * is created using {@code new BigDecimal(getString())}. This
     * method should only called when the parser state is
     * {@link Event#VALUE_NUMBER}.
     *
     * @return a {@code BigDecimal} for a JSON number
     * @throws IllegalStateException when the parser state is not
     *                               {@code VALUE_NUMBER}
     */
    @Override
    public BigDecimal getBigDecimal() {
        if (this.currentEvent != Event.VALUE_NUMBER) {
            throw new IllegalStateException();
        }
        return new BigDecimal(this.getString());
    }

    public double getDouble() {
        if (this.currentEvent != Event.VALUE_NUMBER) {
            throw new IllegalStateException();
        }
        return new BigDecimal(this.getString()).doubleValue();
    }

    /**
     * Return the location that corresponds to the parser's current state in
     * the JSON input source. The location information is only valid in the
     * current parser state (or until the parser is advanced to a next state).
     *
     * @return a non-null location corresponding to the current parser state
     * in JSON input source
     */
    @Override
    public JsonLocation getLocation() {
        return new JsonLocationImpl(this.line, this.col);
    }

    /**
     * Returns a {@code JsonObject} and advances the parser to the
     * corresponding {@code END_OBJECT}.
     *
     * @return the {@code JsonObject} at the current parser position
     * @throws IllegalStateException when the parser state is not
     *                               {@code START_OBJECT}
     * @since 1.1
     */
    @Override
    public JsonObject getObject() {
        return JsonParser.super.getObject();
    }

    /**
     * Returns a {@code JsonValue} at the current parser position.
     * If the parser state is {@code START_ARRAY}, the behavior is
     * the same as {@link #getArray}. If the parser state is
     * {@code START_OBJECT}, the behavior is the same as
     * {@link #getObject}. For all other cases, if applicable, the JSON value is
     * read and returned.
     *
     * @return the {@code JsonValue} at the current parser position.
     * @throws IllegalStateException when the parser state is
     *                               {@code END_OBJECT} or {@code END_ARRAY}
     * @since 1.1
     */
    @Override
    public JsonValue getValue() {
        return JsonParser.super.getValue();
    }

    /**
     * Returns a {@code JsonArray} and advance the parser to the
     * the corresponding {@code END_ARRAY}.
     *
     * @return the {@code JsonArray} at the current parser position
     * @throws IllegalStateException when the parser state is not
     *                               {@code START_ARRAY}
     * @since 1.1
     */
    @Override
    public JsonArray getArray() {
        return JsonParser.super.getArray();
    }

    /**
     * Returns a stream of the {@code JsonArray} elements.
     * The parser state must be {@code START_ARRAY}.
     * The elements are read lazily, on an as-needed basis, as
     * required by the stream operations.
     * If the stream operations do not consume
     * all of the array elements, {@link skipArray} can be used to
     * skip the unprocessed array elements.
     *
     * @return a stream of elements of the {@code JsonArray}
     * @throws IllegalStateException when the parser state is not
     *                               {@code START_ARRAY}
     * @since 1.1
     */
    @Override
    public Stream<JsonValue> getArrayStream() {
        return JsonParser.super.getArrayStream();
    }

    /**
     * Returns a stream of the {@code JsonObject}'s
     * name/value pairs. The parser state must be {@code START_OBJECT}.
     * The name/value pairs are read lazily, on an as-needed basis, as
     * required by the stream operations.
     * If the stream operations do not consume
     * all of the object's name/value pairs, {@link skipObject} can be
     * used to skip the unprocessed elements.
     *
     * @return a stream of name/value pairs of the {@code JsonObject}
     * @throws IllegalStateException when the parser state is not
     *                               {@code START_OBJECT}
     * @since 1.1
     */
    @Override
    public Stream<Map.Entry<String, JsonValue>> getObjectStream() {
        return JsonParser.super.getObjectStream();
    }

    /**
     * Returns a stream of {@code JsonValue} from a sequence of
     * JSON values. The values are read lazily, on an as-needed basis,
     * as needed by the stream operations.
     *
     * @return a Stream of {@code JsonValue}
     * @throws IllegalStateException if the parser is in an array or object.
     * @since 1.1
     */
    @Override
    public Stream<JsonValue> getValueStream() {
        return JsonParser.super.getValueStream();
    }

    /**
     * Advance the parser to {@code END_ARRAY}.
     * If the parser is in array context, i.e. it has previously
     * encountered a {@code START_ARRAY} without encountering the
     * corresponding {@code END_ARRAY}, the parser is advanced to
     * the corresponding {@code END_ARRAY}.
     * If the parser is not in any array context, nothing happens.
     *
     * @since 1.1
     */
    @Override
    public void skipArray() {
        JsonParser.super.skipArray();
    }

    /**
     * Advance the parser to {@code END_OBJECT}.
     * If the parser is in object context, i.e. it has previously
     * encountered a {@code START_OBJECT} without encountering the
     * corresponding {@code END_OBJECT}, the parser is advanced to
     * the corresponding {@code END_OBJECT}.
     * If the parser is not in any object context, nothing happens.
     *
     * @since 1.1
     */
    @Override
    public void skipObject() {
        JsonParser.super.skipObject();
    }

    /**
     * Closes this parser and frees any resources associated with the
     * parser. This method closes the underlying input source.
     *
     * @throws JsonException if an i/o error occurs (IOException
     *                       would be cause of JsonException)
     */
    @Override
    public void close() {
        try {
            if (this.inputStream != null) {
                this.inputStream.close();
            }
        } catch (IOException e) {
            throw new JsonException(e.getMessage());
        }
    }

    private boolean loadIntoBuffer() throws IOException {
        if (this.inputStream != null) {
            int readBytes = this.inputStream.read(inputBuffer, 0, BUFFER_SIZE);
            if (readBytes > 0) {
                this.endBuff = readBytes;
                this.ptrBuff = 0;
                return true;
            }
            this.inputStream.close();
            this.inputStream = null;
            return false;
        }
        return false;
    }

    private void throwParseException() {
        throw new JsonParsingException("Parsing error", new JsonLocationImpl(this.line, this.col));
    }

    private boolean isValueEvent() {
        return switch (this.currentEvent) {
            case VALUE_FALSE, VALUE_NULL, VALUE_NUMBER, VALUE_TRUE, VALUE_STRING, END_ARRAY, END_OBJECT -> true;
            default -> false;
        };
    }

    private void skipWS() {
        if (this.inputStream == null && this.ptrBuff >= this.endBuff-1) return;
        if (this.inputBuffer[this.ptrBuff] == 0x20 ||
            this.inputBuffer[this.ptrBuff] == 0x09 ||
            this.inputBuffer[this.ptrBuff] == 0x0A ||
            this.inputBuffer[this.ptrBuff] == 0x0D) {
            this.ptrBuff++;
            if (this.inputStream != null && this.ptrBuff == this.endBuff) {
                this.ptrBuff = 0;
                try {
                    loadIntoBuffer();
                } catch (IOException e) {
                    throwParseException();
                }
            }
            skipWS();
        }
    }

    private boolean checkWS(byte b) {
        return b == 0x20 ||
                b == 0x09 ||
                b == 0x0A ||
                b == 0x0D;
    }

    private void processTrue() {
        if (this.inputBuffer[++this.ptrBuff] != 'r' ||
            this.inputBuffer[++this.ptrBuff] != 'u' ||
            this.inputBuffer[++this.ptrBuff] != 'e') {
            throwParseException();
        }
    }

    private void processFalse() {
        if (this.inputBuffer[++this.ptrBuff] != 'a' ||
            this.inputBuffer[++this.ptrBuff] != 'l' ||
            this.inputBuffer[++this.ptrBuff] != 's' ||
            this.inputBuffer[++this.ptrBuff] != 'e') {
            throwParseException();
        }
    }

    private void processNull() {
        if (this.inputBuffer[++this.ptrBuff] != 'u' ||
            this.inputBuffer[++this.ptrBuff] != 'l' ||
            this.inputBuffer[++this.ptrBuff] != 'l') {
            throwParseException();
        }
    }

    private void processNumber() {
        while (fastNaturalNumberCheck()) {
            this.ptrBuff += 8;
        }
        while (slowNumberCheck());
    }

    private void processNegativeNumber() { // TODO: Replace this function with a more specific one
        this.beginValuePtr = this.ptrBuff++;
        processNumber();
    }

    private void processString() {
        boolean readEscaped = false;
        this.beginValuePtr = this.ptrBuff;
        this.endValuePtr = this.beginValuePtr;
        while (this.ptrBuff < this.inputBuffer.length && !readEscaped) {
            byte ch = this.inputBuffer[this.ptrBuff++];
            if (ch == '"') {
                break;
            }
            if (isCharUnscaped(ch)){
                this.endValuePtr++;
            }
            else if (ch == 0x5c) {
                readEscaped = true;
                processEscapedChar();
                continueEscapedProcess();
            }
            else if (ch < 0){ // Multiple byte UTF-8 encoding. TODO: Check every byte is correct (must be done with the intrinsic)
                ch = (byte) ((ch & 0xFF) >>> 3);
                if (ch >= 24 && ch < 28) processNBytes(2);
                else if (ch < 30) processNBytes(3);
                else if (ch == 30) processNBytes(4);
                else throwParseException();
            }
        }
    }

    private void processNBytes(int n) {
        this.ptrBuff += n-1;
        this.endValuePtr += n;
    }

    private void processNBytesEscaped(int n) {
        this.ptrBuff--;
        for (int i = 0; i < n; i++) {
            this.inputBuffer[this.endValuePtr++] = this.inputBuffer[this.ptrBuff++];
        }
    }

    private void continueEscapedProcess() {
        while (this.ptrBuff < this.inputBuffer.length) {
            byte ch = this.inputBuffer[this.ptrBuff++];
            if (ch == '"') {
                return;
            }
            if (isCharUnscaped(ch)){
                this.inputBuffer[this.endValuePtr++] = ch;
            }
            else if (ch == 0x5c) {
                processEscapedChar();
            } else if (ch < 0){ // Multiple byte UTF-8 encoding. TODO: Check every byte is correct (must be done with the intrinsic)
                ch = (byte) ((ch & 0xFF) >>> 3);
                if (ch >= 24 && ch < 28) processNBytesEscaped(2);
                else if (ch < 30) processNBytesEscaped(3);
                else if (ch == 30) processNBytesEscaped(4);
                else throwParseException();
            }
            else System.out.println("Unsupported byte");
        }
    }

    private void processEscapedChar() {
        byte ch2 = this.inputBuffer[this.ptrBuff++];
        switch(ch2) {
            case 'b' -> this.inputBuffer[this.endValuePtr++] = '\b';
            case 't' -> this.inputBuffer[this.endValuePtr++] = '\t';
            case 'n' -> this.inputBuffer[this.endValuePtr++] = '\n';
            case 'f' -> this.inputBuffer[this.endValuePtr++] = '\f';
            case 'r' -> this.inputBuffer[this.endValuePtr++] = '\r';
            case '"', '\\', '/' -> this.inputBuffer[this.endValuePtr++] = ch2;
            case 'u' -> {
//                this.inputBuffer[this.endValuePtr++] = ch2;
                processExtendedChar();
            }
            default -> {
                System.out.println("Unsupported byte");
                throwParseException();
            }
        }
    }

    private boolean isCharUnscaped(byte ch) {
        return ch >= 0x20 && ch != 0x22 && ch != 0x5c;
    }

    private void processExtendedChar() { // TODO: Check how to do this correctly, not relying on expensive methods.
        char c1 = buildUnicodeChar();
        if (this.inputBuffer[this.ptrBuff] != 0x5c || this.inputBuffer[this.ptrBuff+1] != 'u') { // We dont want to move the pointer in case there's not a second unicode character
            String str = String.valueOf(c1);
            byte[] st = str.getBytes(StandardCharsets.UTF_8);
            for (byte b : st) {
                this.inputBuffer[this.endValuePtr++] = b;
            }
        } else {
            this.ptrBuff += 2; // We skip the escape and unicode indicator
            char c2 = buildUnicodeChar();
            String str = new String(new char[]{c1,c2});
            byte[] st = str.getBytes(StandardCharsets.UTF_8);
            for (byte b : st) {
                this.inputBuffer[this.endValuePtr++] = b;
            }
        }
    }

    private char buildUnicodeChar() {
        int c = 0;
        for (int i = 0; i < 4; i++) {
            byte ch = this.inputBuffer[this.ptrBuff++];
            if (ch >= 0x30 && ch <= 0x39) {
                c = c * 0x10 + (ch - 0x30);
            } else if (ch >= 0x41 && ch <= 0x46) {
                c = c * 0x10 + (ch - 0x37);
            } else if (ch >= 0x61 && ch <= 0x66) {
                c = c * 0x10 + (ch - 0x57);
            } else {
                throwParseException();
            }
        }
        return (char) c;
    }

    /**
     *
     * @return
     */
    private boolean fastNaturalNumberCheck() { // TODO: This method should be replaced by an intrinsic
        for (int i = 0; i < 8; i++) {
            int number = this.inputBuffer[this.ptrBuff+i] - '0';
            if (number < 0 || number > 9) return false;
        }
        return true;
//        try {
//            long val = ByteBuffer.wrap(this.inputBuffer, this.ptrBuff, 8).getLong();
//            return (((val & 0xF0F0F0F0F0F0F0F0L) |
//                    (((val + 0x0606060606060606L) & 0xF0F0F0F0F0F0F0F0L) >> 4)) ==
//                    0x3333333333333333L);
//        } catch (IndexOutOfBoundsException e) {
//            return false;
//        }
    }

    private boolean slowNumberCheck() {
        if (this.ptrBuff == this.endBuff) {
            this.endValuePtr = this.ptrBuff;
            return false; // EOF
        }
        byte ch = this.inputBuffer[this.ptrBuff];
        if (((ch - '0') & 0xFF) <= 9) {
            this.ptrBuff++;
            return true;
        } if (ch == '.') {
            this.numType = NumberType.DOUBLE;
            this.ptrBuff++;
            processNumber2();
            this.endValuePtr = this.ptrBuff;
            return false; // The invocation to processFloatingNumber will take care of iterating until the end of the number.
        } if (ch == 'e' || ch == 'E') {
            this.numType = NumberType.EXP;
            this.ptrBuff++;
            processNumber2();
            this.endValuePtr = this.ptrBuff;
            return false;
        }
        // We only reach this point if the char is not a valid number candidate.
        if (ch == ',' || checkWS(ch)) {
            this.numType = NumberType.INT;
            this.endValuePtr = this.ptrBuff++;
        } else if (ch == '}' || ch == ']') {
            this.numType = NumberType.INT;
            this.endValuePtr = this.ptrBuff;
        }
        else{
            throwParseException();
        }
        return false;
    }

    private void processNumber2() {
        while (((this.inputBuffer[this.ptrBuff] - '0') & 0xFF) <= 9) {
            if (++this.ptrBuff == this.endBuff) {
                return;
            }
        }
    }

    private void checkLastValueContext() {
        skipWS();
        if (this.inputBuffer[this.ptrBuff] != ',' && this.contextStack.peek() == ContextParser.VALUE_CONTEXT) {
            this.contextStack.pop();
        }
    }

}
