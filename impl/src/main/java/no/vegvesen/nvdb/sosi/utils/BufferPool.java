// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.utils;

/**
 * char[] pool that pool instances of char[] which are expensive to create.
 *
 * Based on an interface from the Glassfish JSON parser (author Jitendra Kotamraju)
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public interface BufferPool {

    /**
     * Gets a new char[] object from the pool.
     *
     * <p>
     * If no object is available in the pool, this method creates a new one.
     *
     * @return
     *      always non-null.
     */
    char[] take();

    /**
     * Returns an object back to the pool.
     */
    void recycle(char[] buf);
}
