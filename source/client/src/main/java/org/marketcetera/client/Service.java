package org.marketcetera.client;

import java.math.BigDecimal;
import java.util.Date;
import javax.jws.WebService;

import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.core.MSymbol;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.marketcetera.util.ws.stateful.ServiceBase;
import org.marketcetera.util.ws.wrappers.RemoteException;

/**
 * The application's web services.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@WebService
@ClassVersion("$Id$")
public interface Service
    extends ServiceBase
{
    /**
     * Returns the server's broker status to the client with the
     * given context.
     *
     * @param context The context.
     *
     * @return The status.
     *
     * @throws RemoteException Thrown if the operation cannot be
     * completed.
     */

    BrokersStatus getBrokersStatus
        (ClientContext context)
        throws RemoteException;

    /**
     * Returns all the reports (execution report and order cancel
     * rejects) generated and received by the server since the
     * supplied date.
     *
     * @param date The date, in UTC.
     *
     * @return The reports.
     *
     * @throws RemoteException Thrown if the operation cannot be
     * completed.
     */

    ReportBaseImpl[] getReportsSince
        (ClientContext context,
         Date date)
        throws RemoteException;

    /**
     * Returns the position of the supplied symbol based on reports,
     * generated and received up until the supplied date in UTC.
     *
     * @param date The date, in UTC.
     * @param symbol The symbol.
     *
     * @return The position.
     *
     * @throws RemoteException Thrown if the operation cannot be
     * completed.
     */

    BigDecimal getPositionAsOf
        (ClientContext context,
         Date date,
         MSymbol symbol)
        throws RemoteException;

    /**
     * Returns the next server order ID to the client with the given
     * context.
     *
     * @param context The context.
     *
     * @return The next order ID.
     *
     * @throws RemoteException Thrown if the operation cannot be
     * completed.
     */

    String getNextOrderID
        (ClientContext context)
        throws RemoteException;
}
