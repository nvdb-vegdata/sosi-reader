// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.document;

import java.util.stream.Stream;

/**
 * An immutable SOSI reference island (containing one or more reference number values).
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public interface SosiRefIsland extends SosiValue {
    Stream<SosiRefNumber> refNumbers();

    String getString();

    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);

    @Override
    String toString();
}
