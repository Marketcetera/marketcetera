package org.marketcetera.photon.views;

import java.util.ArrayList;
import java.util.EnumSet;
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
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.IMarketDataManager;
import org.marketcetera.photon.marketdata.ui.MarketDataUI;
import org.marketcetera.photon.ui.ISymbolProvider;
import org.marketcetera.trade.Instrument;
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
public class MarketDepthMenu
        extends CompoundContributionItem
{
    /* (non-Javadoc)
     * @see org.eclipse.ui.actions.CompoundContributionItem#getContributionItems()
     */
    @Override
    protected IContributionItem[] getContributionItems()
    {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        ISelection selection = window.getSelectionService().getSelection();
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structured = (IStructuredSelection) selection;
            Object selected = structured.getFirstElement();
            if(selected instanceof ISymbolProvider) {
                Instrument instrument = ((ISymbolProvider)selected).getInstrument();
                List<IContributionItem> items = new ArrayList<IContributionItem>(3);
                IMarketDataManager marketDataManager = PhotonPlugin.getDefault().getMarketDataManager();
                Set<Capability> availableCapabilities;
                if(marketDataManager == null) {
                    availableCapabilities = EnumSet.allOf(Capability.class);
                } else {
                    availableCapabilities = marketDataManager.getAvailabilityCapability();
                }
                if(availableCapabilities.contains(Capability.LEVEL_2)) {
                    items.add(createCommand(window,
                                            instrument.getFullSymbol(),
                                            Capability.LEVEL_2,
                                            Messages.MARKET_DEPTH_LEVEL_2_LABEL.getText(),
                                            Messages.MARKET_DEPTH_LEVEL_2_MNEMONIC.getText()));
                }
                if(availableCapabilities.contains(Capability.TOTAL_VIEW)) {
                    items.add(createCommand(window,
                                            instrument.getFullSymbol(),
                                            Capability.TOTAL_VIEW,
                                            Messages.MARKET_DEPTH_TOTAL_VIEW_LABEL.getText(),
                                            Messages.MARKET_DEPTH_TOTAL_VIEW_MNEMONIC.getText()));
                }
                if(availableCapabilities.contains(Capability.OPEN_BOOK)) {
                    items.add(createCommand(window,
                                            instrument.getFullSymbol(),
                                            Capability.OPEN_BOOK,
                                            Messages.MARKET_DEPTH_OPEN_BOOK_LABEL.getText(),
                                            Messages.MARKET_DEPTH_OPEN_BOOK_MNEMONIC.getText()));
                }
                if(availableCapabilities.contains(Capability.BBO10)) {
                    items.add(createCommand(window,
                                            instrument.getFullSymbol(),
                                            Capability.BBO10,
                                            Messages.MARKET_DEPTH_BBO10_LABEL.getText(),
                                            Messages.MARKET_DEPTH_BBO10_MNEMONIC.getText()));
                }
                if(availableCapabilities.contains(Capability.AGGREGATED_DEPTH)) {
                    items.add(createCommand(window,
                                            instrument.getFullSymbol(),
                                            Capability.AGGREGATED_DEPTH,
                                            Messages.MARKET_DEPTH_AGGREGATED_DEPTH_LABEL.getText(),
                                            Messages.MARKET_DEPTH_AGGREGATED_DEPTH_MNEMONIC.getText()));
                }
                if(availableCapabilities.contains(Capability.UNAGGREGATED_DEPTH)) {
                    items.add(createCommand(window,
                                            instrument.getFullSymbol(),
                                            Capability.UNAGGREGATED_DEPTH,
                                            Messages.MARKET_DEPTH_UNAGGREGATED_DEPTH_LABEL.getText(),
                                            Messages.MARKET_DEPTH_UNAGGREGATED_DEPTH_MNEMONIC.getText()));
                }
                return items.toArray(new IContributionItem[items.size()]);
            }
        }
        return new IContributionItem[] {};
    }
    /**
     * Creates a command item.
     *
     * @param inWindow an <code>IServiceLocator</code> value
     * @param inSymbol a <code>String</code> value
     * @param inCapability a <code>Capability</code> value
     * @param inLabel a <code>String</code> value
     * @param inMnemonic a <code>String</code> value
     * @return a <code>CommandContributionItem</code> value
     */
    private CommandContributionItem createCommand(IServiceLocator inWindow,
                                                  String inSymbol,
                                                  Capability inCapability,
                                                  String inLabel,
                                                  String inMnemonic)
    {
        CommandContributionItemParameter parameter = new CommandContributionItemParameter(inWindow,
                                                                                          null,
                                                                                          MarketDataUI.SHOW_MARKET_DEPTH_COMMAND_ID,
                                                                                          CommandContributionItem.STYLE_PUSH);
        parameter.parameters = ImmutableMap.of(MarketDataUI.SHOW_MARKET_DEPTH_COMMAND_SYMBOL_PARAMETER,
                                               inSymbol,
                                               MarketDataUI.SHOW_MARKET_DEPTH_COMMAND_SOURCE_PARAMETER,
                                               inCapability.name());
        parameter.label = inLabel;
        parameter.mnemonic = inMnemonic;
        return new CommandContributionItem(parameter);
    }
}
