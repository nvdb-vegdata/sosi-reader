// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.writer;

import no.vegvesen.nvdb.sosi.document.SosiElement;
import no.vegvesen.nvdb.sosi.document.SosiValue;

import static no.vegvesen.nvdb.sosi.document.SosiValue.ValueType.REF;
import static no.vegvesen.nvdb.sosi.document.SosiValue.ValueType.SERNO;
import static no.vegvesen.nvdb.sosi.document.SosiValue.ValueType.STRING;

/**
 * Implements the default SOSI value formatter
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public class DefaultSosiValueFormatter implements SosiValueFormatter {

    /**
     * {@inheritDoc}
     */
    @Override
    public String apply(SosiElement element, SosiValue value) {
        if (value.getValueType() == STRING) {
            return "\"" + value.getString() + "\"";
        } else if (value.getValueType() == REF) {
            return ":" + value.getString();
        } else if (value.getValueType() == SERNO) {
            return value.getString() + ":";
        } else {
            return value.getString();
        }
    }
}
