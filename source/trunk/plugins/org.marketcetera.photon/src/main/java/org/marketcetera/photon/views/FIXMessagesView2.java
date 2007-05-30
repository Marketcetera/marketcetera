package org.marketcetera.photon.views;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.marketcetera.photon.actions.ShowHeartbeatsAction;
import org.marketcetera.photon.core.FIXMatcher;
import org.marketcetera.photon.core.FIXMessageHistory;
import org.marketcetera.photon.core.MessageHolder;
import org.marketcetera.photon.ui.EventListContentProvider;
import org.marketcetera.photon.ui.FIXMessageTableFormat;
import org.marketcetera.photon.ui.IndexedTableViewer;

import quickfix.field.MsgType;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.matchers.Matcher;

/**
 * FIX Messages view.
 * 
 * @author gmiller
 * @author andrei@lissovski.org
 */
public class FIXMessagesView2 extends HistoryMessagesView implements
		IHeartbeatsToggle {

	public static final String ID = "org.marketcetera.photon.views.FIXMessagesView2";

	private static final Matcher<? super MessageHolder> HEARTBEAT_MATCHER = new FIXMatcher<String>(
			MsgType.FIELD, MsgType.HEARTBEAT, false); // filters out heartbeat

	// messages

	private ShowHeartbeatsAction showHeartbeatsAction;

	private static final String SHOW_HEARTBEATS_SAVED_STATE_KEY = "SHOW_HEARTBEATS";

	protected Enum[] getEnumValues() {
		return null;
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		/**
		 * doing it here and using the state from the action since the message
		 * list is not yet available by the time init() is called
		 */
		setShowHeartbeats(showHeartbeatsAction.isChecked());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite,
	 *      org.eclipse.ui.IMemento)
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);

		showHeartbeatsAction = new ShowHeartbeatsAction(this);

		boolean showHeartbeats = false; // filter out the heartbeats by default
		if (memento != null // can be null if there is no previous saved state
				&& memento.getInteger(SHOW_HEARTBEATS_SAVED_STATE_KEY) != null) {
			showHeartbeats = memento
					.getInteger(SHOW_HEARTBEATS_SAVED_STATE_KEY).intValue() != 0;
		}
		showHeartbeatsAction.setChecked(showHeartbeats);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		memento.putInteger(SHOW_HEARTBEATS_SAVED_STATE_KEY,
				showHeartbeatsAction.isChecked() ? 1 : 0);
	}

	protected void initializeToolBar(IToolBarManager theToolBarManager) {
		// theToolBarManager.add(new TextContributionItem(""));
		theToolBarManager.add(showHeartbeatsAction);
	}

	@Override
	public void setFocus() {
	}

	@SuppressWarnings("unchecked")
	protected FilterList<MessageHolder> getFilterList() {
		return (FilterList<MessageHolder>) getInput();
	}

	public EventList<MessageHolder> extractList(FIXMessageHistory input) {
		FilterList<MessageHolder> filterList = new FilterList<MessageHolder>(
				input.getAllMessagesList());
		return filterList;
	}

	public void setShowHeartbeats(boolean shouldShow) {
		FilterList<MessageHolder> list = getFilterList();
		if (shouldShow) {
			list.setMatcher(null); // no filtering
		} else {
			list.setMatcher(HEARTBEAT_MATCHER); // filter out heartbeats
		}
		getMessagesViewer().refresh();
	}

	@Override
	protected IndexedTableViewer createTableViewer(Table aMessageTable,
			Enum[] enums) {
		IndexedTableViewer aMessagesViewer = new IndexedTableViewer(
				aMessageTable);
		getSite().setSelectionProvider(aMessagesViewer);
		aMessagesViewer
				.setContentProvider(new EventListContentProvider<MessageHolder>());
		// todo: Add back the message direction column.
		// aMessagesViewer.setLabelProvider(new
		// DirectionalMessageTableFormat(aMessageTable, enums, getSite()));
		aMessagesViewer
				.setLabelProvider(new FIXMessageTableFormat<MessageHolder>(
						aMessageTable, FIXMessagesView.ID, MessageHolder.class));
		return aMessagesViewer;
	}

	@Override
	protected void packColumns(Table table) {
		super.packColumns(table);
	}
}
