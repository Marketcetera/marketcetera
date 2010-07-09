package org.marketcetera.photon.views;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.client.ClientManager;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.Util;
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
     * @see org.marketcetera.photon.views.OrderTicketView#bindMessage()
     */
    @Override
    protected void bindMessage()
    {
        super.bindMessage();
        final FutureOrderTicketModel model = getModel();
        final IFutureOrderTicket ticket = getXSWTView();
        /*
         * customer info
         */
        bindRequiredCombo(customerInfoComboViewer,
                          model.getCustomerInfo(),
                          Messages.FUTURE_ORDER_TICKET_VIEW_CUSTOMER_INFO__LABEL.getText());
        enableForNewOrderOnly(ticket.getCustomerInfoCombo());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.OrderTicketView#initViewers(org.marketcetera.photon.views.IOrderTicket)
     */
    @Override
    protected void initViewers(IFutureOrderTicket inTicket)
    {
        super.initViewers(inTicket);
        // set up the combo viewer for the customer info
        customerInfoComboViewer = new ComboViewer(inTicket.getCustomerInfoCombo());
        customerInfoComboViewer.setContentProvider(new ArrayContentProvider());
        customerInfoComboViewer.setInput(emptyList);
    }
    private static final String[] emptyList = new String[] { " " };
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
     * @see org.marketcetera.photon.views.OrderTicketView#customizeWidgets(org.marketcetera.photon.views.IOrderTicket)
     */
    @Override
    protected void customizeWidgets(final IFutureOrderTicket inTicket)
    {
        super.customizeWidgets(inTicket);
        // enter in either of these fields will send the order (assuming there are no errors)
        addSendOrderListener(inTicket.getCustomerInfoCombo());
        inTicket.getCustomerInfoCombo().addListener(SWT.FocusIn,
                                                    new Listener() {
            @Override
            public void handleEvent(Event inArg0)
            {
                if(Arrays.equals(inTicket.getCustomerInfoCombo().getItems(),
                                 emptyList)) {
                    try {
                        Properties userdata = ClientManager.getInstance().getUserData();
                        String rawList = userdata.getProperty(CUSTOMER_INFO_KEY);
                        if(rawList == null) {
                            // no customer info defined
                            return;
                        }
                        Properties customerinfo = Util.propertiesFromString(rawList);
                        Set<String> sortedCustomerInfo = new TreeSet<String>();
                        for(Object customerinfoChunk : customerinfo.values()) {
                            sortedCustomerInfo.add((String)customerinfoChunk);
                        }
                        if(sortedCustomerInfo.isEmpty()) {
                            // no customer info defined
                            return;
                        }
                        inTicket.getCustomerInfoCombo().setItems(sortedCustomerInfo.toArray(new String[sortedCustomerInfo.size()]));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public static final String CUSTOMER_INFO_KEY = "com.fortum.marketcetera.customerinfo.key";
    public static final String ID = "org.marketcetera.photon.views.FutureOrderTicketView"; //$NON-NLS-1$
    /**
     * the customer info combo dropdown
     */
    private ComboViewer customerInfoComboViewer;
}