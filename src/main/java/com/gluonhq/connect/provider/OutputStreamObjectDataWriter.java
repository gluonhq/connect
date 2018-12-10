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
import com.gluonhq.connect.converter.OutputStreamOutputConverter;
import com.gluonhq.connect.source.OutputDataSource;

import java.io.IOException;
import java.util.Optional;

/**
 * A helper class that represents an {@link ObjectDataWriter} that writes an object by converting it with a specific
 * {@link com.gluonhq.connect.converter.OutputStreamOutputConverter} and writing it to a specific
 * {@link OutputDataSource}.
 *
 * @param <T> the type of the object to write
 */
public class OutputStreamObjectDataWriter<T> implements ObjectDataWriter<T> {

    private final OutputDataSource dataSource;
    private final OutputStreamOutputConverter<T> converter;

    /**
     * Construct an instance that will use the specified data source to write the object to that is converted with the
     * specified converter. The OutputStream of the OutputDataSource will be set on the Converter before calling its
     * {@link OutputStreamOutputConverter#write(Object) write} method.
     *
     * @param dataSource the data source where the converted object will be written to
     * @param converter the converter that is used for converting the object to be written
     */
    public OutputStreamObjectDataWriter(OutputDataSource dataSource, OutputStreamOutputConverter<T> converter) {
        this.dataSource = dataSource;
        this.converter = converter;
    }

    @Override
    public GluonObservableObject<T> newGluonObservableObject() {
        return new GluonObservableObject<>();
    }

    /**
     * Writes the object to the specified data source and returns an Optional that contains the exact same object
     * instance that was passed in. This implementation uses the specified converter to convert the provided
     * <code>object</code>. The OutputStream of the specified data source will be set on the converter, before calling
     * the {@link OutputStreamOutputConverter#write(Object) write} method on the converter.
     *
     * @param object the object to write
     * @return the object that will ultimately be set on the GluonObservableObject
     * @throws IOException when an exception occurred during the write process
     */
    @Override
    public Optional<T> writeObject(T object) throws IOException {
        converter.setOutputStream(dataSource.getOutputStream());
        converter.write(object);
        return Optional.of(object);
    }
}
