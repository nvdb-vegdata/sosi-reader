// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.reader;

import no.vegvesen.nvdb.sosi.SosiLocation;
import no.vegvesen.nvdb.sosi.document.SosiElement;
import no.vegvesen.nvdb.sosi.document.SosiValue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
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
    public Optional<SosiElement> findSubElement(String name) {
        requireNonNull(name, "name can't be null");
        return subElements().filter(e -> e.getName().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    public Optional<SosiElement> findSubElementRecursively(String name) {
        requireNonNull(name, "name can't be null");
        Optional<SosiElement> maybeMatch = findSubElement(name);
        if (maybeMatch.isPresent()) {
            return maybeMatch;
        } else {
            for (SosiElement element : subElements) {
                maybeMatch = element.findSubElementRecursively(name);
                if (maybeMatch.isPresent()) {
                    return maybeMatch;
                }
            }
            return Optional.empty();
        }
    }

    @Override
    public List<SosiElement> findSubElements(String... names) {
        requireNonNull(names, "No names specified");
        Set<String> namesToFind = Arrays.stream(names).map(String::toUpperCase).collect(toSet());
        return subElements().filter(e -> namesToFind.contains(e.getName().toUpperCase())).collect(toList());
    }

    @Override
    public Stream<SosiElement> subElements() {
        return isNull(subElements) ? Stream.empty() : subElements.stream();
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
