package org.marketcetera.photon.internal.positions.ui;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.part.Page;
import org.marketcetera.core.position.PositionMetrics;
import org.marketcetera.core.position.PositionRow;
import org.marketcetera.core.position.PositionEngine.PositionData;
import org.marketcetera.photon.commons.ui.table.ChooseColumnsMenu.IColumnProvider;
import org.marketcetera.photon.positions.ui.IPositionLabelProvider;
import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.SearchEngineTextMatcherEditor;
import ca.odell.glazedlists.util.concurrent.ReadWriteLock;

/* $License$ */

/**
 * Abstract base class of pages in the Positions view.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public abstract class PositionsViewPage extends Page implements IColumnProvider {

	private final PositionsView mView;

	private final PositionData mPositionData;

	private final FilterList<PositionRow> mFilteredList;

	private final SearchEngineTextMatcherEditor<PositionRow> mMatcherEditor;

	private final ReadWriteLock mLock;

	private final IMemento mMemento;

	private Control mControl;

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
	public PositionsViewPage(PositionsView view, IMemento memento) {
		Validate.notNull(view);
		mView = view;
		mMemento = memento;
		mPositionData = getPositionData();
		EventList<PositionRow> positions = mPositionData.getPositions();
		mLock = positions.getReadWriteLock();
		mLock.writeLock().lock();
		try {
			mFilteredList = new FilterList<PositionRow>(positions);
			mMatcherEditor = new SearchEngineTextMatcherEditor<PositionRow>(getFilterator());
			mFilteredList.setMatcherEditor(mMatcherEditor);
			setFilterText(mView.getFilterText());
		} finally {
			mLock.writeLock().unlock();
		}
	}

	@Override
	public void createControl(Composite parent) {
		mLock.writeLock().lock();
		try {
			mControl = doCreateControl(parent, mMemento);
			mControl.addDisposeListener(new DisposeListener() {

				@Override
				public void widgetDisposed(DisposeEvent e) {
					controlDisposed();
					mFilteredList.dispose();
					mPositionData.dispose();
				}
			});
		} finally {
			mLock.writeLock().unlock();
		}
	}

	/**
	 * Returns the view this page is a part of.
	 * 
	 * @return returns the view
	 */
	public PositionsView getView() {
		return mView;
	}

	/**
	 * Returns the list of positions, filtered according to the view's filter box.
	 * 
	 * @return returns the position list
	 */
	public EventList<PositionRow> getPositions() {
		return mFilteredList;
	}

	/**
	 * Callback when the control returned by {@link #createControl(Composite)} is disposed.
	 * Subclasses can override to perform cleanup.
	 * 
	 * Subclasses can also override {@link #dispose()}, but by then the control and all children are
	 * invalid.
	 */
	protected void controlDisposed() {
		// no op
	}

	@Override
	public Control getControl() {
		return mControl;
	}

	@Override
	public void setFocus() {
		if (mControl != null && !mControl.isFocusControl()) {
			mControl.setFocus();
		}
	}

	/**
	 * Returns the position data that page will display. The base class takes responsibility for
	 * disposing it when the page is closed.
	 * 
	 * @return the position data for the page, must not be null
	 */
	protected abstract PositionData getPositionData();

	/**
	 * Returns the filterator that should be used to control filtering of position rows.
	 * 
	 * @return a text filterator, must not be null
	 */
	protected abstract TextFilterator<? super PositionRow> getFilterator();

	/**
	 * Creates the control for this positions view page.
	 * 
	 * @param parent
	 *            the parent composite in which to create the control
	 * @return the new page control, must not be null
	 */
	protected abstract Control doCreateControl(Composite parent, IMemento memento);

	/**
	 * Saves page state.
	 * 
	 * @param memento
	 *            the memento of saved state
	 */
	public abstract void saveState(IMemento memento);

	/**
	 * Applies a filter text to this page. The page will reduce the number of items displayed
	 * according to the filter text.
	 * 
	 * @param filterText
	 *            the filter text, should not be null
	 * @throws IllegalArgumentException
	 *             if filterText is null
	 */
	public void setFilterText(String filterText) {
		Validate.notNull(filterText);
		mMatcherEditor.refilter(filterText);
	}

	protected static String formatKey(String s) {
		return s == null ? Messages.POSITIONS_TABLE_EMPTY_KEY.getText() : s;
	}

	protected static String getTraderName(String s) {
		if (s != null) {
			IPositionLabelProvider provider = Activator.getDefault().getPositionLabelProvider();
			if (provider != null) {
				s = provider.getTraderName(s);
			}
		}
		return s;
	}

	protected static String formatBigDecimal(BigDecimal n) {
		return n == null ? Messages.POSITIONS_TABLE_UNKNOWN_VALUE.getText() : n.setScale(2,
				BigDecimal.ROUND_HALF_UP).toPlainString();
	}

	protected static class PositionMetricsFormat implements TableFormat<PositionRow> {
		private static final String[] COLUMN_NAMES = new String[] {
				Messages.POSITIONS_TABLE_POSITION_COLUMN_HEADING.getText(),
				Messages.POSITIONS_TABLE_INCOMING_COLUMN_HEADING.getText(),
				Messages.POSITIONS_TABLE_POSITION_PL_COLUMN_HEADING.getText(),
				Messages.POSITIONS_TABLE_TRADING_PL_COLUMN_HEADING.getText(),
				Messages.POSITIONS_TABLE_REALIZED_PL_COLUMN_HEADING.getText(),
				Messages.POSITIONS_TABLE_UNREALIZED_PL_COLUMN_HEADING.getText(),
				Messages.POSITIONS_TABLE_TOTAL_PL_COLUMN_HEADING.getText() };

		private int mOffset;

		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		public PositionMetricsFormat(int offset) {
			mOffset = offset;
		}

		public String getColumnName(int column) {
			return COLUMN_NAMES[column - mOffset];
		}

		/**
		 * @see TableFormat#getColumnValue(java.lang.Object, int)
		 * @throws IllegalArgumentException
		 *             if column is not in the expected range
		 */
		public Object getColumnValue(PositionRow baseObject, int column) {
			PositionMetrics metrics = baseObject.getPositionMetrics();
			switch (column - mOffset) {
			case 0:
				return metrics.getPosition();
			case 1:
				return metrics.getIncomingPosition();
			case 2:
				return metrics.getPositionPL();
			case 3:
				return metrics.getTradingPL();
			case 4:
				return metrics.getRealizedPL();
			case 5:
				return metrics.getUnrealizedPL();
			case 6:
				return metrics.getTotalPL();
			default:
				throw new IllegalArgumentException(String.valueOf(column));
			}
		}
	}

	protected abstract static class PositionSelectionProvider extends Viewer {
		private final Control mControl;

		public PositionSelectionProvider(Control control) {
			Validate.notNull(control);
			mControl = control;
			OpenStrategy handler = new OpenStrategy(control);
			handler.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					handleSelect(e);
				}
			});
		}

		@Override
		public Control getControl() {
			return mControl;
		}

		@Override
		public ISelection getSelection() {
			if (mControl.isDisposed()) {
				return StructuredSelection.EMPTY;
			}
			List<PositionRow> list = getSelectionFromWidget();
			return new StructuredSelection(list);
		}

		protected abstract List<PositionRow> getSelectionFromWidget();

		private void handleSelect(SelectionEvent event) {
			// handle case where an earlier selection listener disposed the control.
			if (!mControl.isDisposed()) {
				updateSelection(getSelection());
			}
		}

		private void updateSelection(ISelection selection) {
			SelectionChangedEvent event = new SelectionChangedEvent(this, selection);
			fireSelectionChanged(event);
		}

		@Override
		public void setSelection(ISelection selection, boolean reveal) {
			// not used
		}

		@Override
		public void setInput(Object input) {
			// not using input
		}

		@Override
		public void refresh() {
			// no op
		}

		@Override
		public Object getInput() {
			// not using input provider
			return null;
		}
	}

}
