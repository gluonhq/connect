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
 * An ObjectDataRemover is an entity that has the ability to remove an object. The actual source and method that are
 * used for removing the object is completely up to the implementation. The most common scenario is that the remover
 * uses an {@link com.gluonhq.connect.source.OutputDataSource} that is able to remove the object that the source
 * points to.
 *
 * @param <T> the type of the object to remove
 */
public interface ObjectDataRemover<T> {

    /**
     * Removes the object and optionally returns an object. The actual method that removes the object is completely
     * left to the implementation. This method is called by {@link DataProvider#removeObject(GluonObservableObject, ObjectDataRemover)}
     * to initiate the actual removal process. If this method returns a non-empty Optional, the object that is contained
     * in the Optional will be set on the provided <code>observable</code> object. Otherwise, the value of the
     * <code>observable</code> object will be set to <code>null</code>. When the remove operation completed
     * successfully, the state of the provided <code>observable</code> will be set to
     * {@link com.gluonhq.connect.ConnectState#REMOVED}.
     *
     * @param observable the observable object that needs to be removed
     * @return an optional containing an object that will ultimately be set on the provided GluonObservableObject
     * @throws IOException when an exception occurred during the removal process
     */
    Optional<T> removeObject(GluonObservableObject<T> observable) throws IOException;

}
