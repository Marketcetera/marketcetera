package org.marketcetera.brokers.service;

import org.marketcetera.cluster.AbstractCallableClusterTask;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.fix.FixSessionSequenceNumbers;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.Session;

/**
 * Collect session sequence numbers from the appropriate cluster member.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: GetSessionAttributesTask.java 17796 2018-11-20 18:47:57Z colin $
 * @since $Release$
 */
public class GetSessionSequenceNumbersTask
        extends AbstractCallableClusterTask<FixSessionSequenceNumbers>
{
    /* (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public FixSessionSequenceNumbers call()
            throws Exception
    {
        quickfix.Session session = Session.lookupSession(sessionId);
        ClusterData clusterData = getClusterService().getInstanceData();
        if(session == null) {
            SLF4JLoggerProxy.debug(this,
                                   "{} is not present on {}",
                                   session,
                                   clusterData);
            return null;
        } else {
            FixSessionSequenceNumbers result = new FixSessionSequenceNumbers(session);
            SLF4JLoggerProxy.debug(this,
                                   "{} is present on {}, returning {}",
                                   session,
                                   clusterData,
                                   result);
            return result;
        }
    }
    /**
     * Create a new StopSessionTask instance.
     *
     * @param inSessionId a <code>quickfix.SessionID</code> value
     */
    public GetSessionSequenceNumbersTask(quickfix.SessionID inSessionId)
    {
        sessionId = inSessionId;
    }
    /**
     * session id value
     */
    private quickfix.SessionID sessionId;
    private static final long serialVersionUID = -5835812434575796653L;
}
