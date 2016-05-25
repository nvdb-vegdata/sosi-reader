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
package no.vegvesen.nvdb.sosi.utils;

import no.vegvesen.nvdb.sosi.document.SosiDocument;
import no.vegvesen.nvdb.sosi.document.SosiElement;
import no.vegvesen.nvdb.sosi.document.SosiSerialNumber;
import no.vegvesen.nvdb.sosi.document.SosiValue;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;
import static no.vegvesen.nvdb.sosi.document.SosiValue.ValueType.*;
import static no.vegvesen.nvdb.sosi.utils.Functions.castTo;

/**
 * General purpose predicates for SOSI elements and values.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public abstract class Predicates {
    public static Predicate<SosiElement> hasName(String name) {
        return e -> e.getName().equalsIgnoreCase(name);
    }

    public static Predicate<SosiElement> hasNameOneOf(String... names) {
        requireNonNull(names, "No names specified");
        Set<String> namesToMatch = Arrays.stream(names).map(String::toUpperCase).collect(toSet());
        return e -> namesToMatch.contains(e.getName().toUpperCase());
    }

    public static Predicate<SosiElement> isHead() {
        return e -> e.getName().equalsIgnoreCase(SosiDocument.ELEMENT_HEAD);
    }

    public static Predicate<SosiElement> isEnd() {
        return e -> e.getName().equalsIgnoreCase(SosiDocument.ELEMENT_END);
    }

    public static Predicate<SosiElement> hasSerialNumber(long serialNumber) {
        return e -> e.values()
                .filter(isType(SERNO))
                .map(castTo(SosiSerialNumber.class))
                .mapToLong(SosiSerialNumber::longValue)
                .anyMatch(s -> s == serialNumber);
    }

    public static Predicate<SosiValue> isType(SosiValue.ValueType type) {
        return v -> v.getValueType() == type;
    }
}
