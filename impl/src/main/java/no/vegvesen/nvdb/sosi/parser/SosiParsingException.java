// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.parser;

import no.vegvesen.nvdb.sosi.SosiLocation;
import no.vegvesen.nvdb.sosi.SosiException;

/**
 * {@code SosiParsingException} is used when an incorrect SOSI is being parsed.
 *
 * Based on the javax.json.stream.JsonParsingException class.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public class SosiParsingException extends SosiException {

    private final SosiLocation location;

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     * @param location the location of the incorrect SOSI
     */
    public SosiParsingException(String message, SosiLocation location) {
        super(message);
        this.location = location;
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method). (A <tt>null</tt> value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @param location the location of the incorrect SOSI
     */
    public SosiParsingException(String message, Throwable cause, SosiLocation location) {
        super(message, cause);
        this.location = location;
    }

    /**
     * Return the location of the incorrect SOSI.
     *
     * @return the non-null location of the incorrect SOSI
     */
    public SosiLocation getLocation() {
        return location;
    }
}
