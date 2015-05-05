// This software is produced by Statens vegvesen. Unauthorized redistribution,
// reproduction or usage of this software in whole or in part without the
// express written consent of Statens vegvesen is strictly prohibited.
// Copyright © 2015 Statens vegvesen
// ALL RIGHTS RESERVED
package no.vegvesen.nvdb.sosi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.util.Objects.isNull;

/**
 * TODO: Purpose and responsibility
 *
 * @author Tore Eide Andersen (Kantega AS)
 */
public class TestUtils {
    public static InputStream getResource(String name) {
        InputStream stream = TestUtils.class.getClassLoader().getResourceAsStream(name);
        if (isNull(stream)) {
            throw new IllegalArgumentException("Resource " + name + " not found");
        }
        return stream;
    }

    public static String streamToString(InputStream stream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line).append("\n");
            }
            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read stream", e);
        }
    }
}