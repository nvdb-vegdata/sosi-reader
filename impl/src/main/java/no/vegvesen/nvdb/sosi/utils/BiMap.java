// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements a two-way map.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public class BiMap<P, Q> {

    Map<P, Q> right = new HashMap<>();
    Map<Q, P> left = new HashMap<>();

    public void put(P left, Q right) {
        this.right.put(left, right);
        this.left.put(right, left);
    }

    public Q getRightOrDefault(P left, Q defaultValue) {
        return right.getOrDefault(left, defaultValue);
    }

    public P getLeftOrDefault(Q right, P defaultValue) {
        return left.getOrDefault(right, defaultValue);
    }

    public long size() { return right.size(); }

}
