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
import com.gluonhq.connect.converter.JsonInputConverter;
import com.gluonhq.connect.converter.StringInputConverter;
import com.gluonhq.connect.converter.VoidInputConverter;
import com.gluonhq.connect.source.RestDataSource;

import java.io.IOException;
import java.io.InputStream;

public class RestObjectDataReader<T> extends BaseRestProvider implements ObjectDataReader<T> {

    protected final Class<T> targetClass;
    private final InputStreamInputConverter<T> inputConverter;

    public RestObjectDataReader(RestDataSource dataSource, Class<T> targetClass) {
        super(dataSource);

        this.targetClass = targetClass;
        this.inputConverter = null;
    }

    public RestObjectDataReader(RestDataSource dataSource, InputStreamInputConverter<T> inputConverter) {
        super(dataSource);

        this.targetClass = null;
        this.inputConverter = inputConverter;
    }

    @Override
    public GluonObservableObject<T> newGluonObservableObject() {
        return new GluonObservableObject<>();
    }

    @Override
    public T readObject() throws IOException {
        InputStream inputStream = dataSource.getInputStream();
        InputStreamInputConverter converter = inputConverter;
        if (converter == null) {
            if (targetClass != null && String.class.isAssignableFrom(targetClass)) {
                converter = new StringInputConverter();
            } else if (targetClass != null && Void.class.isAssignableFrom(targetClass)) {
                converter = new VoidInputConverter();
            } else {
                int responseCode = getRestDataSource().getResponseCode();
                if (responseCode == 204) {
                    converter = new VoidInputConverter();
                } else {
                    String contentType = getContentType();
                    if (contentType != null) {
                        if (contentType.startsWith(CONTENT_TYPE_APPLICATION_JSON)) {
                            converter = new JsonInputConverter<>(targetClass);
                        } else if (contentType.startsWith(CONTENT_TYPE_TEXT_PLAIN)) {
                            converter = new StringInputConverter();
                        } else {
                            throw new IllegalStateException("Could not determine InputConverter based on Content-Type: " + contentType);
                        }
                    } else {
                        converter = new JsonInputConverter<>(targetClass);
                    }
                }
            }
        }

        converter.setInputStream(inputStream);
        return (T) converter.read();
    }
}
