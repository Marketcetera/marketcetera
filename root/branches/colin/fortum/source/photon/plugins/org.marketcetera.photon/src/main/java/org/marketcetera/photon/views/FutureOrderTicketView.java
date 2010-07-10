package org.marketcetera.photon.views;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.client.ClientManager;
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
        bindCombo(customerInfoComboViewer,
                  model.getCustomerInfo());
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
        final Combo customerInfoCombo = inTicket.getCustomerInfoCombo();
        addSendOrderListener(customerInfoCombo);
        customerInfoCombo.addListener(SWT.FocusIn,
                                      new Listener() {
            @Override
            public void handleEvent(Event inEvent)
            {
                if(Arrays.equals(customerInfoCombo.getItems(),
                                 emptyList)) {
                    try {
                        Properties userdata = ClientManager.getInstance().getUserData();
                        if(userdata == null) {
                            // no customer info defined
                            return;
                        }
                        String rawList = userdata.getProperty(CUSTOMER_INFO_KEY);
                        if(rawList == null) {
                            // no customer info defined
                            return;
                        }
                        String[] customerinfo = rawList.split("H@@H");
                        Set<String> sortedCustomerInfo = new TreeSet<String>();
                        for(String customerinfoChunk : customerinfo) {
                            customerinfoChunk = StringUtils.trimToNull(customerinfoChunk);
                            if(customerinfoChunk != null) {
                                sortedCustomerInfo.add(customerinfoChunk);
                            }
                        }
                        if(sortedCustomerInfo.isEmpty()) {
                            // no customer info defined
                            return;
                        }
                        customerInfoCombo.setItems(sortedCustomerInfo.toArray(new String[sortedCustomerInfo.size()]));
                        customerInfoCombo.pack(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    /**
     * the key used to identify the nordpool customer info data
     */
    public static final String CUSTOMER_INFO_KEY = "com.fortum.marketcetera.photon.userdata.nordpool.customerinfo";
    /**
     * the ID to uniquely identify this view
     */
    public static final String ID = "org.marketcetera.photon.views.FutureOrderTicketView"; //$NON-NLS-1$
    /**
     * the customer info combo dropdown
     */
    private ComboViewer customerInfoComboViewer;
    private static final String[] emptyList = new String[] { " " };
}