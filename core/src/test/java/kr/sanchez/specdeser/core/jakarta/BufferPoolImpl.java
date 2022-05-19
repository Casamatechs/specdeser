package kr.sanchez.specdeser.core.jakarta;

import org.glassfish.json.api.BufferPool;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BufferPoolImpl implements BufferPool {

    // volatile since multiple threads may access queue reference
    private volatile WeakReference<ConcurrentLinkedQueue<char[]>> queue;

    /**
     * Gets a new object from the pool.
     *
     * <p>
     * If no object is available in the pool, this method creates a new one.
     *
     * @return
     *      always non-null.
     */
    @Override
    public final char[] take() {
        char[] t = getQueue().poll();
        if (t==null)
            return new char[4096];
        return t;
    }

    private ConcurrentLinkedQueue<char[]> getQueue() {
        WeakReference<ConcurrentLinkedQueue<char[]>> q = queue;
        if (q != null) {
            ConcurrentLinkedQueue<char[]> d = q.get();
            if (d != null)
                return d;
        }

        // overwrite the queue
        ConcurrentLinkedQueue<char[]> d = new ConcurrentLinkedQueue<>();
        queue = new WeakReference<>(d);

        return d;
    }

    /**
     * Returns an object back to the pool.
     */
    @Override
    public final void recycle(char[] t) {
        getQueue().offer(t);
    }

}
