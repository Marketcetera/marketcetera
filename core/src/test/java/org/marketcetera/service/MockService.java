package org.marketcetera.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MockService
        implements Service
{
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return running.get();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public void start()
    {
        if(isRunning()) {
            stop();
        }
        clear();
        running.set(true);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public void stop()
    {
        if(!isRunning()) {
            return;
        }
        running.set(false);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.service.Service#accept(java.io.Serializable)
     */
    @Override
    public void accept(Serializable inData)
    {
        dataReceived.add(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.service.Service#setDataEmitter(org.marketcetera.service.ServiceDataEmitter)
     */
    @Override
    public void setDataEmitter(ServiceDataEmitter inServiceDataEmitter)
    {
        serviceDataEmitter = inServiceDataEmitter;
    }
    public List<Serializable> getReceivedData()
    {
        return dataReceived;
    }
    /**
     * Get the dataReceived value.
     *
     * @return a <code>List&lt;Serializable&gt;</code> value
     */
    public List<Serializable> getDataReceived()
    {
        return dataReceived;
    }
    /**
     * Get the dataEmitted value.
     *
     * @return a <code>List&lt;Serializable&gt;</code> value
     */
    public List<Serializable> getDataEmitted()
    {
        return dataEmitted;
    }
    /**
     * 
     *
     *
     */
    public void clear()
    {
        dataReceived.clear();
        dataEmitted.clear();
    }
    private ServiceDataEmitter serviceDataEmitter;
    private final List<Serializable> dataReceived = new ArrayList<Serializable>();
    private final List<Serializable> dataEmitted = new ArrayList<Serializable>();
    private final AtomicBoolean running = new AtomicBoolean(false);
}
