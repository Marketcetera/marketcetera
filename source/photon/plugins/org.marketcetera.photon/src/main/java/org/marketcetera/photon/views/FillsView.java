package org.marketcetera.photon.views;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.position.Grouping;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.messagehistory.TradeReportsHistory;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.UserNameService;
import org.marketcetera.photon.actions.OpenAdditionalViewAction;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.UserID;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.util.concurrent.Lock;

/* $License$ */

/**
 * View which shows filled orders.
 * 
 * @author gmiller
 * @author michael.lossos@softwaregoodness.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 */
@ClassVersion("$Id$")
public class FillsView extends AbstractFIXMessagesView {
	private static final String VALUE_ATTRIBUTE = "value"; //$NON-NLS-1$
	private static final String GROUPING_ATTRIBUTE = "grouping"; //$NON-NLS-1$
	private static final String FILTER_TAG = "filter"; //$NON-NLS-1$
	public static final String ID = "org.marketcetera.photon.views.FillsView"; //$NON-NLS-1$
	private FilterList<ReportHolder> mFilteredList;
	private Map<Grouping, String> mFilters;

	@Override
	protected String getViewID() {
		return ID;
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		if (memento != null) {
			IMemento[] filters = memento.getChildren(FILTER_TAG);
			if (filters.length > 0) {
				mFilters = new EnumMap<Grouping, String>(Grouping.class);
				for (IMemento filter : filters) {
					mFilters.put(Grouping.valueOf(filter.getString(GROUPING_ATTRIBUTE)), filter.getString(VALUE_ATTRIBUTE));
				}
			}
		}
		updatePartName();
	}

	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		if (mFilters != null) {
			for (Map.Entry<Grouping, String> entry : mFilters.entrySet()) {
				IMemento filter = memento.createChild(FILTER_TAG);
				filter.putString(GROUPING_ATTRIBUTE, entry.getKey().name());
				filter.putString(VALUE_ATTRIBUTE, entry.getValue());
			}
		}
	}

	@Override
	protected void initializeToolBar(IToolBarManager inTheToolBarManager) {
		super.initializeToolBar(inTheToolBarManager);
		inTheToolBarManager.add(new OpenAdditionalViewAction(getViewSite().getWorkbenchWindow(),
				FILLS_VIEW_LABEL.getText(), ID));
	}

	@Override
	protected EventList<ReportHolder> getMessageList(TradeReportsHistory inHistory) {
		EventList<ReportHolder> base = inHistory.getFillsList();
		Lock lock = base.getReadWriteLock().readLock();
		lock.lock();
		try {
			mFilteredList = new FilterList<ReportHolder>(base, createMatcher(mFilters));
			return mFilteredList;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Filters the view's base list. Further filtering can still be done by the super class text
	 * box.
	 * 
	 * @param filters
	 *            the grouping keys and values used to filter
	 */
	public void setFillsFilter(Map<Grouping, String> filters) {
		if (filters != null && filters.isEmpty()) {
			filters = null;
		}
		mFilters = filters;
		mFilteredList.setMatcher(createMatcher(filters));
		updatePartName();
	}

	private void updatePartName() {
		if (mFilters == null) {
			setPartName(Messages.FILLS_VIEW_DEFAULT_LABEL.getText());
		} else {
			setPartName(getFilterLabel(mFilters));
		}
	}

	private Matcher<ReportHolder> createMatcher(final Map<Grouping, String> filters) {
		// no matcher if there are no filters
		if (filters == null) {
			return null;
		}
		return new Matcher<ReportHolder>() {

			@Override
			public boolean matches(ReportHolder item) {
				ExecutionReport report = (ExecutionReport) item.getReport();
				EqualsBuilder builder = new EqualsBuilder();
				// only need to match fields that are being filtered
				if (filters.containsKey(Grouping.Symbol)) {
					builder.append(filters.get(Grouping.Symbol), report.getSymbol().toString());
				}
				if (filters.containsKey(Grouping.Account)) {
					builder.append(filters.get(Grouping.Account), report.getAccount());
				}
				if (filters.containsKey(Grouping.Trader)) {
					UserID viewer = report.getViewerID();
					builder.append(filters.get(Grouping.Trader), viewer == null ? null : viewer.toString());
				}
				return builder.isEquals();
			}
		};
	}

	private String getFilterLabel(final Map<Grouping, String> filters) {
		// use a ToStringBuilder for convenience (brackets, commas, equals sign, etc)
		ToStringBuilder builder = new ToStringBuilder(filters, new FilterStyle());
		for (Map.Entry<Grouping, String> entry : filters.entrySet()) {
			builder.append(getLabel(entry.getKey()), getValue(entry));
		}
		return Messages.FILLS_VIEW_POSITION_FILLS_LABEL.getText() + builder;
	}

	private String getValue(Entry<Grouping, String> entry) {
		String value = entry.getValue();
		if (value == null) {
			value = Messages.FILLS_VIEW_UNKNOWN_FILTER_VALUE.getText();
		} else if (entry.getKey() == Grouping.Trader) {
			value = UserNameService.getUserName(value);
		}
		return value;
	}

	private String getLabel(Grouping key) {
		switch (key) {
		case Symbol:
			return Messages.FILLS_VIEW_SYMBOL_FILTER_LABEL.getText();
		case Account:
			return Messages.FILLS_VIEW_ACCOUNT_FILTER_LABEL.getText();
		case Trader:
			return Messages.FILLS_VIEW_TRADER_FILTER_LABEL.getText();
		default:
			// need a label for new enum type
			assert false : key;
			// using name is okay if assertions are disabled
			return key.name();
		}
	}

	private static final class FilterStyle extends ToStringStyle {
		private static final long serialVersionUID = 1L;

		public FilterStyle() {
			setUseClassName(false);
			setUseIdentityHashCode(false);
		}
	}
}
