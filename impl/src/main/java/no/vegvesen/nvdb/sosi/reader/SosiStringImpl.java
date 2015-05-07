// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.reader;

import no.vegvesen.nvdb.sosi.document.SosiString;
import no.vegvesen.nvdb.sosi.SosiLocation;

/**
 * Implements a SOSI string value.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
class SosiStringImpl implements SosiString {
    private final String value;
    private final SosiLocation location;

    static SosiString of(String value, SosiLocation location) {
        return new SosiStringImpl(value, location);
    }

    SosiStringImpl(String value, SosiLocation location) {
        this.value = value;
        this.location = location;
    }

    @Override
    public String getString() {
        return value;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.STRING;
    }

    @Override
    public SosiLocation getLocation() {
        return location;
    }

    @Override
    public int hashCode() {
        return getString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SosiStringImpl)) {
            return false;
        }
        SosiStringImpl other = (SosiStringImpl)obj;
        return getString().equals(other.getString());
    }

    @Override
    public String toString() {
        return getString();
    }
}
