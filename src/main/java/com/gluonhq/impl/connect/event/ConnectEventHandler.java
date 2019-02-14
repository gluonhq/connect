package com.gluonhq.impl.connect.event;

import javafx.event.Event;
import javafx.event.EventHandler;

public final class ConnectEventHandler<T extends Event> {

    private EventProcessorRecord<T> firstRecord;
    private EventProcessorRecord<T> lastRecord;

    private EventHandler<? super T> eventHandler;

    public void setEventHandler(final EventHandler<? super T> eventHandler) {
        this.eventHandler = eventHandler;
    }

    public EventHandler<? super T> getEventHandler() {
        return eventHandler;
    }

    public void dispatchBubblingEvent(final Event event) {
        final T specificEvent = (T) event;

        EventProcessorRecord<T> record = firstRecord;
        while (record != null) {
            if (record.isDisconnected()) {
                remove(record);
            } else {
                record.handleBubblingEvent(specificEvent);
            }
            record = record.nextRecord;
        }

        if (eventHandler != null) {
            eventHandler.handle(specificEvent);
        }
    }

    public void dispatchCapturingEvent(final Event event) {
        final T specificEvent = (T) event;

        EventProcessorRecord<T> record = firstRecord;
        while (record != null) {
            if (record.isDisconnected()) {
                remove(record);
            } else {
                record.handleCapturingEvent(specificEvent);
            }
            record = record.nextRecord;
        }
    }

    private void remove(final EventProcessorRecord<T> record) {
        final EventProcessorRecord<T> prevRecord = record.prevRecord;
        final EventProcessorRecord<T> nextRecord = record.nextRecord;

        if (prevRecord != null) {
            prevRecord.nextRecord = nextRecord;
        } else {
            firstRecord = nextRecord;
        }

        if (nextRecord != null) {
            nextRecord.prevRecord = prevRecord;
        } else {
            lastRecord = prevRecord;
        }
    }

    private static abstract class EventProcessorRecord<T extends Event> {
        private EventProcessorRecord<T> nextRecord;
        private EventProcessorRecord<T> prevRecord;

        public abstract boolean stores(EventHandler<? super T> eventProcessor,
                                       boolean isFilter);

        public abstract void handleBubblingEvent(T event);

        public abstract void handleCapturingEvent(T event);

        public abstract boolean isDisconnected();
    }

}