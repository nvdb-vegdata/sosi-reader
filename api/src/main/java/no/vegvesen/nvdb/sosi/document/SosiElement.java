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
package no.vegvesen.nvdb.sosi.document;

import no.vegvesen.nvdb.sosi.SosiLocation;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A SOSI element with zero or more values and zero or more subelements.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public interface SosiElement {
    String getName();

    SosiLocation getLocation();

    Optional<SosiElement> findSubElement(Predicate<SosiElement> predicate);

    Optional<SosiElement> findSubElementRecursively(Predicate<SosiElement> predicate);

    Stream<SosiElement> findSubElements(Predicate<SosiElement> predicate);

    boolean hasSubElements();

    Stream<SosiElement> subElements();

    boolean hasValues();

    Stream<SosiValue> values();

    <T> T getValueAs(Class<T> valueClass);

    <T> List<T> getValuesAs(Class<T> valueClass);

    void rename(Function<String, String> transformer);

    void computeValues(Function<Stream<SosiValue>, Stream<SosiValue>> transformer);
}
