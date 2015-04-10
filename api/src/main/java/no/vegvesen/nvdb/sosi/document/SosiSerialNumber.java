// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.document;


/**
 * An immutable SOSI serial number value.
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public interface SosiSerialNumber extends SosiValue {

    long longValue();

    long longValueExact();

    @Override
    String toString();

    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();
}
