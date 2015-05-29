// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
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
        STRING, NUMBER, SERNO, REF, UNSPECIFIED, DEFAULT
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
