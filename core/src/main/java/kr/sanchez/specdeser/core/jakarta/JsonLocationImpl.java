package kr.sanchez.specdeser.core.jakarta;

import javax.json.stream.JsonLocation;

public class JsonLocationImpl implements JsonLocation {

    private long lineNumber;
    private long columnNumber;

    public JsonLocationImpl(long lineNumber, long columnNumber) {
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }


    /**
     * Return the line number (starts with 1 for the first line) for the current JSON event in the input source.
     *
     * @return the line number (starts with 1 for the first line) or -1 if none is available
     */
    @Override
    public long getLineNumber() {
        return this.lineNumber;
    }

    /**
     * Return the column number (starts with 1 for the first column) for the current JSON event in the input source.
     *
     * @return the column number (starts with 1 for the first column) or -1 if none is available
     */
    @Override
    public long getColumnNumber() {
        return this.columnNumber;
    }

    /**
     * Return the stream offset into the input source this location
     * is pointing to. If the input source is a file or a byte stream then
     * this is the byte offset into that stream, but if the input source is
     * a character media then the offset is the character offset.
     * Returns -1 if there is no offset available.
     *
     * @return the offset of input source stream, or -1 if there is
     * no offset available
     */
    @Override
    public long getStreamOffset() {
        return -1;
    }
}
