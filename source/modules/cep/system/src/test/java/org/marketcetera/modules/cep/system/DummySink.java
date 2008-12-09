package org.marketcetera.modules.cep.system;

import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.SinkDataListener;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Dummy sink that keeps incoming data in a blocking queue so that we can wait on it synchronously
 *
 * @author toli@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
public class DummySink implements SinkDataListener {

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
