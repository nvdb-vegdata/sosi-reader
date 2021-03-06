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
import no.vegvesen.nvdb.sosi.document.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
     * Creates a SosiValue holding a date.
     * @param value the datetime value
     * @param location the location of the value inside the SOSI file
     * @return a SosiValue instance
     */
    public static SosiDate date(LocalDate value, SosiLocation location) {return SosiDateImpl.of(value, location);}

    /**
     * Creates a SosiValue holding a date.
     * @param value the datetime value
     * @return a SosiValue instance
     */
    public static SosiDate date(LocalDate value) { return date(value, SosiLocation.unknown());}

    /**
     * Creates a SosiValue holding a datetime.
     * @param value the datetime value
     * @param location the location of the value inside the SOSI file
     * @return a SosiValue instance
     */
    public static SosiDateTime datetime(LocalDateTime value, SosiLocation location) {return SosiDateTimeImpl.of(value, location);}

    /**
     * Creates a SosiValue holding a datetime.
     * @param value the datetime value
     * @return a SosiValue instance
     */
    public static SosiDateTime datetime(LocalDateTime value) { return datetime(value, SosiLocation.unknown());}

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

    /**
     * Creates a SosiValue holding a reference number.
     * @param refNo the reference number value
     * @param location the location of the value inside the SOSI file
     * @return a SosiValue instance
     */
    public static SosiRefNumber refNo(long refNo, SosiLocation location) {
        return SosiRefNumberImpl.of(refNo, false, location);
    }

    /**
     * Creates a SosiValue holding a reference number.
     * @param refNo the reference number value
     * @return a SosiValue instance
     */
    public static SosiRefNumber refNo(long refNo) {
        return refNo(refNo, SosiLocation.unknown());
    }

    /**
     * Creates a SosiValue holding a reference island containing a single reference number.
     * @param refNo the reference island value
     * @param location the location of the value inside the SOSI file
     * @return a SosiValue instance
     */
    public static SosiRefIsland refIsland(long refNo, SosiLocation location) {
        SosiRefIslandImpl value = new SosiRefIslandImpl(location);
        value.addRefNumber(SosiRefNumberImpl.of(refNo, true, location));
        return value;
    }

    /**
     * Creates a SosiValue holding a reference island containing a single reference number.
     * @param refNo the reference island value
     * @return a SosiValue instance
     */
    public static SosiRefIsland refIsland(long refNo) {
        return refIsland(refNo, SosiLocation.unknown());
    }
}
