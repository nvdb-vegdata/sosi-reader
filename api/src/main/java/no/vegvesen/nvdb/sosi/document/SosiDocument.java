// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.document;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A SOSI document with one or more elements.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public interface SosiDocument {
    String ELEMENT_HEAD = "HODE";
    String ELEMENT_END = "SLUTT";

    Charset getEncoding();

    SosiElement getHead();

    Collection<SosiElement> getElements();

    SosiElement getEnd();

    Stream<SosiElement> elements();

    Optional<SosiElement> findElement(Predicate<SosiElement> predicate);

    Optional<SosiElement> findElementRecursively(Predicate<SosiElement> predicate);
}
