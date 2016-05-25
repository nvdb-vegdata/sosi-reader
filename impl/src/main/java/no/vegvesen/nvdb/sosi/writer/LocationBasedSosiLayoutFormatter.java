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
package no.vegvesen.nvdb.sosi.writer;

import no.vegvesen.nvdb.sosi.document.SosiElement;
import no.vegvesen.nvdb.sosi.document.SosiValue;


/**
 * Implements a layout formatter that preserves formatting of a previously parsed SOSI file
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public class LocationBasedSosiLayoutFormatter implements SosiLayoutFormatter {
    private final LineEnding lineEnding;
    private long lineNo = 1;
    private long elementNo = 0;

    public LocationBasedSosiLayoutFormatter() {
        this.lineEnding = LineEnding.WINDOWS;
    }

    public LocationBasedSosiLayoutFormatter(LineEnding lineEnding) {
        this.lineEnding = lineEnding;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String beforeValue(SosiValue value) {
        StringBuilder sb = new StringBuilder();
        if (!advanceToLine(sb, value.getLocation().getLineNumber())) {
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String beforeElement(SosiElement element) {
        elementNo++;
        StringBuilder sb = new StringBuilder();
        if (!advanceToLine(sb, element.getLocation().getLineNumber())) {
            if (elementNo > 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private boolean advanceToLine(StringBuilder sb, long nextLineNo) {
        long prevLineNo = lineNo;
        while (lineNo < nextLineNo) {
            sb.append(lineEnding.getCharSequence());
            lineNo++;
        }
        return lineNo > prevLineNo;
    }
}
