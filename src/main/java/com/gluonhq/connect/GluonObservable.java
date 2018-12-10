/*
 * Copyright (c) 2016, 2018 Gluon
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

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.EventHandler;
import javafx.event.EventTarget;

/**
 * This is the common interface for both GluonObservableObject and GluonObservableList. All operations on the
 * {@link com.gluonhq.connect.provider.DataProvider} class happen in an asynchronous background thread. This interface
 * can therefor be used to get information about the current state of the observable entity while the operation is
 * running as well as the final state of the entity when the operation has completed.
 */
public interface GluonObservable extends EventTarget {

    /**
     * Return the initialized state of this observable.
     *
     * @return true when the observable is initialized; false otherwise.
     */
    boolean isInitialized();

    /**
     * A boolean property that is true when the observable has been initialized successfully after its initial
     * retrieval.
     *
     * @return true when the observable is initialized; false otherwise.
     */
    ReadOnlyBooleanProperty initializedProperty();

    /**
     * Return the current state of this observable.
     *
     * @return the current state
     */
    ConnectState getState();

    /**
     * Holds the current state of this observable. This property can be used to listen
     * for changes in the synchronization process.
     *
     * @return the current state
     */
    ReadOnlyObjectProperty<ConnectState> stateProperty();

    /**
     * In case an exception occurred during processing, this method will
     * return the exception. Otherwise, <code>null</code> will be returned.
     *
     * @return the exception, if one has occurred; otherwise null.
     */
    Throwable getException();

    /**
     * Holds the exception that was thrown when a synchronization operation failed.
     *
     * @return the exception, if one has occurred; otherwise null.
     */
    ReadOnlyObjectProperty<Throwable> exceptionProperty();

    /**
     * The onReady event handler is called whenever the GluonObservable state
     * transitions to the READY state.
     *
     * @param value the event handler, can be null to clear it
     */
    void setOnReady(EventHandler<ConnectStateEvent> value);

    /**
     * The onRunning event handler is called whenever the GluonObservable state
     * transitions to the RUNNING state.
     *
     * @param value the event handler, can be null to clear it
     */
    void setOnRunning(EventHandler<ConnectStateEvent> value);

    /**
     * The onFailed event handler is called whenever the GluonObservable state
     * transitions to the FAILED state.
     *
     * @param value the event handler, can be null to clear it
     */
    void setOnFailed(EventHandler<ConnectStateEvent> value);

    /**
     * The onSucceeded event handler is called whenever the GluonObservable state
     * transitions to the SUCCEEDED state.
     *
     * @param value the event handler, can be null to clear it
     */
    void setOnSucceeded(EventHandler<ConnectStateEvent> value);

    /**
     * The onCancelled event handler is called whenever the GluonObservable state
     * transitions to the CANCELLED state.
     *
     * @param value the event handler, can be null to clear it
     */
    void setOnCancelled(EventHandler<ConnectStateEvent> value);

    /**
     * The onRemoved event handler is called whenever the GluonObservable state
     * transitions to the REMOVED state.
     *
     * @param value the event handler, can be null to clear it
     */
    void setOnRemoved(EventHandler<ConnectStateEvent> value);

}
