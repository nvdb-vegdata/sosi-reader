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
 * Implements a layout formatter that prints a single element (plus its values) on each line
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public class DefaultSosiLayoutFormatter implements SosiLayoutFormatter {
    private final LineEnding lineEnding;
    private String currentElement;
    private long valueNo;
    private long lineNo = 1;

    public DefaultSosiLayoutFormatter() {
        this.lineEnding = LineEnding.WINDOWS;
    }

    public DefaultSosiLayoutFormatter(LineEnding lineEnding) {
        this.lineEnding = lineEnding;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String beforeValue(SosiValue value) {
        valueNo++;
        if ("NØH".equals(currentElement)) {
            if ((valueNo-1) % 3 == 0) {
                return lineEnding.getCharSequence();
            }
        }
        if ("NØ".equals(currentElement)) {
            if ((valueNo-1) % 2 == 0) {
                return lineEnding.getCharSequence();
            }
        }

        return " ";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String afterValue(SosiValue value) {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String beforeElement(SosiElement element) {
        currentElement = element.getName();
        valueNo = 0;
        return lineNo++ == 1 ? "" : lineEnding.getCharSequence();
    }
}
