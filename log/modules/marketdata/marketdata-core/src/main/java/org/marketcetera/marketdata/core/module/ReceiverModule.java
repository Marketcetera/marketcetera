package org.marketcetera.marketdata.core.module;

import java.util.Map;

import org.marketcetera.core.Pair;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.publisher.PublisherEngine;
import org.marketcetera.module.*;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.Maps;

/* $License$ */

/**
 * Receives data flow data and publishes to the non-module world.
 *
 * <p>Module Features
 * <table>
 * <tr><th>Capabilities</th><td>Data Receiver</td></tr>
 * <tr><th>DataFlow Request Parameters</th><td>n/a</td></tr>
 * <tr><th>Stops data flows</th><td>no</td></tr>
 * <tr><th>Start Operation</th><td>none</td></tr>
 * <tr><th>Stop Operation</th><td>none</td></tr>
 * <tr><th>Management Interface</th><td>none</td></tr>
 * <tr><th>Factory</th><td>{@link ReceiverModuleFactory}</td></tr>
 * </table>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class ReceiverModule
        extends Module
        implements DataReceiver
{
    /**
     * Gets the <code>ReceiverModule</code> instance for the given instance name.
     *
     * @param inInstanceName a <code>String</code> value
     * @return a <code>ReceiverModule</code> value or <code>null</code>
     */
    public static ReceiverModule getModuleForInstanceName(String inInstanceName)
    {
        return runningInstances.get(inInstanceName);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataReceiver#receiveData(org.marketcetera.module.DataFlowID, java.lang.Object)
     */
    @Override
    public void receiveData(DataFlowID inFlowID,
                            Object inData)
            throws ReceiveDataException
    {
        SLF4JLoggerProxy.trace(this,
                               "Receiver module received {} for {}", //$NON-NLS-1$
                               inData,
                               inFlowID);
        publisher.publish(Pair.create(inFlowID,inData));
    }
    /**
     * Subscribes to received data objects.
     *
     * @param inSubscriber an <code>ISubscriber</code> value
     */
    public void subscribe(ISubscriber inSubscriber)
    {
        publisher.subscribe(inSubscriber);
    }
    /**
     * Unsubscribes from received data objects.
     *
     * @param inSubscriber an <code>ISubscriber</code> value
     */
    public void unsubscribe(ISubscriber inSubscriber)
    {
        publisher.unsubscribe(inSubscriber);
    }
    /**
     * Create a new ReceiverModule instance.
     */
    ReceiverModule(String inInstanceName)
    {
        super(new ModuleURN(ReceiverModuleFactory.PROVIDER_URN,
                            inInstanceName),
                            true);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStart()
     */
    @Override
    protected void preStart()
            throws ModuleException
    {
        runningInstances.put(getURN().instanceName(),
                             this);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStop()
     */
    @Override
    protected void preStop()
            throws ModuleException
    {
        runningInstances.remove(getURN().instanceName());
    }
    /**
     * publishes data to interested parties
     */
    private final PublisherEngine publisher = new PublisherEngine(true);
    /**
     * tracks active receiver module instances
     */
    private static final Map<String,ReceiverModule> runningInstances = Maps.newHashMap();
}
