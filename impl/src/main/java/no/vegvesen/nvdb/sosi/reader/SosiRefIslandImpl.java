// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.reader;

import no.vegvesen.nvdb.sosi.SosiLocation;
import no.vegvesen.nvdb.sosi.document.SosiRefIsland;
import no.vegvesen.nvdb.sosi.document.SosiRefNumber;
import no.vegvesen.nvdb.sosi.document.SosiValue;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

/**
 * Implements a SOSI string value.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
class SosiRefIslandImpl implements SosiRefIsland {
    private final List<SosiRefNumber> refNumbers = new LinkedList<>();
    private final SosiLocation location;

    static SosiRefIsland of(SosiLocation location) {
        return new SosiRefIslandImpl(location);
    }

    SosiRefIslandImpl(SosiLocation location) {
        this.location = requireNonNull(location, "location can't be null");
    }

    void addRefNumber(SosiRefNumber refNumber) {
        refNumbers.add(refNumber);
    }

    @Override
    public Stream<SosiRefNumber> refNumbers() {
        return refNumbers.stream();
    }

    @Override
    public String getString() {
        return "(" + refNumbers.stream().map(SosiValue::getString).collect(joining(" ")) + ")";
    }

    @Override
    public ValueType getValueType() {
        return ValueType.REF_ISLAND;
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
        if (!(obj instanceof SosiRefIslandImpl)) {
            return false;
        }
        SosiRefIslandImpl other = (SosiRefIslandImpl)obj;
        return getString().equals(other.getString());
    }

    @Override
    public String toString() {
        return getString();
    }
}
