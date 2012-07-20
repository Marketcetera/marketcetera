package org.marketcetera.modules.async;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.Semaphore;

/* $License$ */
/**
 * A receiver module that blocks when receiving data. Every data delivery
 * blocks until a corresponding call to receive that data is made via
 * {@link #getNextData()}.
 * Additionally a {@link #getSemaphore()} instance is available to be
 * able to wait until the module about to receive data.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class BlockingReceiverModule extends Module implements DataReceiver {
    /**
     * Creates an instance.
     */
    BlockingReceiverModule() {
        super(BlockingModuleFactory.INSTANCE_URN, true);
    }

    @Override
    protected void preStart() throws ModuleException {
        //do nothing
    }

    @Override
    protected void preStop() throws ModuleException {
        //do nothing
    }

    @Override
    public void receiveData(DataFlowID inFlowID, Object inData)
            throws ReceiveDataException {
        try {
            mSemaphore.release();
            mData.put(inData);
        } catch (InterruptedException e) {
            throw new ReceiveDataException(e);
        }
    }

    /**
     * Get the next data item that is being supplied to the module via the
     * concurrent call to {@link #receiveData(org.marketcetera.module.DataFlowID, Object)}.
     * If a concurrent call to receiveData() is not executing, this call
     * blocks until receiveData() is called to deliver data.
     *
     * @return the next data item.
     *
     * @throws InterruptedException if the wait for receiving data
     * was interrupted.
     */
    Object getNextData() throws InterruptedException {
        return mData.take();
    }

    /**
     * The semaphore instance that is released right before data is received.
     *
     * @return the semaphore instance.
     */
    Semaphore getSemaphore() {
        return mSemaphore;
    }

    private final BlockingQueue<Object> mData = new SynchronousQueue<Object>();
    private final Semaphore mSemaphore = new Semaphore(0);
}
