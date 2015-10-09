// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.reader;

import no.vegvesen.nvdb.sosi.document.SosiDocument;
import no.vegvesen.nvdb.sosi.document.SosiElement;
import no.vegvesen.nvdb.sosi.document.SosiString;
import no.vegvesen.nvdb.sosi.encoding.charset.SosiCharset;
import no.vegvesen.nvdb.sosi.encoding.SosiEncoding;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static no.vegvesen.nvdb.sosi.utils.Predicates.hasName;
import static no.vegvesen.nvdb.sosi.utils.Predicates.isEnd;
import static no.vegvesen.nvdb.sosi.utils.Predicates.isHead;


/**
 * Implements a SOSI document
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
class SosiDocumentImpl implements SosiDocument {
    private static final String ELEMENT_CHARSET = "TEGNSETT";

    private Collection<SosiElement> elements;

    static SosiDocument of(Collection<SosiElement> elements) {
        return new SosiDocumentImpl(elements);
    }

    SosiDocumentImpl(Collection<SosiElement> elements) {
        this.elements = requireNonNull(elements, "elements can't be null");
    }

    @Override
    public Charset getEncoding() {
        String sosiCharset = findElementRecursively(hasName(ELEMENT_CHARSET)).map(e -> e.getValueAs(SosiString.class).getString()).orElse("");
        return SosiEncoding.charsetNameFromSosiValue(sosiCharset)
                .map(SosiCharset::forName)
                .orElse(SosiEncoding.defaultCharset());
    }

    @Override
    public SosiElement getHead() {
        return elements()
                .filter(isHead())
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Head element not found"));
    }

    @Override
    public Collection<SosiElement> getElements() {
        return Collections.unmodifiableCollection(elements);
    }

    @Override
    public SosiElement getEnd() {
        return elements()
                .filter(isEnd())
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("End element not found"));
    }

    @Override
    public Stream<SosiElement> elements() {
        return elements.stream();
    }

    @Override
    public Optional<SosiElement> findElement(Predicate<SosiElement> predicate) {
        requireNonNull(predicate, "predicate can't be null");
        return elements().filter(predicate).findFirst();
    }

    @Override
    public Optional<SosiElement> findElementRecursively(Predicate<SosiElement> predicate) {
        requireNonNull(predicate, "predicate can't be null");
        Optional<SosiElement> maybeMatch = findElement(predicate);
        if (maybeMatch.isPresent()) {
            return maybeMatch;
        } else {
            for (SosiElement element : elements) {
                maybeMatch = element.findSubElementRecursively(predicate);
                if (maybeMatch.isPresent()) {
                    return maybeMatch;
                }
            }
            return Optional.empty();
        }
    }
}
