// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
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
    public String beforeElement(SosiElement element) {
        currentElement = element.getName();
        valueNo = 0;
        return lineNo++ == 1 ? "" : lineEnding.getCharSequence();
    }
}
