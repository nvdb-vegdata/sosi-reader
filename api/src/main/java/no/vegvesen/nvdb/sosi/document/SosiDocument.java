// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.document;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A SOSI document with one or more elements.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public interface SosiDocument {

    SosiElement getHead();

    Collection<SosiElement> getElements();

    Stream<SosiElement> elements();

    Optional<SosiElement> findElement(String name);

    Optional<SosiElement> findElementRecursively(String name);
}
