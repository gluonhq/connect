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
package com.gluonhq.impl.connect.provider;

import com.gluonhq.connect.GluonObservableObject;
import com.gluonhq.connect.converter.InputStreamInputConverter;
import com.gluonhq.connect.converter.JsonOutputConverter;
import com.gluonhq.connect.converter.OutputStreamOutputConverter;
import com.gluonhq.connect.converter.StringOutputConverter;
import com.gluonhq.connect.converter.VoidOutputConverter;
import com.gluonhq.connect.provider.ObjectDataRemover;
import com.gluonhq.connect.provider.ObjectDataWriter;
import com.gluonhq.connect.source.RestDataSource;

import java.io.IOException;
import java.util.Optional;

public class RestObjectDataWriterAndRemover<T> extends RestObjectDataReader<T> implements ObjectDataWriter<T>, ObjectDataRemover<T> {

    private final OutputStreamOutputConverter<T> outputConverter;

    public RestObjectDataWriterAndRemover(RestDataSource dataSource, Class<T> targetClass) {
        super(dataSource, targetClass);

        this.outputConverter = null;
    }

    public RestObjectDataWriterAndRemover(RestDataSource dataSource, OutputStreamOutputConverter<T> outputConverter,
                                          InputStreamInputConverter<T> inputConverter) {
        super(dataSource, inputConverter);

        this.outputConverter = outputConverter;
    }

    @Override
    public Optional<T> writeObject(T object) throws IOException {
        getOutputConverter().write(object);

        return Optional.ofNullable(readObject());
    }

    @Override
    public Optional<T> removeObject(GluonObservableObject<T> observable) throws IOException {
        getOutputConverter().write(observable.get());

        return Optional.ofNullable(readObject());
    }

    private OutputStreamOutputConverter<T> getOutputConverter() throws IOException {
        OutputStreamOutputConverter converter = outputConverter;

        if (converter == null) {
            if (dataSource.getContentType() != null) {
                if (dataSource.getContentType().startsWith(CONTENT_TYPE_APPLICATION_JSON)) {
                    converter = new JsonOutputConverter<>(targetClass);
                } else {
                    throw new IllegalStateException("Could not determine OutputConverter based on Content-Type: " + dataSource.getContentType());
                }
            } else if (targetClass != null && String.class.isAssignableFrom(targetClass)) {
                converter = new StringOutputConverter();
            } else if (targetClass != null && Void.class.isAssignableFrom(targetClass)) {
                converter = new VoidOutputConverter();
            } else {
                converter = new JsonOutputConverter<>(targetClass);
            }
        }

        converter.setOutputStream(dataSource.getOutputStream());
        return converter;
    }
}
