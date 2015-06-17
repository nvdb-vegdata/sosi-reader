// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi.utils;

/**
 * Constructs to simplify argument assertions
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public abstract class Argument {

    /**
     * Test the requirement and throws an exception if not fulfilled.
     * @param requirement the requirement
     * @param msg the exception message
     * @param args arguments to merge into the exception message
     */
    public static void require(Requirement requirement, String msg, Object... args) {
        if (!requirement.test()) {
            throw new IllegalArgumentException(String.format(msg, args));
        }
    }

    /**
     * Defines a requirement.
     */
    @FunctionalInterface
    public interface Requirement {
        boolean test();
    }
}
