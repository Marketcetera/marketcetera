package org.marketcetera.dataflow.service;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.dataflow.client.DataBroadcaster;
import org.marketcetera.dataflow.client.DataReceiver;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;

/* $License$ */

/**
 * Provides data flow services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class DataFlowServiceImpl
        implements DataFlowService,DataBroadcaster
{
    /* (non-Javadoc)
     * @see org.marketcetera.dataflow.client.DataPublisher#addDataReceiver(org.marketcetera.dataflow.client.DataReceiver)
     */
    @Override
    public void addDataReceiver(DataReceiver inReceiver)
    {
        dataReceivers.add(inReceiver);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dataflow.client.DataPublisher#removeDataReceiver(org.marketcetera.dataflow.client.DataReceiver)
     */
    @Override
    public void removeDataReceiver(DataReceiver inReceiver)
    {
        dataReceivers.remove(inReceiver);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dataflow.client.DataBroadcaster#receiveData(java.lang.Object)
     */
    @Override
    public void receiveData(Object inData)
    {
        for(DataReceiver dataReceiver : dataReceivers) {
            try {
                dataReceiver.receiveData(inData);
            } catch (Exception e) {
                PlatformServices.handleException(this,
                                                 "Publishing data",
                                                 e);
            }
        }
    }
    /**
     * Start and validate the service.
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.info(this,
                              "Data flow service started");
    }
    /**
     * holds data receivers
     */
    private final Set<DataReceiver> dataReceivers = Sets.newConcurrentHashSet();
}
