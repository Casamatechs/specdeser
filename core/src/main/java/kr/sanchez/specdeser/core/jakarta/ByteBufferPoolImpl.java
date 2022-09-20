package kr.sanchez.specdeser.core.jakarta;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ByteBufferPoolImpl implements ByteBufferPool{

    private volatile WeakReference<ConcurrentLinkedQueue<byte[]>> queue;

    @Override
    public byte[] take() {
        byte[] t = getQueue().poll();
        if (t==null)
            return new byte[AbstractParser.BUFFER_SIZE];
        return t;
    }

    private ConcurrentLinkedQueue<byte[]> getQueue() {
        WeakReference<ConcurrentLinkedQueue<byte[]>> q = queue;
        if (q != null) {
            ConcurrentLinkedQueue<byte[]> d = q.get();
            if (d != null)
                return d;
        }

        // overwrite the queue
        ConcurrentLinkedQueue<byte[]> d = new ConcurrentLinkedQueue<>();
        queue = new WeakReference<>(d);

        return d;
    }

    @Override
    public void recycle(byte[] buffer) {
        getQueue().offer(buffer);
    }
}
