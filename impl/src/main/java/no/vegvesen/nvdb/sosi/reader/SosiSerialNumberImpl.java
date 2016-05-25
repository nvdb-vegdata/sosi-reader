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

import no.vegvesen.nvdb.sosi.document.SosiSerialNumber;
import no.vegvesen.nvdb.sosi.SosiLocation;

/**
 * Implements a SOSI serial number.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
class SosiSerialNumberImpl implements SosiSerialNumber {
    private final SosiLocation location;
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
        return Long.toString(num) + ":";
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
