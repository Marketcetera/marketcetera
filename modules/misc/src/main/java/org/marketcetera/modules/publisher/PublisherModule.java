package org.marketcetera.modules.publisher;

import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.module.AbstractDataReemitterModule;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ReceiveDataException;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Publishes received data to a given publisher before optionally passing it on in the flow.
 *
 * <p>Module Features
 * <table>
 * <tr><th>Capabilities</th><td>Data Emitter, Data Receiver</td></tr>
 * <tr><th>Stops data flows</th><td>No</td></tr>
 * <tr><th>Start Operation</th><td>None</td></tr>
 * <tr><th>Stop Operation</th><td>None</td></tr>
 * <tr><th>Management Interface</th><td>None</td></tr>
 * <tr><th>MX Notification</th><td>None</td></tr>
 * <tr><th>Factory</th><td>{@link PublisherModuleFactory}</td></tr>
 * </table>
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PublisherModule
        extends AbstractDataReemitterModule
{
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataReceiver#receiveData(org.marketcetera.module.DataFlowID, java.lang.Object)
     */
    @Override
    public void receiveData(DataFlowID inFlowID,
                            Object inData)
            throws ReceiveDataException
    {
        SLF4JLoggerProxy.trace(this,
                               "{} received data: {} {}",
                               getURN(),
                               inFlowID,
                               inData);
        try {
            if(subscriber != null) {
                if(subscriber.isInteresting(inData)) {
                    subscriber.publishTo(inData);
                }
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
        }
        super.receiveData(inFlowID,
                          inData);
    }
    /**
     * Create a new PublisherModule instance.
     *
     * @param inModuleURN a <code>ModuleURN</code> value
     * @param inSubscriber an <code>ISubscriber</code> value
     */
    PublisherModule(ModuleURN inModuleURN,
                    ISubscriber inSubscriber)
    {
        super(inModuleURN,
              true);
        subscriber = inSubscriber;
    }
    /**
     * subscriber to which to publish data
     */
    private ISubscriber subscriber;
}
