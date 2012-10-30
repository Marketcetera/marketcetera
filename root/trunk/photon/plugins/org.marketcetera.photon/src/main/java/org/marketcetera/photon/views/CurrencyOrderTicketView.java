package org.marketcetera.photon.views;

import java.io.InputStream;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.client.OrderValidationException;
import org.marketcetera.client.instruments.CurrencyValidationHandler;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.commons.ui.databinding.DataBindingUtils;
import org.marketcetera.trade.Currency;

/* $License$ */

/**
 * Provides an order ticket view for the Currency asset class.
 *
 */
public class CurrencyOrderTicketView
        extends OrderTicketView<CurrencyOrderTicketModel, ICurrencyOrderTicket>
{
    /**
     * Gets the "default" CurrencyOrderTicketView, that is the first one returned
     * by {@link IWorkbenchPage#findView(String)}.
     * 
     * @return the default CurrencyOrderTicketView
     */
    public static CurrencyOrderTicketView getDefault()
    {
        return (CurrencyOrderTicketView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(CurrencyOrderTicketView.ID);
    }
    /**
     * Create a new CurrencyOrderTicketView instance.
     */
    public CurrencyOrderTicketView()
    {
        super(ICurrencyOrderTicket.class,
              PhotonPlugin.getDefault().getCurrencyOrderTicketModel());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.OrderTicketView#bindMessage()
     */
    @Override
    protected void bindMessage()
    {
        super.bindMessage();
        final DataBindingContext dbc = getDataBindingContext();
        final CurrencyOrderTicketModel model = getModel();
        final ICurrencyOrderTicket ticket = getXSWTView();
        final IObservableValue target = SWTObservables.observeText(ticket.getSymbolText(),SWT.Modify);
        MultiValidator currencySymbolValidator = new MultiValidator() {
            @Override
            protected IStatus validate() {
                String symbol = (String)target.getValue();
                if (symbol == null ||
                    symbol.isEmpty()) {
                    return ValidationStatus.ok();
                }
                try {
                		CurrencyValidationHandler.validateCurrencySymbol(symbol);
                		return ValidationStatus.ok();
                } catch (OrderValidationException e) {
                    return ValidationStatus.error(e.getLocalizedMessage(),
                                                  e);
                }
            }
        };
        DataBindingUtils.initControlDecorationSupportFor(currencySymbolValidator,
                                                         SWT.BOTTOM | SWT.LEFT);
        dbc.addValidationStatusProvider(currencySymbolValidator);
        bindRequiredCombo(nearTermComboViewer,
                          model.getNearTenor(),
                          Messages.CURRENCY_ORDER_TICKET_VIEW_NEAR_TENOR__LABEL.getText());
        bindCombo(farTermComboViewer, model.getFarTenor());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.OrderTicketView#initViewers(org.marketcetera.photon.views.IOrderTicket)
     */
    @Override
    protected void initViewers(ICurrencyOrderTicket inTicket)
    {
        super.initViewers(inTicket);
        // set up the combo viewer for Tenor
        nearTermComboViewer = new ComboViewer(inTicket.getNearTenorCombo());
        nearTermComboViewer.setContentProvider(new ArrayContentProvider());
        nearTermComboViewer.setInput(Currency.getTenorSet());
        farTermComboViewer = new ComboViewer(inTicket.getFarTenorCombo());
        farTermComboViewer.setContentProvider(new ArrayContentProvider());
        farTermComboViewer.setInput(Currency.getTenorSet());
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.OrderTicketView#getNewOrderString()
     */
    @Override
    protected String getNewOrderString()
    {
        return Messages.CURRENCY_ORDER_TICKET_VIEW_NEW__HEADING.getText();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.OrderTicketView#getReplaceOrderString()
     */
    @Override
    protected String getReplaceOrderString()
    {
        return Messages.CURRENCY_ORDER_TICKET_VIEW_REPLACE__HEADING.getText();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.XSWTView#getXSWTResourceStream()
     */
    @Override
    protected InputStream getXSWTResourceStream()
    {
        return getClass().getResourceAsStream("/currency_order_ticket.xswt"); //$NON-NLS-1$
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.OrderTicketView#customizeWidgets(org.marketcetera.photon.views.IOrderTicket)
     */
    @Override
    protected void customizeWidgets(ICurrencyOrderTicket inTicket)
    {
        super.customizeWidgets(inTicket);
        // enter in either of these fields will send the order (assuming there are no errors)
        addSendOrderListener(inTicket.getNearTenorCombo());
        addSendOrderListener(inTicket.getFarTenorCombo());
    }
    public static final String ID = "org.marketcetera.photon.views.CurrencyOrderTicketView"; //$NON-NLS-1$
    /**
     * the expiration near term combo dropdown
     */
    private ComboViewer nearTermComboViewer;
    /**
     * the expiration far term combo dropdown
     */
    private ComboViewer farTermComboViewer;
}