package org.marketcetera.photon.views;

import java.io.InputStream;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.PhotonPlugin;

/* $License$ */

/**
 * Provides an order ticket view for the Futures asset class.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class FutureOrderTicketView
        extends OrderTicketView<FutureOrderTicketModel, IFutureOrderTicket>
{
    /**
     * Gets the "default" FutureOrderTicketView, that is the first one returned
     * by {@link IWorkbenchPage#findView(String)}.
     * 
     * @return the default FutureOrderTicketView
     */
    public static FutureOrderTicketView getDefault()
    {
        return (FutureOrderTicketView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(FutureOrderTicketView.ID);
    }
    /**
     * Create a new FutureOrderTicketView instance.
     */
    public FutureOrderTicketView()
    {
        super(IFutureOrderTicket.class,
              PhotonPlugin.getDefault().getFutureOrderTicketModel());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.OrderTicketView#getNewOrderString()
     */
    @Override
    protected String getNewOrderString()
    {
        return Messages.FUTURE_ORDER_TICKET_VIEW_NEW__HEADING.getText();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.OrderTicketView#getReplaceOrderString()
     */
    @Override
    protected String getReplaceOrderString()
    {
        return Messages.FUTURE_ORDER_TICKET_VIEW_REPLACE__HEADING.getText();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.XSWTView#getXSWTResourceStream()
     */
    @Override
    protected InputStream getXSWTResourceStream()
    {
        return getClass().getResourceAsStream("/future_order_ticket.xswt"); //$NON-NLS-1$
    }
    
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.OrderTicketView#bindMessage()
     */
    @Override
    protected void bindMessage()
    {
        super.bindMessage();
        final IFutureOrderTicket ticket = getXSWTView();
        bindText(ticket.getExpiryText(),
                 getModel().getExpiry());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.OrderTicketView#customizeWidgets(org.marketcetera.photon.views.IOrderTicket)
     */
    @Override
    protected void customizeWidgets(final IFutureOrderTicket inTicket)
    {
        super.customizeWidgets(inTicket);
        selectOnFocus(inTicket.getExpiryText());
        addSendOrderListener(inTicket.getExpiryText());
    }
   /**
     * the ID to uniquely identify this view
     */
    public static final String ID = "org.marketcetera.photon.views.FutureOrderTicketView"; //$NON-NLS-1$
}