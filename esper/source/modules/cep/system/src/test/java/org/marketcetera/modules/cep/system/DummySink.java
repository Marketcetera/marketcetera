package org.marketcetera.modules.cep.system;

import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.SinkDataListener;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * Dummy sink that keeps incoming data in a blocking queue so that we can wait on it synchronously
 *
 * @author admin
 * @version $Id$
 * @since $Release$
 */
public class DummySink implements SinkDataListener {
    private Semaphore sema;
    private Object terminator;

    @Override
    public void receivedData(DataFlowID inFlowID, Object inData) {
        //Use add() instead of put() as we don't want this call to block
        mReceived.add(inData);
    }

    public BlockingQueue<Object> getReceived() {
        return mReceived;
    }

    private BlockingQueue<Object> mReceived = new LinkedBlockingQueue<Object>();
}
