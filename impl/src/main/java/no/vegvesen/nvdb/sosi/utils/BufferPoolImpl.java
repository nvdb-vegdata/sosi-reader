// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.utils;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * char[] pool that pool instances of char[] which are expensive to create.
 *
 * Based on a class from the Glassfish JSON parser (author Jitendra Kotamraju)
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
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
        if (isNull(t))
            return new char[4096];
        return t;
    }

    private ConcurrentLinkedQueue<char[]> getQueue() {
        WeakReference<ConcurrentLinkedQueue<char[]>> q = queue;
        if (nonNull(q)) {
            ConcurrentLinkedQueue<char[]> d = q.get();
            if (nonNull(d))
                return d;
        }

        // overwrite the queue
        ConcurrentLinkedQueue<char[]> d = new ConcurrentLinkedQueue<char[]>();
        queue = new WeakReference<ConcurrentLinkedQueue<char[]>>(d);

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
