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

import no.vegvesen.nvdb.sosi.SosiLocation;
import no.vegvesen.nvdb.sosi.document.SosiElement;
import no.vegvesen.nvdb.sosi.document.SosiValue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Implements a SosiElement
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
class SosiElementImpl implements SosiElement {
    private String name;
    private final SosiLocation location;
    private final List<SosiElement> subElements;
    private List<SosiValue> values;

    SosiElementImpl(String name, SosiLocation location, List<SosiValue> values, List<SosiElement> subElements) {
        this.name = requireNonNull(name, "name can't be null");
        this.location = requireNonNull(location, "location can't be null");
        this.values = requireNonNull(values, "values can't be null");
        this.subElements = requireNonNull(subElements, "subElements can't be null");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SosiLocation getLocation() {
        return location;
    }

    @Override
    public Optional<SosiElement> findSubElement(Predicate<SosiElement> predicate) {
        requireNonNull(predicate, "predicate can't be null");
        return subElements().filter(predicate).findFirst();
    }

    @Override
    public Optional<SosiElement> findSubElementRecursively(Predicate<SosiElement> predicate) {
        requireNonNull(predicate, "predicate can't be null");
        Optional<SosiElement> maybeMatch = findSubElement(predicate);
        if (maybeMatch.isPresent()) {
            return maybeMatch;
        } else {
            for (SosiElement element : subElements) {
                maybeMatch = element.findSubElementRecursively(predicate);
                if (maybeMatch.isPresent()) {
                    return maybeMatch;
                }
            }
            return Optional.empty();
        }
    }

    @Override
    public Stream<SosiElement> findSubElements(Predicate<SosiElement> predicate) {
        requireNonNull(predicate, "predicate can't be null");
        return subElements().filter(predicate);
    }

    @Override
    public boolean hasSubElements() {
        return subElements().anyMatch(e -> true);
    }

    @Override
    public Stream<SosiElement> subElements() {
        return isNull(subElements) ? Stream.empty() : subElements.stream();
    }

    @Override
    public boolean hasValues() {
        return values().anyMatch(v -> true);
    }

    @Override
    public Stream<SosiValue> values() {
        return isNull(values) ? Stream.empty() : values.stream();
    }

    @Override
    public <T> T getValueAs(Class<T> valueClass) {
        requireNonNull(valueClass, "valueClass can't be null");
        Optional<SosiValue> maybeValue = values().findFirst();
        if (!maybeValue.isPresent()) {
            throw new IllegalStateException("No values for this element");
        }
        return valueClass.cast(maybeValue.get());
    }

    @Override
    public <T> List<T> getValuesAs(Class<T> valueClass) {
        requireNonNull(valueClass, "valueClass can't be null");
        return values().map(valueClass::cast).collect(toList());
    }

    @Override
    public void rename(Function<String, String> transformer) {
        requireNonNull(transformer, "transformer can't be null");
        this.name = transformer.apply(name);
    }

    @Override
    public void computeValues(Function<Stream<SosiValue>, Stream<SosiValue>> transformer) {
        requireNonNull(transformer, "transformer can't be null");
        this.values = transformer.apply(values()).collect(toList());
    }

    @Override
    public String toString() {
        return getName() + " (" + values().count() + " value(s) and " + subElements().count() + " subelement(s))";
    }
}
