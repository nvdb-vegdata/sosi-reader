// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.reader;

import no.vegvesen.nvdb.sosi.SosiLocation;
import no.vegvesen.nvdb.sosi.document.SosiElement;
import no.vegvesen.nvdb.sosi.document.SosiSerialNumber;
import no.vegvesen.nvdb.sosi.document.SosiValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static no.vegvesen.nvdb.sosi.utils.Argument.require;

/**
 * Factory for SosiElement objects.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public abstract class SosiElementFactory {

    /**
     * Creates a SosiElement.
     * @param name the element name.
     * @param location the location of the element inside the SOSI file
     * @param subElements the subelements of the element, if any
     * @param values the values of the element, if any
     * @return a SosiElement instance
     */
    public static SosiElement element(String name, SosiLocation location, List<SosiElement> subElements, List<SosiValue> values) {
        requireNonNull(name, "name can't be null");
        require(() -> !name.isEmpty(), "name not specified");
        requireNonNull(location, "location can't be null");
        requireNonNull(subElements, "subElements can't be null");
        requireNonNull(values, "values can't be null");

        return new SosiElementImpl(name, location, values, subElements);
    }

    /**
     * Creates a SosiElement.
     * @param name the element name.
     * @param subElements the subelements of the element, if any
     * @param values the values of the element, if any
     * @return a SosiElement instance
     */
    public static SosiElement element(String name, List<SosiElement> subElements, SosiValue... values) {
        requireNonNull(values, "values can't be null");

        List<SosiValue> valueList = Arrays.stream(values).collect(toList());
        return element(name, SosiLocation.unknown(), subElements, valueList);
    }

    /**
     * Creates a SosiElement.
     * @param name the element name.
     * @param values the values of the element, if any
     * @return a SosiElement instance
     */
    public static SosiElement element(String name, SosiValue... values) {
        requireNonNull(values, "values can't be null");

        List<SosiValue> valueList = Arrays.stream(values).collect(toList());
        return element(name, SosiLocation.unknown(), Collections.emptyList(), valueList);
    }

    /**
     * Creates a SosiElement group with a serial number.
     * @param name the element name.
     * @param location the location of the element inside the SOSI file
     * @param subElements the subelements of the element, if any
     * @return a SosiElement instance
     */
    public static SosiElement group(String name, SosiLocation location, Optional<SosiSerialNumber> serialNo, List<SosiElement> subElements) {
        requireNonNull(name, "name can't be null");
        require(() -> !name.isEmpty(), "name not specified");
        requireNonNull(location, "location can't be null");
        requireNonNull(serialNo, "serialNo can't be null");
        requireNonNull(subElements, "subElements can't be null");

        List<SosiValue> valueList = new LinkedList<>();
        serialNo.ifPresent(valueList::add);
        return new SosiElementImpl(name, location, valueList, subElements);
    }

    /**
     * Creates a SosiElement group with a serial number.
     * @param name the element name.
     * @param subElements the subelements of the element, if any
     * @return a SosiElement instance
     */
    public static SosiElement group(String name, Optional<SosiSerialNumber> serialNo, SosiElement... subElements) {
        requireNonNull(subElements, "subElements can't be null");

        List<SosiElement> subElementList = Arrays.stream(subElements).collect(toList());
        return group(name, SosiLocation.unknown(), serialNo, subElementList);
    }

    /**
     * Creates a SosiElement group with a serial number.
     * @param name the element name.
     * @param subElements the subelements of the element, if any
     * @return a SosiElement instance
     */
    public static SosiElement group(String name, Optional<SosiSerialNumber> serialNo, List<SosiElement> subElements) {
        return group(name, SosiLocation.unknown(),serialNo, subElements);
    }
}
