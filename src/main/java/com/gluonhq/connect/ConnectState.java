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
package com.gluonhq.connect;

import com.gluonhq.connect.provider.DataProvider;
import com.gluonhq.connect.provider.ObjectDataRemover;

/**
 * The possible states a {@link GluonObservable} can be in while a {@link DataProvider} operation is in progress. All
 * observables begin in the <code>READY</code> state. When the operation is started, the observable will enter the
 * <code>RUNNING</code> state. Finally, depending on the operation outcome, the observable will always enter one of the
 * following states: <code>SUCCEEDED</code>, <code>FAILED</code>, <code>REMOVED</code> or <code>CANCELLED</code>. When
 * an exception was thrown during the operation, it will enter the <code>FAILED</code> state. When the observable is a
 * {@link GluonObservableObject} and it is passed in as a parameter to the {@link DataProvider#removeObject(GluonObservableObject, ObjectDataRemover) remove method},
 * then the object will enter the <code>REMOVED</code> state when the remove operation succeeded successfully. When the
 * operation was cancelled, the state of the observable will be changed to <code>CANCELLED</code>. Signaling the
 * cancellation of an operation is done by throwing a {@link java.util.concurrent.CancellationException}. In all other
 * cases, the state will become <code>SUCCEEDED</code>.
 */
public enum ConnectState {
    /**
     * This is the initial state of a new GluonObservable object. It will remain
     * in the <code>READY</code> state until the first synchronization operation is started.
     */
    READY,
    /**
     * A GluonObservable object in the <code>RUNNING</code> state means that a
     * synchronization operation is currently in progress.
     */
    RUNNING,
    /**
     * A GluonObservable object in the <code>FAILED</code> state signals that the last
     * executed synchronization operation failed. You can check the {@link GluonObservable#getException() exception}
     * to find out more about the reason of the failure.
     */
    FAILED,
    /**
     * A GluonObservable object in the <code>SUCCEEDED</code> state signals that the last
     * executed synchronization operation completed successfully.
     */
    SUCCEEDED,
    /**
     * A GluonObservable object in the <code>CANCELLED</code> state signals that the last
     * executed synchronization operation was cancelled. Cancellation is most likely caused
     * by a user that cancels an action that is required to proceed with the operation. For
     * example, when an operation requires an authenticated user, that operation is cancelled
     * when the user aborts the authentication process.
     */
    CANCELLED,
    /**
     * A GluonObservable object in the <code>REMOVED</code> state signals that it is
     * successfully removed after executing the {@link DataProvider#removeObject(GluonObservableObject, ObjectDataRemover)} remove operation}.
     * The <code>REMOVED</code> state is only applicable to {@link GluonObservableObject} because
     * a {@link GluonObservableList} can not be removed.
     */
    REMOVED
}
