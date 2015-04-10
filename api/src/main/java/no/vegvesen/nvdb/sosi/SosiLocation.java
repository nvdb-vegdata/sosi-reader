// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi;

/**
 * Provides the location information of a SOSI event in an input source. The
 * {@code SosiLocation} information can be used to identify incorrect SOSI
 * or can be used by higher frameworks to know about the processing location.
 *
 * Based on the javax.json.stream.JsonLocation interface.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public class SosiLocation {

    public static final SosiLocation UNKNOWN = new SosiLocation(-1, -1, -1);

    private final long columnNo;
    private final long lineNo;
    private final long offset;

    public SosiLocation(long lineNo, long columnNo, long streamOffset) {
        this.lineNo = lineNo;
        this.columnNo = columnNo;
        this.offset = streamOffset;
    }

    /**
     * Return the line number for the current SOSI event in the input source.
     *
     * @return the line number or -1 if none is available
     */
    public long getLineNumber() {
        return lineNo;
    }

    /**
     * Return the column number for the current SOSI event in the input source.
     *
     * @return the column number or -1 if none is available
     */
    public long getColumnNumber() {
        return columnNo;
    }

    /**
     * Return the stream offset into the input source this location
     * is pointing to. If the input source is a file or a byte stream then
     * this is the byte offset into that stream, but if the input source is
     * a character media then the offset is the character offset.
     * Returns -1 if there is no offset available.
     *
     * @return the offset of input source stream, or -1 if there is
     * no offset available
     */
    public long getStreamOffset() {
        return offset;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "(line no="+lineNo+", column no="+columnNo+", offset="+ offset +")";
    }
}
