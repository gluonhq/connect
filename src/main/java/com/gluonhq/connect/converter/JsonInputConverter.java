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

import com.gluonhq.impl.connect.converter.JsonUtil;

import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 * An InputConverter that converts a JSON Object read from an InputStream into an object. The actual conversion from
 * JSON to an object is handled by an instance of {@link JsonConverter}.
 *
 * @param <T> the type of the object to convert the JSON Object into
 */
public class JsonInputConverter<T> extends InputStreamInputConverter<T> {

    private final JsonConverter<T> converter;

    /**
     * Construct a new instance of a JsonInputConverter that is able to convert the data read from the InputStream into
     * objects of the specified <code>targetClass</code>.
     *
     * @param targetClass The class defining the objects being converted from JSON.
     */
    public JsonInputConverter(Class<T> targetClass) {
        this.converter = new JsonConverter<>(targetClass);
    }

    /**
     * Converts a JSON Object that is read from the InputStream into an object and returns it. If the specified
     * <code>targetClass</code> in the constructor equals to JsonObject.class, then this method will just return the
     * read JSON object directly instead of running the conversion. Otherwise, a {@link JsonConverter} will be used to
     * convert the JSON Object into the final object to return.
     *
     * @return An object converted from the JSON Object that was read from the InputStream.
     */
    @Override
    public T read() {
        try (JsonReader reader = JsonUtil.createJsonReader(getInputStream())) {
            JsonObject jsonObject = reader.readObject();
            if (JsonObject.class.isAssignableFrom(converter.getTargetClass())) {
                return (T) jsonObject;
            } else {
                return converter.readFromJson(jsonObject);
            }
        }
    }
}
