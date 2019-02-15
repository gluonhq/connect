/*
 * Copyright (c) 2019, Gluon
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
package com.gluonhq.impl.connect.event;

import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.event.EventType;

import java.util.HashMap;
import java.util.Map;

/**
 * An {@code EventDispatcher} which handles {@link com.gluonhq.connect.ConnectStateEvent}'s
 * registration and when used in an event dispatch chain it forwards the received
 * events to the appropriate registered handlers.
 */
public class ConnectEventDispatcher<T extends Event> implements EventDispatcher {

    private final Map<EventType<T>, ConnectEventHandler<T>> eventHandlerMap;
    private final Object eventSource;

    public ConnectEventDispatcher(Object eventSource) {
        this.eventSource = eventSource;
        eventHandlerMap = new HashMap<>();
    }

    @Override
    public Event dispatchEvent(Event event, EventDispatchChain tail) {
        event = dispatchCapturingEvent((T) event);
        if (event.isConsumed()) {
            return null;
        }
        event = tail.dispatchEvent(event);
        if (event != null) {
            event = dispatchBubblingEvent((T) event);
            if (event.isConsumed()) {
                return null;
            }
        }
        return event;
    }

    /**
     * Sets the specified singleton handler. There can only be one such handler
     * specified at a time.
     *
     * @param eventType the event type to associate with the given eventHandler
     * @param eventHandler the handler to register, or null to unregister
     * @throws NullPointerException if the event type is null
     */
    public final void setEventHandler(final EventType<T> eventType, final EventHandler<? super T> eventHandler) {
        validateEventType(eventType);

        ConnectEventHandler<T> dialogEventHandler = eventHandlerMap.get(eventType);

        if (dialogEventHandler == null) {
            if (eventHandler == null) {
                return;
            }
            dialogEventHandler = new ConnectEventHandler<>();
            eventHandlerMap.put(eventType, dialogEventHandler);
        }

        dialogEventHandler.setEventHandler(eventHandler);
    }

    public final Event dispatchCapturingEvent(T event) {
        EventType<T> eventType = (EventType<T>) event.getEventType();
        do {
            event = dispatchCapturingEvent(eventType, event);
            eventType = (EventType<T>) eventType.getSuperType();
        } while (eventType != null);

        return event;
    }

    public final Event dispatchBubblingEvent(T event) {
        EventType<T> eventType = (EventType<T>) event.getEventType();
        do {
            event = dispatchBubblingEvent(eventType, event);
            eventType = (EventType<T>) eventType.getSuperType();
        } while (eventType != null);

        return event;
    }

    private T dispatchCapturingEvent(final EventType<T> handlerType, T event) {
        final ConnectEventHandler<T> compositeEventHandler = eventHandlerMap.get(handlerType);

        if (compositeEventHandler != null) {
            event = fixEventSource(event, eventSource);
            compositeEventHandler.dispatchCapturingEvent(event);
        }
        return event;
    }

    private T dispatchBubblingEvent(final EventType<T> handlerType, T event) {
        final ConnectEventHandler<? extends Event> compositeEventHandler = eventHandlerMap.get(handlerType);

        if (compositeEventHandler != null) {
            event = fixEventSource(event, eventSource);
            compositeEventHandler.dispatchBubblingEvent(event);
        }

        return event;
    }

    private T fixEventSource(final T event, final Object eventSource) {
        return (event.getSource() != eventSource) ? (T) event.copyFor(eventSource, event.getTarget()) : event;
    }

    private static void validateEventType(final EventType<?> eventType) {
        if (eventType == null) {
            throw new NullPointerException("Event type must not be null");
        }
    }
}
