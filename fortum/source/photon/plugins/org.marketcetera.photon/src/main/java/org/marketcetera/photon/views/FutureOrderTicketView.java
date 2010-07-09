package org.marketcetera.photon.views;

import java.io.InputStream;
import java.util.EnumSet;

import org.eclipse.core.databinding.Binding;
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
import org.marketcetera.client.instruments.FutureValidationHandler;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.commons.ui.databinding.DataBindingUtils;
import org.marketcetera.trade.FutureExpirationMonth;

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
        final DataBindingContext dbc = getDataBindingContext();
        final FutureOrderTicketModel model = getModel();
        final IFutureOrderTicket ticket = getXSWTView();
        /*
         * Expiration year
         */
        final IObservableValue target = SWTObservables.observeText(ticket.getExpirationYearText(),
                                                                   SWT.Modify);
        Binding binding = dbc.bindValue(target,
                                        model.getFutureExpirationYear());
        setRequired(binding,
                    Messages.FUTURE_ORDER_TICKET_VIEW_EXPIRATION_YEAR__LABEL.getText());
        MultiValidator expirationYearValidator = new MultiValidator() {
            @Override
            protected IStatus validate() {
                String expirationYear = (String)target.getValue();
                if (expirationYear == null ||
                    expirationYear.isEmpty()) {
                    /*
                     * Let required field support kick in.
                     */
                    return ValidationStatus.ok();
                }
                try {
                    FutureValidationHandler.validateExpirationYear(expirationYear);
                    return ValidationStatus.ok();
                } catch (OrderValidationException e) {
                    return ValidationStatus.error(e.getLocalizedMessage(),
                                                  e);
                }
            }
        };
        DataBindingUtils.initControlDecorationSupportFor(expirationYearValidator,
                                                         SWT.BOTTOM | SWT.LEFT);
        dbc.addValidationStatusProvider(expirationYearValidator);
        enableForNewOrderOnly(ticket.getExpirationYearText());
        /*
         * expiration month
         */
        bindRequiredCombo(expirationMonthComboViewer,
                          model.getFutureExpirationMonth(),
                          Messages.FUTURE_ORDER_TICKET_VIEW_EXPIRATION_MONTH__LABEL.getText());
        enableForNewOrderOnly(ticket.getExpirationMonthCombo());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.OrderTicketView#initViewers(org.marketcetera.photon.views.IOrderTicket)
     */
    @Override
    protected void initViewers(IFutureOrderTicket inTicket)
    {
        super.initViewers(inTicket);
        // set up the combo viewer for the expiration month
        expirationMonthComboViewer = new ComboViewer(inTicket.getExpirationMonthCombo());
        expirationMonthComboViewer.setContentProvider(new ArrayContentProvider());
        expirationMonthComboViewer.setInput(EnumSet.allOf(FutureExpirationMonth.class).toArray());
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
    protected void customizeWidgets(IFutureOrderTicket inTicket)
    {
        super.customizeWidgets(inTicket);
        // the default size is wrong, set it manually
        updateSize(inTicket.getExpirationYearText(),
                   20);
        // selects the text in the widget upon focus to facilitate easy editing
        selectOnFocus(inTicket.getExpirationYearText());
        // enter in either of these fields will send the order (assuming there are no errors)
        addSendOrderListener(inTicket.getExpirationMonthCombo());
        addSendOrderListener(inTicket.getExpirationYearText());
    }
    public static final String ID = "org.marketcetera.photon.views.FutureOrderTicketView"; //$NON-NLS-1$
    /**
     * the expiration month combo dropdown
     */
    private ComboViewer expirationMonthComboViewer;
}