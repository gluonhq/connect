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

import com.gluonhq.connect.provider.DataProvider;
import com.gluonhq.connect.provider.ListDataReader;
import com.gluonhq.impl.connect.EventHelper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;

import static com.gluonhq.connect.ConnectState.CANCELLED;
import static com.gluonhq.connect.ConnectState.READY;
import static com.gluonhq.connect.ConnectStateEvent.*;

/**
 * A GluonObservableList is an ObservableList that is linked with a data source.
 *
 * @param <E> the type of the elements inside this list
 * @see DataProvider#retrieveList(ListDataReader)
 */
public class GluonObservableList<E> extends ModifiableObservableListBase<E> implements GluonObservable, ObservableList<E> {

    private final BooleanProperty initialized = new SimpleBooleanProperty(this, "initialized", false);
    private final ObjectProperty<ConnectState> state = new SimpleObjectProperty<>(this, "state", READY);
    private final ObjectProperty<Throwable> exception = new SimpleObjectProperty<>(this, "exception");

    private final ObservableList<E> backing = FXCollections.observableArrayList();
    private final SortedList<E> backingSorted = new SortedList<>(backing);

    @Override
    public boolean isInitialized() {
        return initialized.get();
    }

    @Override
    public ReadOnlyBooleanProperty initializedProperty() {
        return initialized;
    }

    @Override
    public ConnectState getState() {
        return state.get();
    }

    public void setState(ConnectState state) {
        final ConnectState s = getState();
        if (s != CANCELLED) {
            this.state.set(state);

            // Invoke the event handlers
            switch (this.state.get()) {
                case CANCELLED:
                    fireEvent(new ConnectStateEvent(this, CONNECT_STATE_CANCELLED));
                    break;
                case FAILED:
                    fireEvent(new ConnectStateEvent(this, CONNECT_STATE_FAILED));
                    break;
                case READY:
                    // This event can never meaningfully occur, because the
                    // GluonObservable begins life as ready and can never go back to it!
                    break;
                case RUNNING:
                    fireEvent(new ConnectStateEvent(this, CONNECT_STATE_RUNNING));
                    break;
                case REMOVED:
                    fireEvent(new ConnectStateEvent(this, CONNECT_STATE_REMOVED));
                    break;
                case SUCCEEDED:
                    fireEvent(new ConnectStateEvent(this, CONNECT_STATE_SUCCEEDED));
                    break;
                default:
                    throw new AssertionError("Should be unreachable");
            }
        }
    }

    @Override
    public ReadOnlyObjectProperty<ConnectState> stateProperty() {
        return state;
    }

    @Override
    public Throwable getException() {
        return exception.get();
    }

    public void setException(Throwable exception) {
        this.exception.set(exception);
    }

    @Override
    public ReadOnlyObjectProperty<Throwable> exceptionProperty() {
        return exception;
    }

    @Override
    public void setOnReady(EventHandler<ConnectStateEvent> value) {
        getEventHelper().setOnReady(value);
    }

    @Override
    public void setOnRunning(EventHandler<ConnectStateEvent> value) {
        getEventHelper().setOnRunning(value);
    }

    @Override
    public void setOnFailed(EventHandler<ConnectStateEvent> value) {
        getEventHelper().setOnFailed(value);
    }

    @Override
    public void setOnSucceeded(EventHandler<ConnectStateEvent> value) {
        getEventHelper().setOnSucceeded(value);
    }

    @Override
    public void setOnCancelled(EventHandler<ConnectStateEvent> value) {
        getEventHelper().setOnCancelled(value);
    }

    @Override
    public void setOnRemoved(EventHandler<ConnectStateEvent> value) {
        getEventHelper().setOnRemoved(value);
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return
     * @return the element at the specified position in this list
     */
    @Override
    public E get(int index) {
        return backingSorted.get(index);
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    @Override
    public int size() {
        return backing.size();
    }

    /**
     * Adds the {@code element} to the List at the position of {@code index}.
     *
     * <p>For the description of possible exceptions, please refer to the documentation
     * of {@link #add(java.lang.Object) } method.
     *
     * @param index the position where to add the element
     * @param element the element that will be added

     * @throws ClassCastException if the type of the specified element is
     * incompatible with this list
     * @throws NullPointerException if the specified arguments contain one or
     * more null elements
     * @throws IllegalArgumentException if some property of this element
     * prevents it from being added to this list
     * @throws IndexOutOfBoundsException if the index is out of range
     *         {@code (index < 0 || index > size())}
     */
    @Override
    protected void doAdd(int index, E element) {
        backing.add(index, element);
    }

    /**
     * Sets the {@code element} in the List at the position of {@code index}.
     *
     * <p>For the description of possible exceptions, please refer to the documentation
     * of {@link #set(int, java.lang.Object) } method.
     *
     * @param index the position where to set the element
     * @param element the element that will be set at the specified position
     * @return the old element at the specified position
     *
     * @throws ClassCastException if the type of the specified element is
     * incompatible with this list
     * @throws NullPointerException if the specified arguments contain one or
     * more null elements
     * @throws IllegalArgumentException if some property of this element
     * prevents it from being added to this list
     * @throws IndexOutOfBoundsException if the index is out of range
     *         {@code (index < 0 || index >= size())}
     */
    @Override
    protected E doSet(int index, E element) {
        return backing.set(index, element);
    }

    /**
     * Removes the element at position of {@code index}.
     *
     * @param index the index of the removed element
     * @return the removed element
     *
     * @throws IndexOutOfBoundsException if the index is out of range
     *         {@code (index < 0 || index >= size())}
     */
    @Override
    protected E doRemove(int index) {
        return backing.remove(index);
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        return getEventHelper().buildEventDispatchChain(tail);
    }

    private void fireEvent(ConnectStateEvent event) {
        getEventHelper().fireEvent(event);
    }

    private EventHelper eventHelper = null;
    private EventHelper getEventHelper() {
        if (eventHelper == null) {
            eventHelper = new EventHelper(this);
        }
        return eventHelper;
    }
}
