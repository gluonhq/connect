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
package com.gluonhq.connect.provider;

import com.gluonhq.connect.GluonObservableObject;
import com.gluonhq.connect.converter.InputStreamInputConverter;
import com.gluonhq.connect.source.InputDataSource;

import java.io.IOException;

/**
 * A helper class that represents an {@link ObjectDataReader} that reads an object by reading from a specific
 * {@link InputDataSource} and converting the read data with a specific
 * {@link com.gluonhq.connect.converter.InputStreamInputConverter}.
 *
 * @param <T> the type of the object to read
 */
public class InputStreamObjectDataReader<T> implements ObjectDataReader<T> {

    private final InputDataSource dataSource;
    private final InputStreamInputConverter<T> converter;

    /**
     * Construct an instance that will use the specified data source to read data from and convert it to an object with
     * the specified converter. The InputStream of the InputDataSource will be set on the Converter before calling its
     * {@link InputStreamInputConverter#read() read} method.
     *
     * @param dataSource the data source where the data will be read from
     * @param converter the converter that is used for converting the data into an object
     */
    public InputStreamObjectDataReader(InputDataSource dataSource, InputStreamInputConverter<T> converter) {
        this.dataSource = dataSource;
        this.converter = converter;
    }

    @Override
    public GluonObservableObject<T> newGluonObservableObject() {
        return new GluonObservableObject<>();
    }

    /**
     * Reads the object from the specified data source. This implementation uses the specified converter to convert
     * the data that is read from the specified data source into an object. The InputStream of the specified data source
     * will be set on the converter, before calling the {@link InputStreamInputConverter#read() read} method on the
     * converter.
     *
     * @return the read object
     * @throws IOException when an exception occurred during the read process
     */
    @Override
    public T readObject() throws IOException {
        converter.setInputStream(dataSource.getInputStream());
        return converter.read();
    }
}
