// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.utils;

import java.util.function.Function;

/**
 * TODO: Purpose and responsibility
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public abstract class Functions {
    public static <S, T> Function<S,T> castTo(Class<T> targetClass) {
        return targetClass::cast;
    }
}
