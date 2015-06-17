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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.isNull;

/**
 * Implements a SosiElementBuilder used during parsing
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
class SosiElementBuilderImpl implements SosiElementBuilder {
    private final String name;
    private final SosiLocation location;
    private List<SosiElement> subElements;
    private List<SosiValue> values;

    SosiElementBuilderImpl(String name, SosiLocation location) {
        this.name = name;
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
        return new SosiElementImpl(name, location, snapshotValues, snapshotSubElements);
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

        SosiValue newValue = SosiStringImpl.of(lastValue.getString() + value.getString(), lastValue.getLocation());
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
}
