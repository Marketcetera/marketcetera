package org.marketcetera.photon.views;

import java.io.InputStream;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.photon.PhotonPlugin;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
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
    @Override
    protected InputStream getXSWTResourceStream()
    {
        return getClass().getResourceAsStream("/future_order_ticket.xswt"); //$NON-NLS-1$
    }
    @Override
    protected String getReplaceOrderString()
    {
        return Messages.FUTURE_ORDER_TICKET_VIEW_REPLACE__HEADING.getText();
    }
    @Override
    protected String getNewOrderString()
    {
        return Messages.FUTURE_ORDER_TICKET_VIEW_NEW__HEADING.getText();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.OrderTicketView#bindMessage()
     */
    @Override
    protected void bindMessage()
    {
        super.bindMessage();
        final DataBindingContext dbc = getDataBindingContext();
        final FutureOrderTicketModel model = getModel();
        final IFutureOrderTicket ticket = getXSWTView();
        // expiration year
        final IObservableValue target = SWTObservables.observeText(ticket.getExpirationYearText(),
                                                                   SWT.Modify);
            enableForNewOrderOnly(ticket.getExpirationYearText());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.OrderTicketView#customizeWidgets(org.marketcetera.photon.views.IOrderTicket)
     */
    @Override
    protected void customizeWidgets(IFutureOrderTicket inTicket)
    {
        super.customizeWidgets(inTicket);
        updateSize(inTicket.getExpirationYearText(),
                   5);
        selectOnFocus(inTicket.getExpirationYearText());
        addSendOrderListener(inTicket.getExpirationYearText());
        addSendOrderListener(inTicket.getExpirationMonthCombo());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.OrderTicketView#initViewers(org.marketcetera.photon.views.IOrderTicket)
     */
    @Override
    protected void initViewers(IFutureOrderTicket inTicket)
    {
        super.initViewers(inTicket);
    }
    public static final String ID = "org.marketcetera.photon.views.FutureOrderTicketView"; //$NON-NLS-1$
    /**
     * 
     */
    private ComboViewer expirationMonthComboViewer;
}