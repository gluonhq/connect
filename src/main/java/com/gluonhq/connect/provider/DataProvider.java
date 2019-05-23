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
package com.gluonhq.connect.provider;

import com.gluonhq.connect.ConnectState;
import com.gluonhq.connect.GluonObservableList;
import com.gluonhq.connect.GluonObservableObject;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The DataProvider is the root entry to obtain instances of {@link GluonObservableObject} and
 * {@link GluonObservableList}. All operations on the DataProvider happen in an asynchronous background thread. You can
 * use the properties on the {@link com.gluonhq.connect.GluonObservable} entity to get information about the progress
 * of the operation.
 */
public class DataProvider {

    private static final Logger LOG = Logger.getLogger(DataProvider.class.getName());

    private static final AtomicInteger THREAD_NUMBER = new AtomicInteger(0);
    private static ExecutorService executorService = Executors.newFixedThreadPool(5, runnable -> {
        Thread thread = Executors.defaultThreadFactory().newThread(runnable);
        thread.setName("DataProviderThread-" + THREAD_NUMBER.getAndIncrement());
        thread.setDaemon(true);
        return thread;
    });

    /**
     * Writes the object by using the specified ObjectDataWriter. A GluonObservableObject is returned,
     * that will contain the written version of the provided object when the write operation completed
     * successfully.
     *
     * @param object the object to store
     * @param writer the writer to use for storing the object
     * @param <T> the type of the object to store
     * @return an instance of GluonObservableObject that will hold the written version of the provided object upon
     * successful completion of the write operation
     */
    public static <T> GluonObservableObject<T> storeObject(T object, ObjectDataWriter<T> writer) {
        GluonObservableObject<T> observable = writer.newGluonObservableObject();

        final StackTraceElement[] callingStack = LOG.isLoggable(Level.FINE) ? Thread.currentThread().getStackTrace() : null;
        executorService.execute(() -> {
            try {
                Optional<T> toSet = writer.writeObject(object);

                if (!observable.isInitialized()) {
                    Platform.runLater(() -> {
                        observable.set(toSet.orElse(object));
                        ((SimpleBooleanProperty) observable.initializedProperty()).set(true);
                        observable.setState(ConnectState.SUCCEEDED);
                    });
                } else {
                    Platform.runLater(() -> {
                        observable.set(toSet.orElse(object));
                        observable.setState(ConnectState.SUCCEEDED);
                    });
                }
            } catch (CancellationException ex) {
                Platform.runLater(() -> {
                    observable.setState(ConnectState.CANCELLED);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    observable.setException(callingStack != null ? DataProvider.generateFullException(callingStack, ex) : ex);
                    observable.setState(ConnectState.FAILED);
                });
            }
        });
        return observable;
    }

    /**
     * Retrieves an object using the specified ObjectDataReader. A GluonObservableObject is returned,
     * that will contain the object when the read operation completed successfully.
     *
     * @param reader the reader to use for retrieving the object
     * @param <T> the type of the object to retrieve
     * @return an instance of GluonObservableObject that will hold the retrieved object upon successful completion
     * of the read operation
     */
    public static <T> GluonObservableObject<T> retrieveObject(ObjectDataReader<T> reader) {
        GluonObservableObject<T> observable = reader.newGluonObservableObject();

        Platform.runLater(() -> observable.setState(ConnectState.RUNNING));

        final StackTraceElement[] callingStack = LOG.isLoggable(Level.FINE) ? Thread.currentThread().getStackTrace() : null;
        executorService.execute(() -> {
            try {
                T t = reader.readObject();

                if (!observable.isInitialized()) {
                    Platform.runLater(() -> {
                        observable.set(t);
                        ((SimpleBooleanProperty) observable.initializedProperty()).set(true);
                        observable.setState(ConnectState.SUCCEEDED);
                    });
                } else {
                    Platform.runLater(() -> {
                        observable.set(t);
                        observable.setState(ConnectState.SUCCEEDED);
                    });
                }
            } catch (CancellationException ex) {
                Platform.runLater(() -> {
                    observable.setState(ConnectState.CANCELLED);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    observable.setException(callingStack != null ? DataProvider.generateFullException(callingStack, ex) : ex);
                    observable.setState(ConnectState.FAILED);
                });
            }
        });
        return observable;
    }

