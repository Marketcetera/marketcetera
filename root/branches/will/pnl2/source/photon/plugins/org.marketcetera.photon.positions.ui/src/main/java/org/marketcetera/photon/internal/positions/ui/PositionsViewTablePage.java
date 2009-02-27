package org.marketcetera.photon.internal.positions.ui;

import java.math.BigDecimal;
import java.util.List;

import net.miginfocom.swt.MigLayout;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.marketcetera.core.position.PositionEngine;
import org.marketcetera.core.position.PositionRow;
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

	private SearchEngineTextMatcherEditor<PositionRow> matcherEditor;

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
	protected Control doCreateControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new MigLayout("fill, ins 0")); //$NON-NLS-1$
		Table table = new Table(composite, SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL);
		table.setLayoutData("dock center, hmin 100, wmin 100"); //$NON-NLS-1$
		EventList<PositionRow> positions = getPositions();
		FilterList<PositionRow> filtered = new FilterList<PositionRow>(positions);
		matcherEditor = new SearchEngineTextMatcherEditor<PositionRow>(filterator);
		filtered.setMatcherEditor(matcherEditor);
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
		return composite;

	}

	private EventList<PositionRow> getPositions() {
		PositionEngine engine = Activator.getDefault().getPositionEngine();
		return engine.getFlatData().getPositions();
	}
	private static final TextFilterator<PositionRow> filterator = new TextFilterator<PositionRow>() {

		@Override
		public void getFilterStrings(List<String> baseList, PositionRow element) {
			baseList.add(element.getAccount());
			baseList.add(element.getSymbol());
			baseList.add(element.getTraderId());
		}
	};
	
	@Override
	public void setFilterText(String filterText) {
		matcherEditor.refilter(filterText);
	}

	

}
