package org.marketcetera.photon.views;

import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.client.OrderValidationException;
import org.marketcetera.client.instruments.CurrencyValidationHandler;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.commons.ui.databinding.DataBindingUtils;

/* $License$ */

/**
 * This class implements the view that provides the end user the ability to type
 * in--and graphically interact with--currency orders.
 * 
 */
@ClassVersion("$Id$")
public class CurrencyOrderTicketView extends
        OrderTicketView<CurrencyOrderTicketModel, ICurrencyOrderTicket> {

    public static final String ID = "org.marketcetera.photon.views.CurrencyOrderTicketView"; //$NON-NLS-1$

    /**
     * Constructor.
     */
    public CurrencyOrderTicketView() {
        super(ICurrencyOrderTicket.class, PhotonPlugin.getDefault()
                .getCurrencyOrderTicketModel());
    }

    @Override
    protected InputStream getXSWTResourceStream() {
        return getClass().getResourceAsStream("/currency_order_ticket.xswt"); //$NON-NLS-1$
    }

    @Override
    protected String getNewOrderString() {
        return Messages.CURRENCY_ORDER_TICKET_VIEW_NEW__HEADING.getText();
    }

    @Override
    protected String getReplaceOrderString() {
        return Messages.CURRENCY_ORDER_TICKET_VIEW_REPLACE__HEADING.getText();
    }

    @Override
    protected void initViewers(ICurrencyOrderTicket ticket) {
        super.initViewers(ticket);
    }

    @Override
    protected void customizeWidgets(final ICurrencyOrderTicket ticket) {
        super.customizeWidgets(ticket);

        /*
         * Update size of text fields since default will be small.
         */
        updateSize(ticket.getNearTenorText(), 8);
        updateSize(ticket.getFarTenorText(), 8);

        /*
         * Customize text fields to auto select the text on focus to make it
         * easy to change the value.
         */
        selectOnFocus(ticket.getNearTenorText());
        selectOnFocus(ticket.getFarTenorText());

        /*
         * If the ticket has no errors, enter on these fields will trigger a
         * send.
         */
        addSendOrderListener(ticket.getNearTenorText());
        addSendOrderListener(ticket.getFarTenorText());

        ticket.getSelectNearTenorButton().addSelectionListener(
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(final SelectionEvent e) {
                        PopupDialog d = new CalendarPopup(getSite().getShell(),ticket.getSelectNearTenorButton(),ticket.getNearTenorText());
                        d.open();
                    };
                });
        
        ticket.getSelectFarTenorButton().addSelectionListener(
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(final SelectionEvent e) {
                        PopupDialog d = new CalendarPopup(getSite().getShell(), ticket.getSelectFarTenorButton(),ticket.getFarTenorText());
                        d.open();
                    };
                });
    }

    /**
     * Gets the "default" CurrencyOrderTicketView, that is the first one returned
     * by {@link IWorkbenchPage#findView(String)}
     * 
     * @return the default CurrencyOrderTicketView
     */
    public static CurrencyOrderTicketView getDefault() {
        CurrencyOrderTicketView orderTicket = (CurrencyOrderTicketView) PlatformUI
                .getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .findView(CurrencyOrderTicketView.ID);

        return orderTicket;
    }

    @Override
    protected void bindMessage() {
        super.bindMessage();
        final DataBindingContext dbc = getDataBindingContext();
        final CurrencyOrderTicketModel model = getModel();
        final ICurrencyOrderTicket ticket = getXSWTView();

        //Symbol
        final IObservableValue symbolTarget = SWTObservables.observeText(ticket.getSymbolText(),SWT.Modify);
        MultiValidator currencySymbolValidator = new MultiValidator() {
            @Override
            protected IStatus validate() {
                String symbol = (String)symbolTarget.getValue();
                if (symbol == null || symbol.isEmpty()) {
                    return ValidationStatus.ok();
                }
                try {
                		CurrencyValidationHandler.validateCurrencySymbol(symbol);
                		return ValidationStatus.ok();
                } catch (OrderValidationException e) {
                    return ValidationStatus.error(e.getLocalizedMessage(), e);
                }
            }
        };
        DataBindingUtils.initControlDecorationSupportFor(currencySymbolValidator, SWT.BOTTOM | SWT.LEFT);
        dbc.addValidationStatusProvider(currencySymbolValidator);        
        
        //Near Tenor
        final IObservableValue nearTenorTarget = SWTObservables.observeText(ticket
                .getNearTenorText(), SWT.Modify);
        dbc.bindValue(nearTenorTarget, model.getNearTenor());
        MultiValidator nearTenorValidator = new MultiValidator() {
            @Override
            protected IStatus validate() {
                String tenor = (String) nearTenorTarget.getValue();
                tenor = StringUtils.trimToNull(tenor);
                if (tenor == null || tenor.isEmpty()) {
                    return ValidationStatus.ok();
                }
                try {
                    CurrencyValidationHandler.validateTenor(tenor);
                    return ValidationStatus.ok();
                } catch (OrderValidationException e) {
                    return ValidationStatus.error(e.getLocalizedMessage(), e);
                }
            }
        };
        DataBindingUtils.initControlDecorationSupportFor(nearTenorValidator,
                SWT.BOTTOM | SWT.LEFT);
        dbc.addValidationStatusProvider(nearTenorValidator);
        enableForNewOrderOnly(ticket.getNearTenorText());
        enableForNewOrderOnly(ticket.getSelectNearTenorButton());
        
        //Far Tenor
        final IObservableValue farTenorTarget = SWTObservables.observeText(ticket
                .getFarTenorText(), SWT.Modify);
        dbc.bindValue(farTenorTarget, model.getFarTenor());
        MultiValidator farTenorValidator = new MultiValidator() {
            @Override
            protected IStatus validate() {
                String tenor = (String) farTenorTarget.getValue();
                tenor = StringUtils.trimToNull(tenor);
                if (tenor == null || tenor.isEmpty()) {
                    return ValidationStatus.ok();
                }
                try {
                    CurrencyValidationHandler.validateTenor(tenor);
                    return ValidationStatus.ok();
                } catch (OrderValidationException e) {
                    return ValidationStatus.error(e.getLocalizedMessage(), e);
                }
            }
        };
        DataBindingUtils.initControlDecorationSupportFor(farTenorValidator,
                SWT.BOTTOM | SWT.LEFT);
        dbc.addValidationStatusProvider(farTenorValidator);
        final IObservableValue leftCCYTarget = SWTObservables.observeSelection(ticket.getRadioButtonCCY1());
        dbc.bindValue(leftCCYTarget, model.getLeftCCY());
        
        final IObservableValue rightCCYTarget = SWTObservables.observeSelection(ticket.getRadioButtonCCY2());
        dbc.bindValue(rightCCYTarget, model.getRightCCY());
        
        enableForNewOrderOnly(ticket.getFarTenorText());
        enableForNewOrderOnly(ticket.getSelectFarTenorButton());
        enableForNewOrderOnly(ticket.getRadioButtonCCY1());
        enableForNewOrderOnly(ticket.getRadioButtonCCY2());

    }

    private class CalendarPopup extends PopupDialog {
    	
    	Button button;
    	Text text;

        private static final String DATE_FORMAT = "%04d%02d%02d"; //$NON-NLS-1$

        public CalendarPopup(Shell shell, Button button, Text text) {
            super(shell, SWT.ON_TOP, true, false, false, false, false, null,
                    null);
            this.button = button;
            this.text = text;
        }

        @Override
        protected Point getDefaultLocation(Point initialSize) {
            Point location = getShell().getDisplay().map(button.getParent(),
                    null, button.getLocation());
            Point size = button.getSize();
            return new Point(location.x + size.x, location.y + size.y);
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            Composite composite = (Composite) super.createDialogArea(parent);
            final DateTime calendar = new DateTime(composite, SWT.CALENDAR);
            calendar.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                	text.setText(
                                    String.format(DATE_FORMAT, calendar
                                            .getYear(),
                                            calendar.getMonth() + 1, calendar
                                                    .getDay()));
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    widgetSelected(e);
                    close();
                }
            });
            return composite;
        }
    }
}
