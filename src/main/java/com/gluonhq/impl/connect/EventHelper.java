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
package com.gluonhq.impl.connect;

import com.gluonhq.connect.ConnectStateEvent;
import com.gluonhq.impl.connect.event.ConnectEventDispatcher;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;

import static com.gluonhq.connect.ConnectStateEvent.*;

public class EventHelper {

    private final EventTarget target;

    private final ObjectProperty<EventHandler<ConnectStateEvent>> onReady;
    final ObjectProperty<EventHandler<ConnectStateEvent>> onReadyProperty() { return onReady; }
    final EventHandler<ConnectStateEvent> getOnReady() { return onReady.get(); }
    public final void setOnReady(EventHandler<ConnectStateEvent> value) { onReady.set(value); }

    private final ObjectProperty<EventHandler<ConnectStateEvent>> onRunning;
    final ObjectProperty<EventHandler<ConnectStateEvent>> onRunningProperty() { return onRunning; }
    final EventHandler<ConnectStateEvent> getOnRunning() { return onRunning.get(); }
    public final void setOnRunning(EventHandler<ConnectStateEvent> value) { onRunning.set(value); }

    private final ObjectProperty<EventHandler<ConnectStateEvent>> onFailed;
    final ObjectProperty<EventHandler<ConnectStateEvent>> onFailedProperty() { return onFailed; }
    final EventHandler<ConnectStateEvent> getOnFailed() { return onFailed.get(); }
    public final void setOnFailed(EventHandler<ConnectStateEvent> value) { onFailed.set(value); }

    private final ObjectProperty<EventHandler<ConnectStateEvent>> onSucceeded;
    final ObjectProperty<EventHandler<ConnectStateEvent>> onSucceededProperty() { return onSucceeded; }
    final EventHandler<ConnectStateEvent> getOnSucceeded() { return onSucceeded.get(); }
    public final void setOnSucceeded(EventHandler<ConnectStateEvent> value) { onSucceeded.set(value); }

    private final ObjectProperty<EventHandler<ConnectStateEvent>> onCancelled;
    final ObjectProperty<EventHandler<ConnectStateEvent>> onCancelledProperty() { return onCancelled; }
    final EventHandler<ConnectStateEvent> getOnCancelled() { return onCancelled.get(); }
    public final void setOnCancelled(EventHandler<ConnectStateEvent> value) { onCancelled.set(value); }

    private final ObjectProperty<EventHandler<ConnectStateEvent>> onRemoved;
    final ObjectProperty<EventHandler<ConnectStateEvent>> onRemovedProperty() { return onRemoved; }
    final EventHandler<ConnectStateEvent> getOnRemoved() { return onRemoved.get(); }
    public final void setOnRemoved(EventHandler<ConnectStateEvent> value) { onRemoved.set(value); }

    private ConnectEventDispatcher<ConnectStateEvent> internalEventDispatcher;

    public EventHelper(EventTarget bean) {
        this.target = bean;
        onReady = new SimpleObjectProperty<>(bean, "onReady") {
            @Override
            protected void invalidated() {
                EventHandler<ConnectStateEvent> handler = get();
                setEventHandler(CONNECT_STATE_READY, handler);
            }
        };
        onRunning = new SimpleObjectProperty<>(bean, "onRunning") {
            @Override
            protected void invalidated() {
                EventHandler<ConnectStateEvent> handler = get();
                setEventHandler(CONNECT_STATE_RUNNING, handler);
            }
        };
        onFailed = new SimpleObjectProperty<>(bean, "onFailed") {
            @Override
            protected void invalidated() {
                EventHandler<ConnectStateEvent> handler = get();
                setEventHandler(CONNECT_STATE_FAILED, handler);
            }
        };
        onSucceeded = new SimpleObjectProperty<>(bean, "onSucceeded") {
            @Override
            protected void invalidated() {
                EventHandler<ConnectStateEvent> handler = get();
                setEventHandler(CONNECT_STATE_SUCCEEDED, handler);
            }
        };
        onCancelled = new SimpleObjectProperty<>(bean, "onCancelled") {
            @Override
            protected void invalidated() {
                EventHandler<ConnectStateEvent> handler = get();
                setEventHandler(CONNECT_STATE_CANCELLED, handler);
            }
        };
        onRemoved = new SimpleObjectProperty<>(bean, "onRemoved") {
            @Override
            protected void invalidated() {
                EventHandler<ConnectStateEvent> handler = get();
                setEventHandler(CONNECT_STATE_REMOVED, handler);
            }
        };
    }

    /**
     * Sets the handler to use for this event type. There can only be one such
     * handler specified at a time. This handler is guaranteed to be called
     * first. This is used for registering the user-defined onFoo event
     * handlers.
     *
     * @param eventType the event type to associate with the given eventHandler
     * @param eventHandler the handler to register, or null to unregister
     */
    private void setEventHandler(
            final EventType<ConnectStateEvent> eventType,
            final EventHandler<ConnectStateEvent> eventHandler) {
        getInternalEventDispatcher()
                .setEventHandler(eventType, eventHandler);
    }

    private ConnectEventDispatcher<ConnectStateEvent> getInternalEventDispatcher() {
        if (internalEventDispatcher == null) {
            internalEventDispatcher = new ConnectEventDispatcher<>(target);
        }
        return internalEventDispatcher;
    }

    /**
     * Fires the specified event. Any event filter encountered will
     * be notified and can consume the event. If not consumed by the filters,
     * the event handlers on this task are notified. If these don't consume the
     * event either, then all event handlers are called and can consume the
     * event.
     * <p>
     * This method must be called on the FX user thread.
     *
     * @param event the event to fire
     */
    public void fireEvent(Event event) {
        Event.fireEvent(target, event);
    }

    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        return internalEventDispatcher == null ? tail : tail.append(getInternalEventDispatcher());
    }
}
