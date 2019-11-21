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
package no.vegvesen.nvdb.sosi.document;

import no.vegvesen.nvdb.sosi.SosiLocation;

import static java.util.Objects.requireNonNull;

/**
 * An immutable SOSI value.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public interface SosiValue {
    enum ValueType {
        STRING, NUMBER, SERNO, REF, REF_ISLAND, UNSPECIFIED, DEFAULT, DATE
    }

    ValueType getValueType();

    SosiLocation getLocation();

    String getString();

    @Override
    String toString();

    static SosiValue DEFAULT(SosiLocation location) {
        requireNonNull(location, "location can't be null");
        return new SosiValue() {
            @Override
            public ValueType getValueType() {
                return ValueType.DEFAULT;
            }

            @Override
            public SosiLocation getLocation() {
                return location;
            }

            @Override
            public String getString() {
                return "@";
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof SosiValue) {
                    return getValueType().equals(((SosiValue) obj).getValueType());
                }
                return false;
            }

            @Override
            public int hashCode() {
                return ValueType.DEFAULT.hashCode();
            }

            @Override
            public String toString() {
                return getString();
            }
        };
    }

    static SosiValue UNSPECIFIED(SosiLocation location) {
        requireNonNull(location, "location can't be null");
        return new SosiValue() {
            @Override
            public ValueType getValueType() {
                return ValueType.UNSPECIFIED;
            }

            @Override
            public SosiLocation getLocation() {
                return location;
            }

            @Override
            public String getString() {
                return "*";
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof SosiValue) {
                    return getValueType().equals(((SosiValue) obj).getValueType());
                }
                return false;
            }

            @Override
            public int hashCode() {
                return ValueType.UNSPECIFIED.hashCode();
            }

            @Override
            public String toString() {
                return getString();
            }
        };
    }
}
