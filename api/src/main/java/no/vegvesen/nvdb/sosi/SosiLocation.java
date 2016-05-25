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
    private final long columnNo;
    private final long lineNo;
    private final long offset;

    public static SosiLocation of(long lineNo) {
        return new SosiLocation(lineNo, -1, -1);
    }

    public static SosiLocation of(long lineNo, long columnNo, long streamOffset) {
        return new SosiLocation(lineNo, columnNo, streamOffset);
    }

    public static SosiLocation unknown() {
        return SosiLocation.of(-1, -1, -1);
    }

    private SosiLocation(long lineNo, long columnNo, long streamOffset) {
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
