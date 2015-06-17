// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.reader;

import no.vegvesen.nvdb.sosi.SosiLocation;
import no.vegvesen.nvdb.sosi.document.SosiNumber;
import no.vegvesen.nvdb.sosi.document.SosiSerialNumber;
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
     * Creates a SosiValue holding an integer number.
     * @param value the integer value
     * @return a SosiValue instance
     */
    public static SosiNumber number(int value) {
        return number(value, SosiLocation.unknown());
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
     * Creates a SosiValue holding a long integer number.
     * @param value the long integer value
     * @return a SosiValue instance
     */
    public static SosiNumber number(long value) {
        return number(value, SosiLocation.unknown());
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
     * Creates a SosiValue holding a double number.
     * @param value the double value
     * @return a SosiValue instance
     */
    public static SosiNumber number(double value) {
        return number(value, SosiLocation.unknown());
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

    /**
     * Creates a SosiValue holding a string.
     * @param value the string value
     * @return a SosiValue instance
     */
    public static SosiString string(String value) {
        return string(value, SosiLocation.unknown());
    }

    /**
     * Creates a SosiValue holding a serial number.
     * @param serialNo the serial number value
     * @param location the location of the value inside the SOSI file
     * @return a SosiValue instance
     */
    public static SosiSerialNumber serialNo(long serialNo, SosiLocation location) {
        return SosiSerialNumberImpl.of(serialNo, location);
    }

    /**
     * Creates a SosiValue holding a serial number.
     * @param serialNo the serial number value
     * @return a SosiValue instance
     */
    public static SosiSerialNumber serialNo(long serialNo) {
        return serialNo(serialNo, SosiLocation.unknown());
    }
}
