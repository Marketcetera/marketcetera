package org.marketcetera.photon.internal.positions.ui;

import java.math.BigDecimal;
import java.util.List;

import net.miginfocom.swt.MigLayout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IMemento;
import org.marketcetera.core.position.PositionEngine;
import org.marketcetera.core.position.PositionRow;
import org.marketcetera.core.position.PositionEngine.PositionData;
import org.marketcetera.photon.commons.ui.table.ColumnState;
import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swt.EventTableViewer;
import ca.odell.glazedlists.swt.TableComparatorChooser;
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

	private static final String TABLE_SORT_STATE_KEY = "tableSortState"; //$NON-NLS-1$

	private static final class PositionRowConfigurer implements TableItemConfigurer<PositionRow> {
		@Override
		public void configure(TableItem item, PositionRow rowValue, Object columnValue, int row,
				int column) {
			String text;
			if (column <= 1) {
				text = formatKey((String) columnValue);
			} else if (column == 2) {
				text = formatKey(getTraderName((String) columnValue));
			} else {
				text = formatBigDecimal((BigDecimal) columnValue);
			}
			item.setText(column, text);
		}
	}

	private EventTableViewer<PositionRow> mViewer;
	private Table mTable;
	private TableComparatorChooser<PositionRow> mChooser;

	/**
	 * Constructor.
	 * 
	 * @param view
	 *            the view this page is part of, cannot be null
	 * @param memento
	 *            the saved page state
	 * @throws IllegalArgumentException
	 *             if view is null
	 */
	public PositionsViewTablePage(PositionsView view, IMemento memento) {
		super(view, memento);
	}

	@Override
	protected Control doCreateControl(Composite parent, IMemento memento) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new MigLayout("fill, ins 0")); //$NON-NLS-1$
		mTable = new Table(composite, SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.VIRTUAL);
		mTable.setLayoutData("dock center, hmin 100, wmin 100"); //$NON-NLS-1$
		TableFormat<PositionRow> tableFormat = GlazedLists.tableFormat(PositionRow.class,
				new String[] { "symbol", //$NON-NLS-1$  
						"account", //$NON-NLS-1$ 
						"traderId", //$NON-NLS-1$ 
						"positionMetrics.position", //$NON-NLS-1$
						"positionMetrics.incomingPosition", //$NON-NLS-1$
						"positionMetrics.positionPL", //$NON-NLS-1$ 
						"positionMetrics.tradingPL", //$NON-NLS-1$ 
						"positionMetrics.realizedPL", //$NON-NLS-1$
						"positionMetrics.unrealizedPL", //$NON-NLS-1$ 
						"positionMetrics.totalPL" //$NON-NLS-1$ 
				}, new String[] { Messages.POSITIONS_TABLE_SYMBOL_COLUMN_HEADING.getText(),
						Messages.POSITIONS_TABLE_ACCOUNT_COLUMN_HEADING.getText(),
						Messages.POSITIONS_TABLE_TRADER_COLUMN_HEADING.getText(),
						Messages.POSITIONS_TABLE_POSITION_COLUMN_HEADING.getText(),
						Messages.POSITIONS_TABLE_INCOMING_COLUMN_HEADING.getText(),
						Messages.POSITIONS_TABLE_POSITION_PL_COLUMN_HEADING.getText(),
						Messages.POSITIONS_TABLE_TRADING_PL_COLUMN_HEADING.getText(),
						Messages.POSITIONS_TABLE_REALIZED_PL_COLUMN_HEADING.getText(),
						Messages.POSITIONS_TABLE_UNREALIZED_PL_COLUMN_HEADING.getText(),
						Messages.POSITIONS_TABLE_TOTAL_PL_COLUMN_HEADING.getText() });
		EventList<PositionRow> positions = getPositions();
		SortedList<PositionRow> sorted = new SortedList<PositionRow>(positions, null);
		mViewer = new EventTableViewer<PositionRow>(sorted, mTable, tableFormat,
				new PositionRowConfigurer());
		mChooser = TableComparatorChooser.install(mViewer, sorted, false);
		// make unrealized PL a bit wider
		mTable.getColumn(8).setWidth(90);
		if (memento != null) {
			ColumnState.restore(mTable, memento);
			String sortState = memento.getString(TABLE_SORT_STATE_KEY);
			if (sortState != null) {
				mChooser.fromString(sortState);
			}
		}
		for (TableColumn column : mTable.getColumns()) {
			column.setMoveable(true);
			if (column.getWidth() == 0) {
				column.setResizable(false);
			}
		}
		return composite;
	}

	@Override
	public Control getColumnWidget() {
		return mTable;
	}

	@Override
	protected void controlDisposed() {
		mViewer.dispose();
	}

	@Override
	protected PositionData getPositionData() {
		PositionEngine engine = Activator.getDefault().getPositionEngine();
		return engine.getFlatData();
	}

	@Override
	protected TextFilterator<PositionRow> getFilterator() {
		return new TextFilterator<PositionRow>() {

			@Override
			public void getFilterStrings(List<String> baseList, PositionRow element) {
				// search symbol, account, and traderId (columns 0, 1, and 2)
				for (int i = 0; i <= 2; i++) {
					TableColumn column = mTable.getColumn(i);
					if (column.getWidth() > 0) {
						// use the column text instead of the row element value since it may be
						// decorated
						baseList.add(column.getText());
					}
				}
			}
		};
	}

	@Override
	public void saveState(IMemento memento) {
		ColumnState.save(mTable, memento);
		memento.putString(TABLE_SORT_STATE_KEY, mChooser.toString());
	}

}
