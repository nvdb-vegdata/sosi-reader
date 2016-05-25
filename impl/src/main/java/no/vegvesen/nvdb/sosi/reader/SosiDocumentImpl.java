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
