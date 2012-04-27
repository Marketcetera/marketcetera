package org.marketcetera.photon.internal.marketdata.ui;

import java.text.MessageFormat;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.marketcetera.photon.marketdata.ui.MarketDataUI;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Handles {@link MarketDataUI#SHOW_MARKET_DEPTH_COMMAND_ID} by launching a Market Depth view for
 * the symbol and source specified in the command parameters.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class ShowMarketDepthHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final String symbol = (String) event
				.getParameter(MarketDataUI.SHOW_MARKET_DEPTH_COMMAND_SYMBOL_PARAMETER);
		final String source = (String) event
				.getParameter(MarketDataUI.SHOW_MARKET_DEPTH_COMMAND_SOURCE_PARAMETER);
		final IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindowChecked(event)
				.getActivePage();
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				try {
					page.showView(MarketDepthView.ID, MessageFormat.format("{0},{1}", //$NON-NLS-1$ 
							symbol, source), IWorkbenchPage.VIEW_ACTIVATE);
				} catch (PartInitException e) {
					Messages.SHOW_MARKET_DEPTH_HANDLER_UNABLE_TO_SHOW_VIEW.error(
							ShowMarketDepthHandler.this, e, symbol, source);
				}
			}
		});
		return null;
	}

}
