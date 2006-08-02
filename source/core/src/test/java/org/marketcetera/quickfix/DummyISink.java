package org.marketcetera.quickfix;

import org.jcyclone.core.queue.ISink;
import org.jcyclone.core.queue.IElement;
import org.jcyclone.core.queue.SinkException;
import org.jcyclone.core.queue.ITransaction;
import org.marketcetera.core.ClassVersion;

import java.util.List;
import java.util.ArrayList;

/**
 * Dummy implememntation of a Sink - just stores all the incoming events
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class DummyISink implements ISink {
    public List<IElement> events = new ArrayList<IElement>();

    public long getNumEventsHandled() {
        return events.size();
    }

    public void enqueue(IElement element) throws SinkException {
        events.add(element);
    }

    public boolean enqueueLossy(IElement element) {
        events.add(element);
        return true;
    }

    public void enqueueMany(List elements) throws SinkException {
        events.addAll(elements);
    }

    public ITransaction enqueuePrepare(List elements) throws SinkException {
        return null;
    }

    public void enqueuePrepare(List elements, ITransaction txn) throws SinkException {
        throw new RuntimeException("enqueuePrepare not implemented");
    }

    public int size() {
        return events.size();
    }

    public void setCapacity(int newCapacity) {
        // noop
    }

    public int capacity() {
        return 37;
    }
}
