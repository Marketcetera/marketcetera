package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/* $License$ */
/**
 * A test sink data listener that receives data without blocking and enables
 * its clients to block when reading data received by the sink via
 * {@link #getNextData()}.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class BlockingSinkDataListener implements SinkDataListener {
    @Override
    public void receivedData(DataFlowID inFlowID, Object inData) {
        //Use add() instead of put() as we don't want this call to block
        mReceived.add(inData);
    }

    /**
     * Gets the next received data object. waits until the data object
     * is available.
     *
     * @return the next received data object.
     *
     * @throws InterruptedException if the thread was interrupted.
     */
    public Object getNextData() throws InterruptedException {
        //block until there's data available.
        return mReceived.take();
    }

    /**
     * The number of objects that have been received but not yet fetched.
     *
     * @return number of unfetched received objects.
     */
    public int size() {
        return mReceived.size();
    }

    private final BlockingQueue<Object> mReceived =
            new LinkedBlockingQueue<Object>();
}
