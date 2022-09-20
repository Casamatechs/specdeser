package kr.sanchez.specdeser.core.jakarta;

public interface ByteBufferPool {

    byte[] take();

    void recycle(byte[] buffer);
}
