// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.writer;

import no.vegvesen.nvdb.sosi.document.SosiElement;
import no.vegvesen.nvdb.sosi.document.SosiValue;


/**
 * Defines a SOSI file layout formatter
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public interface SosiLayoutFormatter {

    /**
     * The string to prepend a value in the SOSI file.
     * @param value the value to be prepended
     * @return the prepend text
     */
    String beforeValue(SosiValue value);

    /**
     * The string to prepend an element in the SOSI file.
     * @param element the element to be prepended
     * @return the prepend text
     */
    String beforeElement(SosiElement element);
}
