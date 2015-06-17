// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.writer;

import no.vegvesen.nvdb.sosi.document.SosiElement;
import no.vegvesen.nvdb.sosi.document.SosiValue;


/**
 * Implements a layout formatter that preserves formatting of a previously parsed SOSI file
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public class LocationBasedSosiLayoutFormatter implements SosiLayoutFormatter {
    private long lineNo = 1;

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
        StringBuilder sb = new StringBuilder();
        if (!advanceToLine(sb, element.getLocation().getLineNumber())) {
            if (lineNo > 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private boolean advanceToLine(StringBuilder sb, long nextLineNo) {
        long prevLineNo = lineNo;
        while (lineNo < nextLineNo) {
            sb.append("\n");
            lineNo++;
        }
        return lineNo > prevLineNo;
    }
}
