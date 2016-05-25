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
