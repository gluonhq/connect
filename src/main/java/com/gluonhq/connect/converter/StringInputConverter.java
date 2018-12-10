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
package com.gluonhq.connect.converter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An InputConverter that reads a string from an InputStream.
 */
public class StringInputConverter extends InputStreamInputConverter<String> {

    private static final Logger LOGGER = Logger.getLogger(StringInputConverter.class.getName());

    /**
     * Reads a string from an InputStream.
     *
     * @return A string object that was read from the InputStream.
     */
    @Override
    public String read() {
        try (StringWriter stringWriter = new StringWriter()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(getInputStream())); BufferedWriter writer = new BufferedWriter(stringWriter)) {
                boolean firstWrite = true;
                String line;
                while ((line = reader.readLine()) != null) {
                    if (firstWrite) {
                        firstWrite = false;
                    } else {
                        writer.newLine();
                    }
                    writer.write(line);
                }
            }

            return stringWriter.toString();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Something went wrong while reading string from InputStream.", ex);
            return null;
        }
    }
}
