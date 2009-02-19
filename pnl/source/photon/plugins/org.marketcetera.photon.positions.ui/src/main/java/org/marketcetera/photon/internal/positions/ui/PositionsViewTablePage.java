package org.marketcetera.photon.internal.positions.ui;

import java.math.BigDecimal;
import java.util.List;

import net.miginfocom.swt.MigLayout;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.IPageSite;
import org.marketcetera.core.position.PositionEngine;
import org.marketcetera.core.position.PositionRow;
import org.marketcetera.photon.commons.ui.FilterBox;
import org.marketcetera.photon.commons.ui.FilterBox.FilterChangeEvent;
import org.marketcetera.photon.commons.ui.FilterBox.FilterChangeListener;
import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.SearchEngineTextMatcherEditor;
import ca.odell.glazedlists.swt.EventTableViewer;
import ca.odell.glazedlists.swt.TableItemConfigurer;

/* $License$ */

/**
 * Flat table based positions view page.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class PositionsViewTablePage extends PositionsViewPage {

	private FilterList<PositionRow> filtered;

	private Composite mControl;

	/**
	 * Constructor.
	 * 
	 * @param view
	 *            the view this page is part of
	 */
	public PositionsViewTablePage(PositionsView view) {
		super(view);
	}

	@Override
	public void init(IPageSite pageSite) {
		super.init(pageSite);
		IToolBarManager toolbar = getSite().getActionBars().getToolBarManager();
		toolbar.add(new ControlContribution("filter") { //$NON-NLS-1$

					@Override
					protected Control createControl(Composite parent) {
						Composite composite = new Composite(parent, SWT.NONE);
						composite.setLayout(new MigLayout("ins 1")); //$NON-NLS-1$
						Label filterLabel = new Label(composite, SWT.NONE);
						filterLabel.setText(Messages.POSITIONS_VIEW_FILTER_LABEL.getText());
						FilterBox filter = new FilterBox(composite);
						filter.setInitialText(""); //$NON-NLS-1$
						if (filtered != null) {
							filtered.setMatcherEditor(new PositionsMatcherEditor(filter));
						}
						return composite;
					}
				});
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new MigLayout("fill, ins 0")); //$NON-NLS-1$
		Table table = new Table(composite, SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL);
		table.setLayoutData("dock center, hmin 100, wmin 100"); //$NON-NLS-1$
		EventList<PositionRow> positions = getPositions();
		filtered = new FilterList<PositionRow>(positions);
		new EventTableViewer<PositionRow>(filtered, table, GlazedLists.tableFormat(
				PositionRow.class, new String[] { "symbol", //$NON-NLS-1$  
						"account", //$NON-NLS-1$ 
						"traderId", //$NON-NLS-1$ 
						"positionMetrics.position", //$NON-NLS-1$
						"positionMetrics.positionPL", //$NON-NLS-1$ 
						"positionMetrics.tradingPL", //$NON-NLS-1$ 
						"positionMetrics.realizedPL", //$NON-NLS-1$
						"positionMetrics.unrealizedPL", //$NON-NLS-1$ 
						"positionMetrics.totalPL" //$NON-NLS-1$ 
				}, new String[] { Messages.POSITIONS_TABLE_SYMBOL_COLUMN_HEADING.getText(),
						Messages.POSITIONS_TABLE_ACCOUNT_COLUMN_HEADING.getText(),
						Messages.POSITIONS_TABLE_TRADER_COLUMN_HEADING.getText(),
						Messages.POSITIONS_TABLE_POSITION_COLUMN_HEADING.getText(),
						Messages.POSITIONS_TABLE_POSITION_PL_COLUMN_HEADING.getText(),
						Messages.POSITIONS_TABLE_TRADING_PL_COLUMN_HEADING.getText(),
						Messages.POSITIONS_TABLE_REALIZED_PL_COLUMN_HEADING.getText(),
						Messages.POSITIONS_TABLE_UNREALIZED_PL_COLUMN_HEADING.getText(),
						Messages.POSITIONS_TABLE_TOTAL_PL_COLUMN_HEADING.getText() }),
				new TableItemConfigurer<PositionRow>() {
					@Override
					public void configure(TableItem item, PositionRow rowValue, Object columnValue,
							int row, int column) {
						if (columnValue instanceof BigDecimal) {
							item.setText(column, round((BigDecimal) columnValue));
						} else {
							item.setText(column, ObjectUtils.toString(columnValue));
						}
					}

					private String round(BigDecimal n) {
						return n.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
					}

				});
		mControl = composite;

	}

	@Override
	public Control getControl() {
		return mControl;
	}

	@Override
	public void setFocus() {
		if (!mControl.isFocusControl()) {
			mControl.setFocus();
		}
	}

	private EventList<PositionRow> getPositions() {
		PositionEngine engine = Activator.getDefault().getPositionEngine();
		return engine.getFlatData().getPositions();
	}

	private static class PositionsMatcherEditor extends FilterBoxMatcherEditor<PositionRow> {

		private static final TextFilterator<PositionRow> filterator = new TextFilterator<PositionRow>() {

			@Override
			public void getFilterStrings(List<String> baseList, PositionRow element) {
				baseList.add(element.getAccount());
				baseList.add(element.getSymbol());
				baseList.add(element.getTraderId());
			}
		};

		public PositionsMatcherEditor(FilterBox filterBox) {
			super(filterBox, filterator);
		}

	}

	private static class FilterBoxMatcherEditor<E> extends SearchEngineTextMatcherEditor<E> {

		private final FilterBox filterBox;
		private final FilterChangeListener listener = new Listener();

		public FilterBoxMatcherEditor(FilterBox filterBox) {
			super();
			this.filterBox = filterBox;
			connect();
		}

		public FilterBoxMatcherEditor(FilterBox filterBox, TextFilterator<? super E> textFilterator) {
			super(textFilterator);
			this.filterBox = filterBox;
			connect();
		}

		private void connect() {
			filterBox.addListener(listener);
			refilter(filterBox.getFilterText());
		}

		public void dispose() {
			filterBox.removeListener(listener);
		}

		private class Listener implements FilterChangeListener {

			@Override
			public void filterChanged(FilterChangeEvent event) {
				refilter(event.getFilterText());
			}
		}

	}

}
