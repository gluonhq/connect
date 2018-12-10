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

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.util.Iterator;

/**
 * An IterableInputConverter that converts a JSON Array read from an InputStream into an Iterator that can be used to
 * iterate over a list of objects. The actual conversion from JSON to an object is handled by an instance of
 * {@link JsonConverter}.
 *
 * @param <E> the type of the object that the items in the JSON Array are converted into
 */
public class JsonIterableInputConverter<E> extends InputStreamIterableInputConverter<E> implements Iterator<E> {

    private final Class<E> targetClass;

    private JsonArray jsonArray;
    private int index;
    private JsonConverter<E> converter;

    /**
     * Construct a new instance of a JsonIterableInputConverter that is able to convert the data read from the
     * InputStream into objects of the specified <code>targetClass</code>.
     *
     * <p>The <code>targetClass</code> can be any of the types listed below. All other types will be converted from
     * JSON by using a {@link JsonConverter}.</p>
     *
     * <ul>
     *     <li>java.lang.Boolean: read from the JSON value as JsonValue.TRUE or JsonValue.FALSE</li>
     *     <li>java.lang.Byte: read from the JSON value as an int and converted into a byte</li>
     *     <li>java.lang.Double: read from the JSON value as a JSON number from which the double value is taken</li>
     *     <li>java.lang.Float: read from the JSON value as a JSON number from which the double value is taken that is converted to a float</li>
     *     <li>java.lang.Integer: read from the JSON value as an int</li>
     *     <li>java.lang.Long: read from the JSON value as a JSON number from which the long value is taken</li>
     *     <li>java.lang.Short: read from the JSON value as an int and converted into a short</li>
     *     <li>java.lang.String: read from the JSON value as a JSON String</li>
     *     <li>javax.json.JsonObject: the returned objects will be the actual JSON Object that is read from the JSON Array</li>
     * </ul>
     *
     * @param targetClass The class defining the objects being converted from JSON.
     */
    public JsonIterableInputConverter(Class<E> targetClass) {
        this.targetClass = targetClass;
    }

    /**
     * Returns the next element in the iteration. In this case, it will be an object that is converted from the next
     * available element in the JSON Array that was read from the InputStream.
     *
     * @return An object converted from the JSON Value that is taken from the next element in the JSON Array.
     */
    @Override
    public E next() {
        if (Boolean.class.isAssignableFrom(targetClass)) {
            return (E) (Boolean) jsonArray.getBoolean(index++);
        } else if (Byte.class.isAssignableFrom(targetClass)) {
            return (E) (Byte) ((Integer) jsonArray.getInt(index++)).byteValue();
        } else if (Double.class.isAssignableFrom(targetClass)) {
            return (E) (Double) jsonArray.getJsonNumber(index++).doubleValue();
        } else if (Float.class.isAssignableFrom(targetClass)) {
            return (E) (Float) ((Double) jsonArray.getJsonNumber(index++).doubleValue()).floatValue();
        } else if (Integer.class.isAssignableFrom(targetClass)) {
            return (E) (Integer) jsonArray.getInt(index++);
        } else if (Long.class.isAssignableFrom(targetClass)) {
            return (E) (Long) jsonArray.getJsonNumber(index++).longValue();
        } else if (Short.class.isAssignableFrom(targetClass)) {
            return (E) (Short) ((Integer) jsonArray.getInt(index++)).shortValue();
        } else if (String.class.isAssignableFrom(targetClass)) {
            return (E) jsonArray.getString(index++);
        } else {
            JsonObject jsonObject = jsonArray.getJsonObject(index++);
            if (JsonObject.class.isAssignableFrom(targetClass)) {
                return (E) jsonObject;
            } else {
                if (converter == null) {
                    converter = new JsonConverter<>(targetClass);
                }
                return converter.readFromJson(jsonObject);
            }
        }
    }

    /**
     * Returns <code>true</code> if the iteration has more elements, in this case if there are more elements to be
     * returned from the JSON Array that was read from the InputStream.
     *
     * @return <code>true</code> if there are more items available in the Iterator, <code>false</code> otherwise.
     */
    @Override
    public boolean hasNext() {
        return index < jsonArray.size();
    }

    /**
     * Returns an Iterator that loops over the items in the JSON Array that is read from the InputStream. This
     * implementation returns itself as the Iterator. Each element inside the JSON Array will be converted into the
     * correct object when the {@link #next} method is called.
     *
     * @return An Iterator that can be used to loop over the objects that are contained in the JSON Array that was read
     * from the InputStream.
     */
    @Override
    public Iterator<E> iterator() {
        index = 0;

        try (JsonReader reader = JsonUtil.createJsonReader(getInputStream())) {
            jsonArray = reader.readArray();
        }

        return this;
    }
}
