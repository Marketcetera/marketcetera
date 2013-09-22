package org.marketcetera.ors;

import org.marketcetera.client.*;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.ors.symbol.SymbolResolverServices;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id: ServiceImpl.java 16664 2013-08-23 23:06:00Z colin $")
public class DirectClientFactory
        implements ClientFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientFactory#getClient(org.marketcetera.client.ClientParameters, org.marketcetera.client.ClientLifecycleManager)
     */
    @Override
    public Client getClient(ClientParameters inClientParameters)
            throws ClientInitException, ConnectionException
    {
        return new DirectClient(inClientParameters,
                                reportHistoryServices,
                                symbolResolverServices);
    }
    /**
     * Get the reportHistoryServices value.
     *
     * @return a <code>ReportHistoryServices</code> value
     */
    public ReportHistoryServices getReportHistoryServices()
    {
        return reportHistoryServices;
    }
    /**
     * Sets the reportHistoryServices value.
     *
     * @param inReportHistoryServices a <code>ReportHistoryServices</code> value
     */
    public void setReportHistoryServices(ReportHistoryServices inReportHistoryServices)
    {
        reportHistoryServices = inReportHistoryServices;
    }
    /**
     * Get the symbolResolverServices value.
     *
     * @return a <code>SymbolResolverServices</code> value
     */
    public SymbolResolverServices getSymbolResolverServices()
    {
        return symbolResolverServices;
    }
    /**
     * Sets the symbolResolverServices value.
     *
     * @param inSymbolResolverServices a <code>SymbolResolverServices</code> value
     */
    public void setSymbolResolverServices(SymbolResolverServices inSymbolResolverServices)
    {
        symbolResolverServices = inSymbolResolverServices;
    }
    /**
     * 
     */
    private ReportHistoryServices reportHistoryServices;
    /**
     * 
     */
    private SymbolResolverServices symbolResolverServices;
}
