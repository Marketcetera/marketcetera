package org.marketcetera.photon.internal.strategy.ui;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.photon.commons.ui.table.ColumnConfiguration;
import org.marketcetera.photon.commons.ui.table.TableConfiguration;
import org.marketcetera.photon.commons.ui.table.TableSupport;
import org.marketcetera.photon.commons.ui.table.ChooseColumnsMenu.IColumnProvider;
import org.marketcetera.photon.internal.strategy.Messages;
import org.marketcetera.photon.internal.strategy.TradeSuggestion;
import org.marketcetera.photon.internal.strategy.TradeSuggestionManager;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * View for incoming trade suggestions.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class TradeSuggestionsView extends ViewPart implements IColumnProvider {

	private TableSupport mTableSupport = TableSupport
			.create(getTableConfiguration());

	@Override
	public void createPartControl(Composite parent) {
		mTableSupport.createTable(parent);
		mTableSupport.setInput(TradeSuggestionManager.getCurrent()
				.getTradeSuggestions());
		getViewSite().setSelectionProvider(mTableSupport.getTableViewer());
		hookContextMenu();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		Menu menu = menuMgr.createContextMenu(mTableSupport.getTableViewer()
				.getControl());
		mTableSupport.getTableViewer().getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, mTableSupport.getTableViewer());
	}

	@Override
	public void setFocus() {
		mTableSupport.setFocus();
	}

	@Override
	public Table getColumnWidget() {
		return mTableSupport.getTableViewer().getTable();
	}

	private TableConfiguration getTableConfiguration() {
		ColumnConfiguration[] columns = new ColumnConfiguration[] {
				ColumnConfiguration.hidden().beanProperty("identifier") //$NON-NLS-1$
						.heading(
								Messages.TRADE_SUGGESTION_IDENTIFIER_LABEL
										.getText()),
				ColumnConfiguration.defaults().beanProperty("side").heading( //$NON-NLS-1$
						Messages.TRADE_SUGGESTION_SIDE_LABEL.getText()),
				ColumnConfiguration.hidden().beanProperty("securityType") //$NON-NLS-1$
						.heading(
								Messages.TRADE_SUGGESTION_SECURITY_TYPE_LABEL
										.getText()),
				ColumnConfiguration.defaults().beanProperty("quantity") //$NON-NLS-1$
						.heading(
								Messages.TRADE_SUGGESTION_QUANTITY_LABEL
										.getText()),
				ColumnConfiguration.defaults().beanProperty("symbol").heading( //$NON-NLS-1$
						Messages.TRADE_SUGGESTION_SYMBOL_LABEL.getText()),
				ColumnConfiguration.defaults().beanProperty("price").heading( //$NON-NLS-1$
						Messages.TRADE_SUGGESTION_PRICE_LABEL.getText()),
				ColumnConfiguration.defaults().beanProperty("orderType") //$NON-NLS-1$
						.heading(
								Messages.TRADE_SUGGESTION_ORDER_TYPE_LABEL
										.getText()),
				ColumnConfiguration.hidden().beanProperty("timeInForce") //$NON-NLS-1$
						.heading(
								Messages.TRADE_SUGGESTION_TIME_IN_FORCE_LABEL
										.getText()),
				ColumnConfiguration.hidden().beanProperty("orderCapacity") //$NON-NLS-1$
						.heading(
								Messages.TRADE_SUGGESTION_ORDER_CAPACITY_LABEL
										.getText()),
				ColumnConfiguration.hidden().beanProperty("positionEffect") //$NON-NLS-1$
						.heading(
								Messages.TRADE_SUGGESTION_POSITION_EFFECT_LABEL
										.getText()),
				ColumnConfiguration.defaults().beanProperty("score").heading( //$NON-NLS-1$
						Messages.TRADE_SUGGESTION_SCORE_LABEL.getText()),
				ColumnConfiguration.hidden().beanProperty("account").heading( //$NON-NLS-1$
						Messages.TRADE_SUGGESTION_ACCOUNT_LABEL.getText()),
				ColumnConfiguration.hidden().beanProperty("brokerID") //$NON-NLS-1$
						.heading(
								Messages.TRADE_SUGGESTION_BROKER_ID_LABEL
										.getText()),
				ColumnConfiguration.defaults().beanProperty("timestamp") //$NON-NLS-1$
						.heading(
								Messages.TRADE_SUGGESTION_TIMESTAMP_LABEL
										.getText()).layoutData(
								new ColumnWeightData(25)) };

		return TableConfiguration.defaults().tableStyle(
				SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER)
				.headerVisible(true).itemClass(TradeSuggestion.class).columns(
						columns);
	}

}
