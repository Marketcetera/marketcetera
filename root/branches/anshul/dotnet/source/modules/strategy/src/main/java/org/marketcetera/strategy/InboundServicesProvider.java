package org.marketcetera.strategy;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.marketcetera.client.ConnectionException;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.trade.MSymbol;

/* $License$ */

/**
 * Provides data needed for inputs to {@link Strategy} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public interface InboundServicesProvider
{
    /**
     * Returns the list of brokers known to the system.
     *
     * @return a <code>List&lt;BrokerStatus&gt;</code> value
     * @throws ConnectionException if the information could not be retrieved
     */
    public List<BrokerStatus> getBrokers()
        throws ConnectionException;
    /**
     * Gets the position in the given security at the given point in time.
     *
     * @param inDate a <code>Date</code> value
     * @param inSymbol a <code>MSymbol</code> value
     * @return a <code>BigDecimal</code> value
     * @throws ConnectionException if the information could not be retrieved 
     */
    public BigDecimal getPositionAsOf(Date inDate,
                                      MSymbol inSymbol)
        throws ConnectionException;
}
