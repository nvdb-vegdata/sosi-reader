// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.reader;

import no.vegvesen.nvdb.sosi.document.SosiRefNumber;
import no.vegvesen.nvdb.sosi.SosiLocation;

/**
 * Implements a SOSI reference number.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
class SosiRefNumberImpl implements SosiRefNumber {
    private final long num;
    private final boolean insideIsland;
    private final SosiLocation location;

    static SosiRefNumber of(long num, boolean island, SosiLocation location) {
        return new SosiRefNumberImpl(num, island, location);
    }

    SosiRefNumberImpl(long num, boolean insideIsland, SosiLocation location) {
        this.num = num;
        this.insideIsland = insideIsland;
        this.location = location;
    }

    @Override
    public boolean isInsideIsland() {
        return insideIsland;
    }
    @Override
    public long longValue() {
        return num;
    }

    @Override
    public long longValueExact() {
        return num;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.REF;
    }

    @Override
    public SosiLocation getLocation() {
        return location;
    }

    @Override
    public String getString() {
        return ":" + Long.toString(num);
    }

    @Override
    public String toString() {
        return getString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SosiRefNumber)) {
            return false;
        }
        SosiRefNumber other = (SosiRefNumber)obj;
        return longValue() == other.longValue();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(longValue());
    }
}
