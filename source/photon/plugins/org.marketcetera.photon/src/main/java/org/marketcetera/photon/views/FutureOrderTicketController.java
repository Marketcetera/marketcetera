package org.marketcetera.photon.views;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.ui.FuturePerspectiveFactory;

/* $License$ */

/**
 * Controller for the future order ticket.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.0
 */
@ClassVersion("$Id$")
public class FutureOrderTicketController
        extends OrderTicketController<FutureOrderTicketModel>
{
    /**
     * Create a new FutureOrderTicketController instance.
     *
     * @param inOrderTicketModel a <code>FutureOrderTicketModel</code> value
     */
    public FutureOrderTicketController(FutureOrderTicketModel inOrderTicketModel)
    {
        super(inOrderTicketModel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.IOrderTicketController#getPerspectiveId()
     */
    @Override
    public String getPerspectiveId()
    {
        return FuturePerspectiveFactory.ID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.IOrderTicketController#getViewId()
     */
    @Override
    public String getViewId()
    {
        return FutureOrderTicketView.ID;
    }
}
