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

import java.io.IOException;
import java.util.Optional;

/**
 * An ObjectDataWriter is an entity that has the ability to write an object. The actual source and method that are used
 * for writing the object is completely up to the implementation. The most common scenario is that the writer converts
 * the object into serialized form by using an {@link com.gluonhq.connect.converter.OutputConverter} which than is
 * passed into an {@link com.gluonhq.connect.source.OutputDataSource} that is able to write the serialized data a
 * certain source.
 *
 * @param <T> the type of the object to write
 */
public interface ObjectDataWriter<T> {

    /**
     * Provide an instance of a GluonObservableObject. This method is called by {@link DataProvider#storeObject(Object, ObjectDataWriter)}
     * to get a GluonObservableObject that will contain the actual object when the store operation has completed
     * successfully. Most implementations will just return an instance of GluonObservableObject itself. Note that it is
     * perfectly valid to return existing instances of GluonObservableObject.
     *
     * @return an instance of a GluonObservableObject
     */
    GluonObservableObject<T> newGluonObservableObject();

    /**
     * Writes the object and optionally returns one. The actual method that converts the object into serialized data and
     * the source where this data is written to is completely left to the implementation. This method is called by
     * {@link DataProvider#storeObject(Object, ObjectDataWriter)} to initiate the actual write process. The returned
     * object will be set on the final GluonObservableObject that is provided by this writer's
     * {@link #newGluonObservableObject()} method. The returned object can be the same as the provided object or it
     * can be a completely new object. An empty Optional can be returned as well, which is the same as returning the
     * provided object.
     *
     * @param object the object to write
     * @return the object that will ultimately be set on the GluonObservableObject
     * @throws IOException when an exception occurred during the write process
     */
    Optional<T> writeObject(T object) throws IOException;

}
