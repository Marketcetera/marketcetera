package org.marketcetera.trade;

import org.springframework.security.core.GrantedAuthority;

/* $License$ */

/**
 * Defines trading permission names.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public enum TradePermissions
        implements GrantedAuthority
{
    SendOrderAction,
    ViewBrokerStatusAction,
    ViewOpenOrdersAction,
    ViewReportAction,
    ViewPositionAction,
    ViewUserDataAction,
    WriteUserDataAction,
    AddReportAction,
    DeleteReportAction;
    /* (non-Javadoc)
     * @see org.springframework.security.core.GrantedAuthority#getAuthority()
     */
    @Override
    public String getAuthority()
    {
        return name();
    }
}
