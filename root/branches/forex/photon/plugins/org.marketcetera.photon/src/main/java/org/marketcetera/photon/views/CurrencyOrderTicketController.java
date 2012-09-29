package org.marketcetera.photon.views;

import org.marketcetera.photon.ui.CurrencyPerspectiveFactory;

/* $License$ */

/**
 * Controller for the currency order ticket.
 *
 */
public class CurrencyOrderTicketController
        extends OrderTicketController<CurrencyOrderTicketModel>
{
    /**
     * Create a new CurrencyOrderTicketController instance.
     *
     * @param inOrderTicketModel a <code>CurrencyOrderTicketModel</code> value
     */
    public CurrencyOrderTicketController(CurrencyOrderTicketModel inOrderTicketModel)
    {
        super(inOrderTicketModel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.IOrderTicketController#getPerspectiveId()
     */
    @Override
    public String getPerspectiveId()
    {
        return CurrencyPerspectiveFactory.ID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.IOrderTicketController#getViewId()
     */
    @Override
    public String getViewId()
    {
        return CurrencyOrderTicketView.ID;
    }
}
