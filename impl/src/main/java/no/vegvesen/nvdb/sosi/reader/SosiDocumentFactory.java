// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.reader;

import no.vegvesen.nvdb.sosi.document.SosiDocument;
import no.vegvesen.nvdb.sosi.document.SosiElement;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static no.vegvesen.nvdb.sosi.utils.Argument.require;

/**
 * Factory for SosiDocument objects.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public abstract class SosiDocumentFactory {

    /**
     * Creates a SosiDocument.
     * @param elements the elements to be contained in the document
     * @return a SosiDocument instance
     */
    public static SosiDocument document(List<SosiElement> elements) {
        requireNonNull(elements, "elements can't be null");
        require(() -> !elements.isEmpty(), "elements can't be empty");
        return new SosiDocumentImpl(elements);
    }

    /**
     * Creates a SosiDocument.
     * @param elements the elements to be contained in the document
     * @return a SosiDocument instance
     */
    public static SosiDocument document(SosiElement... elements) {
        requireNonNull(elements, "elements can't be null");
        List<SosiElement> elementList = Arrays.stream(elements).collect(toList());
        require(() -> !elementList.isEmpty(), "elements can't be empty");
        return new SosiDocumentImpl(elementList);
    }
}
