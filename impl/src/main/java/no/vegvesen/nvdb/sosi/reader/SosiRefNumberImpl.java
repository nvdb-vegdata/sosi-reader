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

import no.vegvesen.nvdb.sosi.document.SosiRefNumber;
import no.vegvesen.nvdb.sosi.SosiLocation;

import java.util.Objects;

/**
 * Implements a SOSI reference number.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
class SosiRefNumberImpl implements SosiRefNumber {
    private final long num;
    private final boolean insideIsland;
    private final boolean reversedOrder;
    private final SosiLocation location;

    static SosiRefNumber of(long num, boolean insideIsland, SosiLocation location) {
        return new SosiRefNumberImpl(num, insideIsland, location);
    }

    SosiRefNumberImpl(long num, boolean insideIsland, SosiLocation location) {
        this.num = Math.abs(num);
        this.reversedOrder = num < 0;
        this.insideIsland = insideIsland;
        this.location = location;
    }

    @Override
    public boolean isInsideIsland() {
        return insideIsland;
    }

    @Override
    public boolean isReversedOrder() {
        return reversedOrder;
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
        return ":" + (reversedOrder ? "-" : "") + Long.toString(num);
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
        return num == other.longValue() &&
               insideIsland == other.isInsideIsland() &&
               reversedOrder == other.isReversedOrder();
    }

    @Override
    public int hashCode() {
        return Objects.hash(num, insideIsland, reversedOrder);
    }
}
