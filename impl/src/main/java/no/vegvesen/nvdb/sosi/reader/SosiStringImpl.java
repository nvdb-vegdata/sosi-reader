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

import no.vegvesen.nvdb.sosi.document.SosiString;
import no.vegvesen.nvdb.sosi.SosiLocation;

import static java.util.Objects.requireNonNull;

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
        this.value = requireNonNull(value, "value can't be null");
        this.location = requireNonNull(location, "location can't be null");
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
