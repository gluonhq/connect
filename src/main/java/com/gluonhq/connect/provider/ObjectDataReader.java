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

/**
 * An ObjectDataReader is an entity that has the ability to read an object. The actual source and method that are used
 * for reading the object is completely up to the implementation. The most common scenario is that the reader reads
 * its data using a {@link com.gluonhq.connect.source.InputDataSource} and converts the read bytes by using an
 * {@link com.gluonhq.connect.converter.InputConverter}.
 *
 * @param <T> the type of the object to read
 */
public interface ObjectDataReader<T> {

    /**
     * Provide an instance of a GluonObservableObject. This method is called by {@link DataProvider#retrieveObject(ObjectDataReader)}
     * to get a GluonObservableObject that will contain the actual object when the retrieve operation has completed
     * successfully. Most implementations will just return an instance of GluonObservableObject itself. Note that it is
     * perfectly valid to return existing instances of GluonObservableObject.
     *
     * @return an instance of a GluonObservableObject
     */
    GluonObservableObject<T> newGluonObservableObject();

    /**
     * Reads the object and returns it. The actual source of the read and the method that converts the read data into
     * an object instance is completely left to the implementation. This method is called by
     * {@link DataProvider#retrieveObject(ObjectDataReader)} to initiate the actual read process. The returned object
     * will be set on the GluonObservableObject that is returned in the {@link #newGluonObservableObject()} method.
     *
     * @return the read object
     * @throws IOException when an exception occurred during the read process
     */
    T readObject() throws IOException;

}
