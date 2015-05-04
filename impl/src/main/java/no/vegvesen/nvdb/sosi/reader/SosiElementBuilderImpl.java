// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.reader;

import no.vegvesen.nvdb.sosi.SosiMessages;
import no.vegvesen.nvdb.sosi.document.SosiElement;
import no.vegvesen.nvdb.sosi.document.SosiValue;
import no.vegvesen.nvdb.sosi.SosiLocation;
import no.vegvesen.nvdb.sosi.utils.BufferPool;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

/**
 * Implements a SosiElementBuilder
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
class SosiElementBuilderImpl implements SosiElementBuilder {
    private final String name;
    private SosiLocation location;
    private List<SosiElement> subElements;
    private List<SosiValue> values;
    private final BufferPool bufferPool;

    SosiElementBuilderImpl(String name, BufferPool bufferPool, SosiLocation location) {
        this.name = name;
        this.bufferPool = bufferPool;
        this.location = location;
    }

    @Override
    public SosiElementBuilder addValue(SosiValue value) {
        validateValue(value);
        putValue(value);
        return this;
    }

    @Override
    public SosiElementBuilder addValue(String value, SosiLocation location) {
        validateValue(value);
        putValue(SosiStringImpl.of(value, location));
        return this;
    }

    @Override
    public SosiElementBuilder concatValue(String value, SosiLocation location) {
        validateValue(value);
        appendValue(SosiStringImpl.of(value, location));
        return this;
    }

    @Override
    public SosiElementBuilder addValue(BigInteger value, SosiLocation location) {
        validateValue(value);
        putValue(SosiNumberImpl.of(value, location));
        return this;
    }

    @Override
    public SosiElementBuilder addValue(BigDecimal value, SosiLocation location) {
        validateValue(value);
        putValue(SosiNumberImpl.of(value, location));
        return this;
    }

    @Override
    public SosiElementBuilder addValue(int value, SosiLocation location) {
        putValue(SosiNumberImpl.of(value, location));
        return this;
    }

    @Override
    public SosiElementBuilder addValue(long value, SosiLocation location) {
        putValue(SosiNumberImpl.of(value, location));
        return this;
    }

    @Override
    public SosiElementBuilder addValue(double value, SosiLocation location) {
        putValue(SosiNumberImpl.of(value, location));
        return this;
    }

    @Override
    public SosiElementBuilder addSubElement(String name, SosiElement subElement) {
        validateName(name);
        if (isNull(subElement)) {
            throw new NullPointerException(SosiMessages.ELEMENTBUILDER_OBJECT_BUILDER_NULL());
        }
        putSubElement(subElement);
        return this;
    }

    @Override
    public SosiElement build() {
        List<SosiElement> snapshotSubElements = (isNull(subElements))
                ? Collections.<SosiElement>emptyList()
                : Collections.unmodifiableList(subElements);
        subElements = null;
        List<SosiValue> snapshotValues = (isNull(values))
                ? Collections.<SosiValue>emptyList()
                : Collections.unmodifiableList(values);
        values = null;
        return new SosiElementImpl(name, location, snapshotValues, snapshotSubElements, bufferPool);
    }

    private void putValue(SosiValue value) {
        if (isNull(values)) {
            this.values = new LinkedList<>();
        }
        values.add(value);
    }

    private void appendValue(SosiValue value) {
        if (!(value instanceof SosiStringImpl)) {
            throw new IllegalArgumentException("Concatenation supported for SosiString values only");
        }
        if (isNull(values)) {
            throw new IllegalStateException("Cannot concatenate when there are no values");
        }
        SosiValue lastValue = values.get(values.size()-1);
        if (!(lastValue instanceof SosiStringImpl)) {
            throw new IllegalArgumentException("Concatenation supported for SosiString values only");
        }

        SosiValue newValue = SosiStringImpl.of(((SosiStringImpl) lastValue).getString() + ((SosiStringImpl) value).getString(), lastValue.getLocation());
        values.set(values.size() - 1, newValue);
    }

    private void putSubElement(SosiElement element) {
        if (isNull(subElements)) {
            this.subElements = new LinkedList<>();
        }
        subElements.add(element);
    }

    private void validateName(String name) {
        if (isNull(name)) {
            throw new NullPointerException(SosiMessages.ELEMENTBUILDER_NAME_NULL());
        }
    }

    private void validateValue(Object value) {
        if (isNull(value)) {
            throw new NullPointerException(SosiMessages.ELEMENTBUILDER_VALUE_NULL());
        }
    }

    private static final class SosiElementImpl implements SosiElement {
        private final String name;
        private SosiLocation location;
        private final List<SosiElement> subElements;
        private List<SosiValue> values;
        private final BufferPool bufferPool;

        SosiElementImpl(String name, SosiLocation location, List<SosiValue> values, List<SosiElement> subElements, BufferPool bufferPool) {
            this.name = name;
            this.location = location;
            this.values = values;
            this.subElements = subElements;
            this.bufferPool = bufferPool;
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
            return subElements().filter(e -> e.getName().equalsIgnoreCase(name)).findFirst();
        }

        @Override
        public Optional<SosiElement> findSubElementRecursively(String name) {
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
            Objects.requireNonNull(names, "No names specified");
            List<String> namesToFind = Arrays.stream(names).map(String::toUpperCase).collect(toList());
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
            Optional<SosiValue> maybeValue = values().findFirst();
            if (!maybeValue.isPresent()) {
                throw new IllegalStateException("No values for this element");
            }
            return valueClass.cast(maybeValue.get());
        }

        @Override
        public <T> List<T> getValuesAs(Class<T> valueClass) {
            return values().map(valueClass::cast).collect(toList());
        }

        @Override
        public void transformValues(Function<Stream<SosiValue>, Stream<SosiValue>> transformer) {
            this.values = transformer.apply(values()).collect(toList());
        }

        @Override
        public String toString() {
            return getName() + " (" + values().count() + " value(s) and " + subElements().count() + " subelement(s))";
        }
    }
}
