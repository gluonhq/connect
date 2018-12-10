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

import java.io.InputStream;

/**
 * An abstract helper class that defines an InputConverter of which the input source is a standard
 * {@link java.io.InputStream}. This class can be extended by an implementation of InputConverter that makes use of an
 * InputStream as its input source. The data that is being read from the InputStream can be used to convert it into an
 * actual object.
 *
 * @param <T> the type of the object that is read by this InputConverter
 */
public abstract class InputStreamInputConverter<T> implements InputConverter<T> {

    private InputStream inputStream;

    /**
     * Returns the InputStream where this InputConverter will read its data from.
     *
     * @return The InputStream to use as the input source for this InputConverter.
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Sets the InputStream to be used as the input source for this InputConverter.
     *
     * @param inputStream The InputStream to use as the input source for this InputConverter.
     */
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
