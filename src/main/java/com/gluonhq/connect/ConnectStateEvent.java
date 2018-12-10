/*
 * Copyright (c) 2018 Gluon
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

import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventType;

public class ConnectStateEvent extends Event {

    /**
     * Common supertype for all worker state event types.
     */
    public static final EventType<ConnectStateEvent> ANY =
            new EventType<>(Event.ANY, "CONNECT_STATE");

    /**
     * This event occurs when the state of a GluonObservable implementation has
     * transitioned to the READY state.
     */
    public static final EventType<ConnectStateEvent> CONNECT_STATE_READY =
            new EventType<>(ConnectStateEvent.ANY, "CONNECT_STATE_READY");

    /**
     * This event occurs when the state of a GluonObservable implementation has
     * transitioned to the RUNNING state.
     */
    public static final EventType<ConnectStateEvent> CONNECT_STATE_RUNNING =
            new EventType<>(ConnectStateEvent.ANY, "CONNECT_STATE_RUNNING");

    /**
     * This event occurs when the state of a GluonObservable implementation has
     * transitioned to the FAILED state.
     */
    public static final EventType<ConnectStateEvent> CONNECT_STATE_FAILED =
            new EventType<>(ConnectStateEvent.ANY, "CONNECT_STATE_FAILED");

    /**
     * This event occurs when the state of a GluonObservable implementation has
     * transitioned to the SUCCEEDED state.
     */
    public static final EventType<ConnectStateEvent> CONNECT_STATE_SUCCEEDED =
            new EventType<>(ConnectStateEvent.ANY, "CONNECT_STATE_SUCCEEDED");

    /**
     * This event occurs when the state of a GluonObservable implementation has
     * transitioned to the CANCELLED state.
     */
    public static final EventType<ConnectStateEvent> CONNECT_STATE_CANCELLED =
            new EventType<>(ConnectStateEvent.ANY, "CONNECT_STATE_CANCELLED");

    /**
     * This event occurs when the state of a GluonObservable implementation has
     * transitioned to the REMOVED state.
     */
    public static final EventType<ConnectStateEvent> CONNECT_STATE_REMOVED =
            new EventType<>(ConnectStateEvent.ANY, "CONNECT_STATE_REMOVED");

    /**
     * Create a new ConnectStateEvent. Specify the gluon observable and the event type.
     *
     * @param gluonObservable The GluonObservable which is firing the event. The
     *               GluonObservable really should be an EventTarget, otherwise the
     *               EventTarget for the event will be null.
     * @param eventType The type of event. This should not be null.
     */
    public ConnectStateEvent(@NamedArg("gluonObservable") GluonObservable gluonObservable, @NamedArg("eventType") EventType<? extends ConnectStateEvent> eventType) {
        super(gluonObservable, gluonObservable, eventType);
    }

    @Override
    public GluonObservable getSource() {
        return (GluonObservable) super.getSource();
    }
}
