package org.marketcetera.photon.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.services.IServiceLocator;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.marketdata.MarketDataManager;
import org.marketcetera.photon.marketdata.ui.MarketDataUI;
import org.marketcetera.photon.ui.ISymbolProvider;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ImmutableMap;

/* $License$ */

/**
 * Menu for choosing market depth sources.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class MarketDepthMenu extends CompoundContributionItem {

	@Override
	protected IContributionItem[] getContributionItems() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		ISelection selection = window.getSelectionService().getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structured = (IStructuredSelection) selection;
			Object selected = structured.getFirstElement();
			if (selected instanceof ISymbolProvider) {
				MSymbol symbol = ((ISymbolProvider) selected).getSymbol();
				Set<Capability> capabilities = MarketDataManager.getCurrent()
						.getActiveFeedCapabilities();
				List<IContributionItem> items = new ArrayList<IContributionItem>(3);
				if (capabilities.contains(Capability.LEVEL_2)) {
					items.add(createCommand(window, symbol.getFullSymbol(), Capability.LEVEL_2,
							Messages.MARKET_DEPTH_LEVEL_2_LABEL.getText(),
							Messages.MARKET_DEPTH_LEVEL_2_MNEMONIC.getText()));
				}
				if (capabilities.contains(Capability.TOTAL_VIEW)) {
					items.add(createCommand(window, symbol.getFullSymbol(), Capability.TOTAL_VIEW,
							Messages.MARKET_DEPTH_TOTAL_VIEW_LABEL.getText(),
							Messages.MARKET_DEPTH_TOTAL_VIEW_MNEMONIC.getText()));
				}
				if (capabilities.contains(Capability.OPEN_BOOK)) {
					items.add(createCommand(window, symbol.getFullSymbol(), Capability.OPEN_BOOK,
							Messages.MARKET_DEPTH_OPEN_BOOK_LABEL.getText(),
							Messages.MARKET_DEPTH_OPEN_BOOK_MNEMONIC.getText()));
				}
				return items.toArray(new IContributionItem[items.size()]);

			}
		}
		return new IContributionItem[] {};
	}

	private CommandContributionItem createCommand(IServiceLocator window, String symbol,
			Capability capability, String label, String mnemonic) {
		CommandContributionItemParameter parameter = new CommandContributionItemParameter(window,
				null, MarketDataUI.SHOW_MARKET_DEPTH_COMMAND_ID, CommandContributionItem.STYLE_PUSH);
		parameter.parameters = ImmutableMap.of(
				MarketDataUI.SHOW_MARKET_DEPTH_COMMAND_SYMBOL_PARAMETER, symbol,
				MarketDataUI.SHOW_MARKET_DEPTH_COMMAND_SOURCE_PARAMETER, capability.name());
		parameter.label = label;
		parameter.mnemonic = mnemonic;
		return new CommandContributionItem(parameter);
	}
}