    /**
     * Remove the provided GluonObservableObject using the specified ObjectDataRemover. The state of the provided
     * observable will be updated to {@link ConnectState#REMOVED} and the contained object will be set to
     * <code>null</code> when the remove operation completed successfully.
     *
     * @param observable the observable to remove
     * @param remover the remover to use for removing the object
     * @param <T> the type of the object that is contained in the GluonObservableObject
     */
    public static <T> void removeObject(GluonObservableObject<T> observable, ObjectDataRemover<T> remover) {
        Platform.runLater(() -> observable.setState(ConnectState.RUNNING));

        final StackTraceElement[] callingStack = LOG.isLoggable(Level.FINE) ? Thread.currentThread().getStackTrace() : null;
        executorService.execute(() -> {
            try {
                Optional<T> t = remover.removeObject(observable);

                Platform.runLater(() -> {
                    observable.set(t.orElse(null));
                    ((SimpleObjectProperty<Throwable>) observable.exceptionProperty()).set(null);
                    observable.setState(ConnectState.REMOVED);
                });
            } catch (CancellationException ex) {
                Platform.runLater(() -> {
                    observable.setState(ConnectState.CANCELLED);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    observable.setException(callingStack != null ? DataProvider.generateFullException(callingStack, ex) : ex);
                    observable.setState(ConnectState.FAILED);
                });
            }
        });
    }

    /**
     * Retrieves a list using the specified ListDataReader. A GluonObservableList is returned, containing all the items
     * that exist in the list. The returned list can be used immediately: you can manipulate its items or assign it to
     * a JavaFX ListView for instance.
     *
     * @param reader the reader to use for retrieving the list
     * @param <E> the type of the objects inside the list
     * @return an instance of GluonObservableList that will hold the items contained in the list upon successful
     * completion of the read operation
     */
    public static <E> GluonObservableList<E> retrieveList(ListDataReader<E> reader) {
        GluonObservableList<E> observable = reader.newGluonObservableList();

        Platform.runLater(() -> observable.setState(ConnectState.RUNNING));

        final StackTraceElement[] callingStack = LOG.isLoggable(Level.FINE) ? Thread.currentThread().getStackTrace() : null;
        executorService.execute(() -> {
            try {
                for (Iterator<E> it = reader.iterator(); it.hasNext();) {
                    E e = it.next();
                    if (e != null) {
                        Platform.runLater(() -> observable.add(e));
                    }
                }

                if (!observable.isInitialized()) {
                    Platform.runLater(() -> {
                        ((SimpleBooleanProperty) observable.initializedProperty()).set(true);
                        observable.setState(ConnectState.SUCCEEDED);
                    });
                } else {
                    Platform.runLater(() -> {
                        observable.setState(ConnectState.SUCCEEDED);
                    });
                }
            } catch (CancellationException ex) {
                Platform.runLater(() -> {
                    observable.setState(ConnectState.CANCELLED);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    observable.setException(callingStack != null ? DataProvider.generateFullException(callingStack, ex) : ex);
                    observable.setState(ConnectState.FAILED);
                });
            }
        });
        return observable;
    }

    /**
     * When the logging level of the LOG instance is set to fine or lower, the stack of the thread that called the
     * DataProvider method will be added to the Exception and set as the exception property of the GluonObservable
     * object. When the log level is higher, only the Exception that was thrown will be set.
     *
     * @param callingStack the stack of the thread that called the DataProvider method at the time the method is called
     * @param cause the actual Exception that was thrown inside one of the DataProvider threads
     * @return an Exception that has the provided <code>callingStack</code> as it's stack trace, and the provided
     * <code>cause</code> as the cause of the Exception.
     */
    private static Exception generateFullException(StackTraceElement[] callingStack, Exception cause) {
        Exception exception = new Exception(cause);
        exception.setStackTrace(callingStack);
        return exception;
    }

}
