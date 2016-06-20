package org.marketcetera.photon.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.model.marketdata.MDDepthOfBook;
import org.marketcetera.photon.model.marketdata.MDPackage;
import org.marketcetera.photon.model.marketdata.MDQuote;
import org.marketcetera.trade.*;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Initializes order ticket from the selection quote. Currently this
 * assumes the quote is an {@link MDQuote} child of a {@link MDDepthOfBook}
 * object.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: CreateOrderFromQuote.java 10808 2009-10-12 21:33:18Z
 *          anshul $
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class CreateOrderFromQuote extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event)
            throws ExecutionException
    {
        Object selection = ((StructuredSelection)HandlerUtil.getActiveMenuSelection(event)).getFirstElement();
        if(selection instanceof MDQuote) {
            MDQuote quote = (MDQuote)selection;
            Side side = Side.Sell;
            if(quote.eContainingFeature() == MDPackage.Literals.MD_DEPTH_OF_BOOK__ASKS) {
                side = Side.Buy;
            }
            Instrument instrument = quote.getInstrument();
            OrderSingle newOrder = Factory.getInstance().createOrderSingle();
            newOrder.setInstrument(instrument);
            newOrder.setOrderType(OrderType.Limit);
            newOrder.setSide(side);
            newOrder.setQuantity(quote.getSize());
            newOrder.setPrice(quote.getPrice());
            newOrder.setTimeInForce(TimeInForce.Day);
            try {
                PhotonPlugin.getDefault().showOrderInTicket(newOrder);
            } catch (WorkbenchException e) {
                SLF4JLoggerProxy.error(this, e);
                Shell shell = HandlerUtil.getActiveShellChecked(event);
                ErrorDialog.openError(shell, null, null,
                        new Status(IStatus.ERROR, PhotonPlugin.ID, e
                                .getLocalizedMessage()));
            }
        }
        return null;
    }
}
