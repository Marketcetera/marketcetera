package org.marketcetera.photon.internal.positions.ui;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import net.miginfocom.swt.MigLayout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IMemento;
import org.marketcetera.core.position.Grouping;
import org.marketcetera.core.position.PositionEngine;
import org.marketcetera.core.position.PositionRow;
import org.marketcetera.core.position.PositionEngine.PositionData;
import org.marketcetera.photon.commons.ui.table.ColumnState;
import org.marketcetera.photon.internal.positions.ui.glazed.EventTreeModel;
import org.marketcetera.photon.internal.positions.ui.glazed.EventTreeViewer;
import org.marketcetera.photon.internal.positions.ui.glazed.TreeComparatorChooser;
import org.marketcetera.photon.internal.positions.ui.glazed.TreeItemConfigurer;
import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;

/* $License$ */

/**
 * Hierarchical tree based positions view page.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class PositionsViewTreePage extends PositionsViewPage {

	private static final String TREE_SORT_STATE_KEY = "treeSortState"; //$NON-NLS-1$

	private final class PositionRowConfigurer implements TreeItemConfigurer<PositionRow> {

		@Override
		public void configure(TreeItem item, PositionRow rowValue, Object columnValue, int row,
				int column) {
			String text;
			if (column == 0) {
				if (rowValue.getChildren() == null) {
					// can't use columnValue (grouping) since it isn't set on the bottom
					// (non-summary) row
					EnumSet<Grouping> groupings = EnumSet.allOf(Grouping.class);
					groupings.removeAll(Arrays.asList(getView().getGrouping()));
					columnValue = groupings.iterator().next().get(rowValue);
				}
				text = formatKey((String) columnValue);
			} else {
				text = formatBigDecimal((BigDecimal) columnValue);
			}
			item.setText(column, text);
		}
	}

	private EventTreeViewer<PositionRow> mViewer;
	private Tree mTree;
	private TreeComparatorChooser<PositionRow> mChooser;

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
	public PositionsViewTreePage(PositionsView view, IMemento memento) {
		super(view, memento);
	}

	@Override
	protected Control doCreateControl(Composite parent, IMemento memento) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new MigLayout("fill, ins 0")); //$NON-NLS-1$
		mTree = new Tree(composite, SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.VIRTUAL);
		mTree.setLayoutData("dock center, hmin 100, wmin 100"); //$NON-NLS-1$
		TableFormat<PositionRow> tableFormat = GlazedLists.tableFormat(PositionRow.class,
				new String[] { "grouping", //$NON-NLS-1$
						"positionMetrics.position", //$NON-NLS-1$
						"positionMetrics.incomingPosition", //$NON-NLS-1$
						"positionMetrics.positionPL", //$NON-NLS-1$ 
						"positionMetrics.tradingPL", //$NON-NLS-1$ 
						"positionMetrics.realizedPL", //$NON-NLS-1$
						"positionMetrics.unrealizedPL", //$NON-NLS-1$ 
						"positionMetrics.totalPL" //$NON-NLS-1$ 
				}, new String[] { Messages.POSITIONS_TABLE_GROUPING_COLUMN_HEADING.getText(),
						Messages.POSITIONS_TABLE_POSITION_COLUMN_HEADING.getText(),
						Messages.POSITIONS_TABLE_INCOMING_COLUMN_HEADING.getText(),
						Messages.POSITIONS_TABLE_POSITION_PL_COLUMN_HEADING.getText(),
						Messages.POSITIONS_TABLE_TRADING_PL_COLUMN_HEADING.getText(),
						Messages.POSITIONS_TABLE_REALIZED_PL_COLUMN_HEADING.getText(),
						Messages.POSITIONS_TABLE_UNREALIZED_PL_COLUMN_HEADING.getText(),
						Messages.POSITIONS_TABLE_TOTAL_PL_COLUMN_HEADING.getText() });
		EventList<PositionRow> positions = getPositions();
		SortedList<PositionRow> sorted = new SortedList<PositionRow>(positions, null);
		mViewer = new EventTreeViewer<PositionRow>(sorted, mTree, tableFormat,
				new EventTreeModel<PositionRow>() {

					@Override
					public EventList<PositionRow> getChildren(PositionRow item) {
						return item.getChildren();
					}
				}, new PositionRowConfigurer());
		// make grouping unrealized PL a bit wider
		mTree.getColumn(0).setWidth(150);
		mTree.getColumn(6).setWidth(90);
		mChooser = TreeComparatorChooser.install(mViewer, sorted, false);
		if (memento != null) {
			ColumnState.restore(mTree, memento);
			String sortState = memento.getString(TREE_SORT_STATE_KEY);
			if (sortState != null) {
				mChooser.fromString(sortState);
			}
		}
		for (int i = 1; i < mTree.getColumnCount(); i++) {
			TreeColumn column = mTree.getColumn(i);
			column.setMoveable(true);
			if (column.getWidth() == 0) {
				column.setResizable(false);
			}
		}
		return composite;

	}

	@Override
	public Control getColumnWidget() {
		return mTree;
	}

	@Override
	protected void controlDisposed() {
		mViewer.dispose();
	}

	@Override
	protected PositionData getPositionData() {
		PositionEngine engine = Activator.getDefault().getPositionEngine();
		return engine.getGroupedData(getView().getGrouping());
	}

	@Override
	protected TextFilterator<? super PositionRow> getFilterator() {
		return new TextFilterator<PositionRow>() {

			@Override
			public void getFilterStrings(List<String> baseList, PositionRow element) {
				baseList.add(getView().getGrouping()[0].get(element));
			}
		};
	}

	@Override
	public void saveState(IMemento memento) {
		ColumnState.save(mTree, memento);
		memento.putString(TREE_SORT_STATE_KEY, mChooser.toString());
	}

}
