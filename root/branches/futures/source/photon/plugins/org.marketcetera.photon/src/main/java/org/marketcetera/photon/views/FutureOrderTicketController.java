package org.marketcetera.photon.views;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FutureOrderTicketController
        extends OrderTicketController<FutureOrderTicketModel>
{
    /**
     * Create a new FutureOrderTicketController instance.
     *
     * @param inOrderTicketModel
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
        return StockOrderTicketView.ID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.IOrderTicketController#getViewId()
     */
    @Override
    public String getViewId()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
