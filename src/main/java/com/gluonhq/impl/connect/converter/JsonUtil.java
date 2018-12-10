/*
 * Copyright (c) 2016 Gluon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of Gluon, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL GLUON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.impl.connect.converter;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A utility class that provides useful methods when working with JSON data.
 */
public class JsonUtil {

    private static final Logger LOG = Logger.getLogger(JsonUtil.class.getName());

    private static final JsonReaderFactory readerFactory = Json.createReaderFactory(null);

    /**
     * Create a JsonReader from the specified <code>InputStream</code>. When the logging level for this class is set
     * to {@link Level#FINE}, the JSON content of the InputStream will be written to a String and logged. Otherwise,
     * it will just create a JsonReader from a basic <code>InputStreamReader</code>.
     * @param input the InputStream to read the JSON data from
     * @return a JsonReader to read the data from the InputStream
     */
    public static JsonReader createJsonReader(InputStream input) {
        Reader sourceReader;
        if (LOG.isLoggable(Level.FINE)) {
            String string;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"))) {
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                string = stringBuilder.toString();
                sourceReader = new StringReader(string);

                LOG.fine("Read JSON data: " + string);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Something went wrong while reading plain text from inputstream.", ex);
                return null;
            }
        } else {
            sourceReader = new InputStreamReader(input);
        }

        return readerFactory.createReader(sourceReader);
    }
}
