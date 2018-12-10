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

import com.gluonhq.connect.GluonObservableList;

import java.io.IOException;
import java.util.Iterator;

/**
 * A ListDataReader is an entity that has the ability to read a list of objects. The actual source and method that
 * are used for reading the objects is completely up to the implementation. Typically, a the reader will read its
 * data by using an {@link com.gluonhq.connect.source.InputDataSource} and converts the read bytes into objects by
 * using an {@link com.gluonhq.connect.converter.IterableInputConverter}.
 *
 * @param <E> the type of the objects contained in the list to read
 */
public interface ListDataReader<E> {

    /**
     * Provide an instance of a GluonObservableList. This method will be called by {@link DataProvider#retrieveList(ListDataReader)}
     * to get a GluonObservableList that can be populated. Most implementations will just return an instance of
     * GluonObservableList itself. Note that it is perfectly valid to return existing instances of
     * GluonObservableList.
     *
     * @return an instance of GluonObservableList
     */
    GluonObservableList<E> newGluonObservableList();

    /**
     * Returns an iterator that is able to iterate over the read objects from the list. This method will be
     * called by {@link DataProvider#retrieveList(ListDataReader)} to initiate the read process. The returned
     * iterator will be used to populate the GluonObservableList that is returned in the {@link #newGluonObservableList()}
     * method.
     *
     * @return an iterator that is able to read over the objects from the list
     * @throws IOException when something went wrong during the process of reading the objects
     */
    Iterator<E> iterator() throws IOException;

}
