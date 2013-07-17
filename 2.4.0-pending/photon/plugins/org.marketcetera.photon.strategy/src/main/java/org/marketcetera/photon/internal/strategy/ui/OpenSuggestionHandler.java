package org.marketcetera.photon.internal.strategy.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.internal.strategy.TradeSuggestion;
import org.marketcetera.photon.internal.strategy.TradeSuggestionManager;
import org.marketcetera.photon.strategy.StrategyUI;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

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
            PhotonPlugin.getDefault().showOrderInTicket(order);
            TradeSuggestionManager.getCurrent().removeSuggestion(suggestion);
        } catch (WorkbenchException e) {
            SLF4JLoggerProxy.error(this, e);
            Shell shell = HandlerUtil.getActiveShellChecked(event);
            ErrorDialog.openError(shell, null, null, new Status(IStatus.ERROR,
                    StrategyUI.PLUGIN_ID, e.getLocalizedMessage()));
        }
        return null;
    }
}
