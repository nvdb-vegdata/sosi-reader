// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.reader;

import no.vegvesen.nvdb.sosi.SosiLocation;
import no.vegvesen.nvdb.sosi.document.SosiNumber;
import no.vegvesen.nvdb.sosi.document.SosiString;

/**
 * Factory for SosiValue instances
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public class SosiValueFactory {
    /**
     * Creates a SosiValue holding an integer number.
     * @param value the integer value
     * @param location the location of the value inside the SOSI file
     * @return a SosiValue instance
     */
    public static SosiNumber number(int value, SosiLocation location) {
        return SosiNumberImpl.of(value, location);
    }

    /**
     * Creates a SosiValue holding a long integer number.
     * @param value the long integer value
     * @param location the location of the value inside the SOSI file
     * @return a SosiValue instance
     */
    public static SosiNumber number(long value, SosiLocation location) {
        return SosiNumberImpl.of(value, location);
    }

    /**
     * Creates a SosiValue holding a double number.
     * @param value the double value
     * @param location the location of the value inside the SOSI file
     * @return a SosiValue instance
     */
    public static SosiNumber number(double value, SosiLocation location) {
        return SosiNumberImpl.of(value, location);
    }

    /**
     * Creates a SosiValue holding a string.
     * @param value the string value
     * @param location the location of the value inside the SOSI file
     * @return a SosiValue instance
     */
    public static SosiString string(String value, SosiLocation location) {
        return SosiStringImpl.of(value, location);
    }
}
