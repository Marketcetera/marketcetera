package org.marketcetera.photon.internal.strategy;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.views.IOrderTicketController;
import org.marketcetera.photon.views.StockOrderTicketController;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.FIXConverter;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.Message;

/* $License$ */

/**
 * Opens the trade suggestion in the ticket view to be modified.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class OpenSuggestionHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IStructuredSelection selection = (IStructuredSelection) HandlerUtil
                .getCurrentSelectionChecked(event);
        TradeSuggestion suggestion = (TradeSuggestion) selection
                .getFirstElement();
        OrderSingle order = suggestion.getOrder();
        try {
            Message message = FIXConverter.toQMessage(FIXVersion.FIX_SYSTEM
                    .getMessageFactory(), FIXVersion.FIX_SYSTEM, order);
            IOrderTicketController orderTicketController = PhotonPlugin
                    .getDefault().getOrderTicketController(message);
            String perspective;
            String view;
            if (orderTicketController instanceof StockOrderTicketController) {
                perspective = "org.marketcetera.photon.EquityPerspective"; //$NON-NLS-1$
                view = "org.marketcetera.photon.views.StockOrderTicketView"; //$NON-NLS-1$
            } else {
                perspective = "org.marketcetera.photon.OptionPerspective"; //$NON-NLS-1$
                view = "org.marketcetera.photon.views.OpenOrdersView"; //$NON-NLS-1$
            }
            orderTicketController.setOrderMessage(message);
            try {
                PlatformUI.getWorkbench().showPerspective(perspective,
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow());
                PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getActivePage().showView(view);
                TradeSuggestionManager.getCurrent().removeSuggestion(suggestion);
            } catch (WorkbenchException e) {
                SLF4JLoggerProxy.error(this, e);
                Shell shell = HandlerUtil.getActiveShellChecked(event);
                ErrorDialog.openError(shell, null, null, new Status(
                        IStatus.ERROR, Activator.PLUGIN_ID, e
                                .getLocalizedMessage()));
            }
        } catch (Exception e) {
            Messages.OPEN_SUGGESTION_HANDLER_CONVERSION_FAILURE.error(this, e);
            Shell shell = HandlerUtil.getActiveShellChecked(event);
            ErrorDialog.openError(shell, null,
                    Messages.OPEN_SUGGESTION_HANDLER_CONVERSION_FAILURE.getText(),
                    new Status(IStatus.ERROR, Activator.PLUGIN_ID, e
                            .getLocalizedMessage()));
        }
        return null;
    }
}
