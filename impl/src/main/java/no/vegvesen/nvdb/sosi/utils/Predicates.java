// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
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
