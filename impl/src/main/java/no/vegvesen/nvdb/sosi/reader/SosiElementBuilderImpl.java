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

import no.vegvesen.nvdb.sosi.SosiMessages;
import no.vegvesen.nvdb.sosi.document.SosiElement;
import no.vegvesen.nvdb.sosi.document.SosiRefNumber;
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
public class SosiElementBuilderImpl implements SosiElementBuilder {
    private final String name;
    private final SosiLocation location;
    private List<SosiElement> subElements;
    private List<SosiValue> values;

    public SosiElementBuilderImpl(String name, SosiLocation location) {
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
    public SosiElementBuilder addIslandValue(SosiValue value) {
        validateValue(value);
        putIslandValue(value);
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

    private void putIslandValue(SosiValue value) {
        if (!(value instanceof SosiRefNumberImpl)) {
            throw new IllegalArgumentException("Islands support SosiRefNumber values only");
        }
        if (isNull(values)) {
            throw new IllegalStateException("Cannot put island value when there are no values");
        }
        SosiValue lastValue = values.get(values.size()-1);
        if (!(lastValue instanceof SosiRefIslandImpl)) {
            throw new IllegalArgumentException("No island to put value into");
        }

        ((SosiRefIslandImpl)lastValue).addRefNumber((SosiRefNumber) value);
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
