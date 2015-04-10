// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.document;

import no.vegvesen.nvdb.sosi.SosiLocation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A SOSI element with zero or more values and zero or more subelements.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public interface SosiElement {
    String getName();

    SosiLocation getLocation();

    Optional<SosiElement> findSubElement(String name);

    Optional<SosiElement> findSubElementRecursively(String name);

    List<SosiElement> findSubElements(String name);

    Stream<SosiElement> subElements();

    Stream<SosiValue> values();

    <T> T getValueAs(Class<T> valueClass);

    <T> List<T> getValuesAs(Class<T> valueClass);
}
