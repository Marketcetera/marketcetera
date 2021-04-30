package org.marketcetera.eventbus.data.test.rpc;

import java.util.concurrent.atomic.AtomicLong;

import org.marketcetera.eventbus.data.event.SimpleDataEvent;

public class AbstractMockDataEvent
        extends SimpleDataEvent
{
    protected AbstractMockDataEvent()
    {
        setId(counter.incrementAndGet());
    }
    private static final AtomicLong counter = new AtomicLong(0);
}