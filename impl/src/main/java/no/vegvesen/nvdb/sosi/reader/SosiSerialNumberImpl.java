// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.reader;

import no.vegvesen.nvdb.sosi.document.SosiSerialNumber;
import no.vegvesen.nvdb.sosi.SosiLocation;

/**
 * Implements a SOSI serial number.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
class SosiSerialNumberImpl implements SosiSerialNumber {
    private static SosiLocation location;
    private final long num;

    static SosiSerialNumber of(long num, SosiLocation location) {
        return new SosiSerialNumberImpl(num, location);
    }

    SosiSerialNumberImpl(long num, SosiLocation location) {
        this.num = num;
        this.location = location;
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
        return ValueType.SERNO;
    }

    @Override
    public SosiLocation getLocation() {
        return location;
    }

    @Override
    public String getString() {
        return Long.toString(num);
    }

    @Override
    public String toString() {
        return getString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SosiSerialNumber)) {
            return false;
        }
        SosiSerialNumber other = (SosiSerialNumber)obj;
        return longValue() == other.longValue();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(longValue());
    }
}
