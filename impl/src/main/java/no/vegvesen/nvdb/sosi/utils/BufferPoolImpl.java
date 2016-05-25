/*
 * Copyright (c) 2015-2016, Statens vegvesen
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
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
