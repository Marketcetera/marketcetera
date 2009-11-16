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
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.client.OrderValidationException;
import org.marketcetera.client.instruments.OptionValidationHandler;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.commons.ui.databinding.DataBindingUtils;
import org.marketcetera.trade.OptionType;

/* $License$ */

/**
 * This class implements the view that provides the end user the ability to type
 * in--and graphically interact with--stock option orders.
 * 
 * Additionally this class manages the stock and option market data that can be
 * displayed along with the order ticket itself.
 * 
 * @author gmiller
 * @version $Id$
 * @since 1.0.0
 * 
 */
@ClassVersion("$Id$")
public class OptionOrderTicketView extends
        OrderTicketView<OptionOrderTicketModel, IOptionOrderTicket> {

    public static final String ID = "org.marketcetera.photon.views.OptionOrderTicketView"; //$NON-NLS-1$
    private ComboViewer mOptionTypeComboViewer;
    private ComboViewer mOrderCapacityComboViewer;
    private ComboViewer mOpenCloseComboViewer;

    /**
     * Constructor.
     */
    public OptionOrderTicketView() {
        super(IOptionOrderTicket.class, PhotonPlugin.getDefault()
                .getOptionOrderTicketModel());
    }

    @Override
    protected InputStream getXSWTResourceStream() {
        return getClass().getResourceAsStream("/option_order_ticket.xswt"); //$NON-NLS-1$
    }

    @Override
    protected String getNewOrderString() {
        return Messages.OPTION_ORDER_TICKET_VIEW_NEW__HEADING.getText();
    }

    @Override
    protected String getReplaceOrderString() {
        return Messages.OPTION_ORDER_TICKET_VIEW_REPLACE__HEADING.getText();
    }

    @Override
    protected void initViewers(IOptionOrderTicket ticket) {
        super.initViewers(ticket);

        /*
         * Put or Call combo based on OptionType enum.
         */
        mOptionTypeComboViewer = new ComboViewer(ticket.getPutOrCallCombo());
        mOptionTypeComboViewer.setContentProvider(new ArrayContentProvider());
        mOptionTypeComboViewer.setInput(EnumSet.complementOf(
                EnumSet.of(OptionType.Unknown)).toArray());

        /*
         * Order capacity combo based on OrderCapacity enum.
         * 
         * An extra blank entry is added since the field is optional.
         */
        mOrderCapacityComboViewer = new ComboViewer(ticket
                .getOrderCapacityCombo());
        mOrderCapacityComboViewer
                .setContentProvider(new ArrayContentProvider());
        mOrderCapacityComboViewer.setInput(getModel()
                .getValidOrderCapacityValues());

        /*
         * Open close combo based on PositionEffect enum.
         * 
         * An extra blank entry is added since the field is optional.
         */
        mOpenCloseComboViewer = new ComboViewer(ticket.getOpenCloseCombo());
        mOpenCloseComboViewer.setContentProvider(new ArrayContentProvider());
        mOpenCloseComboViewer.setInput(getModel()
                .getValidPositionEffectValues());
    }

    @Override
    protected void customizeWidgets(final IOptionOrderTicket ticket) {
        super.customizeWidgets(ticket);

        /*
         * Update size of text fields since default will be small.
         */
        updateSize(ticket.getOptionExpiryText(), 10);
        updateSize(ticket.getStrikePriceText(), 10);

        /*
         * Customize text fields to auto select the text on focus to make it
         * easy to change the value.
         */
        selectOnFocus(ticket.getOptionExpiryText());
        selectOnFocus(ticket.getStrikePriceText());

        /*
         * If the ticket has no errors, enter on these fields will trigger a
         * send.
         */
        addSendOrderListener(ticket.getOptionExpiryText());
        addSendOrderListener(ticket.getStrikePriceText());
        addSendOrderListener(ticket.getPutOrCallCombo());
        addSendOrderListener(ticket.getOrderCapacityCombo());
        addSendOrderListener(ticket.getOpenCloseCombo());

        ticket.getSelectExpiryButton().addSelectionListener(
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(final SelectionEvent e) {
                        PopupDialog d = new CalendarPopup(getSite().getShell());
                        d.open();
                    };
                });
    }

    /**
     * Gets the "default" OptionOrderTicketView, that is the first one returned
     * by {@link IWorkbenchPage#findView(String)}
     * 
     * @return the default OptionOrderTicketView
     */
    public static OptionOrderTicketView getDefault() {
        OptionOrderTicketView orderTicket = (OptionOrderTicketView) PlatformUI
                .getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .findView(OptionOrderTicketView.ID);

        return orderTicket;
    }

    @Override
    protected void bindMessage() {
        super.bindMessage();
        final DataBindingContext dbc = getDataBindingContext();
        final OptionOrderTicketModel model = getModel();
        final IOptionOrderTicket ticket = getXSWTView();

        /*
         * Expiry
         */
        final IObservableValue target = SWTObservables.observeText(ticket
                .getOptionExpiryText(), SWT.Modify);
        Binding binding = dbc.bindValue(target, model.getOptionExpiry());
        setRequired(binding, Messages.OPTION_ORDER_TICKET_VIEW_EXPIRY__LABEL
                .getText());
        MultiValidator expiryValidator = new MultiValidator() {
            @Override
            protected IStatus validate() {
                String expiry = (String) target.getValue();
                if (expiry == null || expiry.isEmpty()) {
                    /*
                     * Let required field support kick in.
                     */
                    return ValidationStatus.ok();
                }
                try {
                    OptionValidationHandler.validateExpiryDate(expiry);
                    return ValidationStatus.ok();
                } catch (OrderValidationException e) {
                    return ValidationStatus.error(e.getLocalizedMessage(), e);
                }
            }
        };
        DataBindingUtils.initControlDecorationSupportFor(expiryValidator,
                SWT.BOTTOM | SWT.LEFT);
        dbc.addValidationStatusProvider(expiryValidator);

        /*
         * Strike
         */
        bindRequiredDecimal(ticket.getStrikePriceText(),
                model.getStrikePrice(),
                Messages.OPTION_ORDER_TICKET_VIEW_STRIKE_PRICE__LABEL.getText());

        /*
         * Option type
         */
        bindRequiredCombo(mOptionTypeComboViewer, model.getOptionType(),
                Messages.OPTION_ORDER_TICKET_VIEW_OPTION_TYPE__LABEL.getText());

        /*
         * Order Capacity
         */
        bindCombo(mOrderCapacityComboViewer, model.getOrderCapacity());

        /*
         * Open Close
         */
        bindCombo(mOpenCloseComboViewer, model.getPositionEffect());
    }

    private class CalendarPopup extends PopupDialog {

        private static final String DATE_FORMAT = "%04d%02d%02d"; //$NON-NLS-1$

        public CalendarPopup(Shell shell) {
            super(shell, SWT.ON_TOP, true, false, false, false, false, null,
                    null);
        }

        @Override
        protected Point getDefaultLocation(Point initialSize) {
            Button button = getXSWTView().getSelectExpiryButton();
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
                    getXSWTView().getOptionExpiryText()
                            .setText(
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
