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

import com.gluonhq.connect.GluonObservableList;
import com.gluonhq.connect.converter.InputStreamIterableInputConverter;
import com.gluonhq.connect.converter.JsonIterableInputConverter;
import com.gluonhq.connect.provider.ListDataReader;
import com.gluonhq.connect.source.RestDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class RestListDataReader<E> extends BaseRestProvider implements ListDataReader<E> {

    private final Class<E> targetClass;
    private final InputStreamIterableInputConverter<E> inputConverter;

    public RestListDataReader(RestDataSource dataSource, Class<E> targetClass) {
        super(dataSource);

        this.targetClass = targetClass;
        this.inputConverter = null;
    }

    public RestListDataReader(RestDataSource dataSource, InputStreamIterableInputConverter<E> inputConverter) {
        super(dataSource);

        this.targetClass = null;
        this.inputConverter = inputConverter;
    }

    @Override
    public GluonObservableList<E> newGluonObservableList() {
        return new GluonObservableList<>();
    }

    @Override
    public Iterator<E> iterator() throws IOException {
        InputStream inputStream = dataSource.getInputStream();
        InputStreamIterableInputConverter<E> converter = inputConverter;
        if (converter == null) {
            String contentType = getContentType();
            if (contentType != null) {
                if (contentType.startsWith(CONTENT_TYPE_APPLICATION_JSON)) {
                    converter = new JsonIterableInputConverter<>(targetClass);
                } else {
                    throw new IllegalStateException("Could not determine IterableInputConverter based on Content-Type: " + contentType);
                }
            } else {
                converter = new JsonIterableInputConverter<>(targetClass);
            }
        }

        converter.setInputStream(inputStream);
        return converter.iterator();
    }
}
