// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.reader;

import no.vegvesen.nvdb.sosi.document.SosiDocument;
import no.vegvesen.nvdb.sosi.document.SosiElement;
import no.vegvesen.nvdb.sosi.document.SosiString;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;


/**
 * Implements a SOSI document
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
class SosiDocumentImpl implements SosiDocument {
    private static final String ELEMENT_HEAD = "HODE";
    private static final String ELEMENT_CHARSET = "TEGNSETT";
    private static final String DEFAULT_ENCODING = "ISO-8859-1";

    private List<SosiElement> elements;

    static SosiDocument of(List<SosiElement> elements) {
        return new SosiDocumentImpl(elements);
    }

    SosiDocumentImpl(List<SosiElement> elements) {
        this.elements = elements;
    }

    @Override
    public Charset getEncoding() {
        String sosiCharset = findElementRecursively(ELEMENT_CHARSET).map(e -> e.getValueAs(SosiString.class).getString()).orElse("");
        String encoding = DEFAULT_ENCODING;

        switch (sosiCharset.toUpperCase()) {
            case "ANSI" :
            case "ISO8859-1" :
            case "ISO8859-10" :
                encoding = "ISO-8859-1";
                break;
            case "DOSN8" :
            case "ND7" :
            case "DECN7" :
                encoding = sosiCharset;
                break;
        }

        return Charset.forName(encoding);
    }

    @Override
    public SosiElement getHead() {
        return elements()
                .filter(e -> e.getName().equalsIgnoreCase(ELEMENT_HEAD))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Head element not found"));
    }

    @Override
    public Collection<SosiElement> getElements() {
        return Collections.unmodifiableCollection(elements);
    }

    @Override
    public Stream<SosiElement> elements() {
        return elements.stream();
    }

    @Override
    public Optional<SosiElement> findElement(String name) {
        return elements().filter(e -> e.getName().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    public Optional<SosiElement> findElementRecursively(String name) {
        Optional<SosiElement> maybeMatch = findElement(name);
        if (maybeMatch.isPresent()) {
            return maybeMatch;
        } else {
            for (SosiElement element : elements) {
                maybeMatch = element.findSubElementRecursively(name);
                if (maybeMatch.isPresent()) {
                    return maybeMatch;
                }
            }
            return Optional.empty();
        }
    }
}
