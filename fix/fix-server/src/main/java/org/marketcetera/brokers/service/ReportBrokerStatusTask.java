package org.marketcetera.brokers.service;

import org.marketcetera.brokers.BrokerConstants;
import org.marketcetera.brokers.ClusteredBrokerStatus;
import org.marketcetera.cluster.RunnableClusterTask;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Reports the a specified broker status from each cluster member.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ReportBrokerStatusTask
        extends RunnableClusterTask
{
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        ClusteredBrokerStatus brokerStatus = new ClusteredBrokerStatus(session,
                                                                       getClusterService().getInstanceData(),
                                                                       status,
                                                                       false);
        if(status == FixSessionStatus.DELETED) {
            removeBrokerStatus(brokerStatus);
        } else {
            brokerService.reportBrokerStatus(brokerStatus);
        }
    }
    /**
     * Create a new ReportBrokerStatusTask instance.
     *
     * @param inSession a <code>FixSession</code> value
     * @param inStatus a <code>FixSessionStatus</code> value
     */
    public ReportBrokerStatusTask(FixSession inSession,
                                  FixSessionStatus inStatus)
    {
        session = inSession;
        status = inStatus;
    }
    /**
     * Remove broker status for the given session.
     */
    private void removeBrokerStatus(ClusteredBrokerStatus brokerStatus)
    {
        SLF4JLoggerProxy.trace(this,
                               "Removing status for {}",
                               session);
        try {
            getClusterService().removeAttribute(BrokerConstants.brokerStatusPrefix+brokerStatus.getId()+brokerStatus.getHost());
        } catch (NullPointerException ignored) {
            // these can happen on shutdown and can be safely ignored
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "Unable to remove broker status");
        }
    }
    /**
     * cluster-local broker service value
     */
    @Autowired
    private transient BrokerService brokerService;
    /**
     * fix session to be enabled
     */
    private final FixSession session;
    /**
     * status to report
     */
    private final FixSessionStatus status;
    private static final long serialVersionUID = 181147680348143737L;
}