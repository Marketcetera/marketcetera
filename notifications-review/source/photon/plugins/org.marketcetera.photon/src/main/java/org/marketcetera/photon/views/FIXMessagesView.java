package org.marketcetera.photon.views;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.messagehistory.FIXMessageHistory;
import org.marketcetera.messagehistory.MessageHolder;
import org.marketcetera.photon.actions.OpenAdditionalViewAction;
import org.marketcetera.photon.actions.ShowHeartbeatsAction;
import org.marketcetera.photon.messagehistory.FIXMatcher;
import org.marketcetera.photon.messagehistory.FIXStringMatcher;
import org.marketcetera.photon.ui.DirectionalMessageTableFormat;
import org.marketcetera.photon.ui.FIXMessageTableFormat;

import quickfix.field.MsgType;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.matchers.Matcher;

/* $License$ */

/**
 * FIX Messages view.
 * 
 * @author gmiller
 * @author andrei@lissovski.org
 * @author michael.lossos@softwaregoodness.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class FIXMessagesView
    extends AbstractFIXMessagesView
    implements IHeartbeatsToggle
{
    private boolean mShowHeartbeat;
    public static final String ID = "org.marketcetera.photon.views.FIXMessagesView"; //$NON-NLS-1$
    /**
     * this matcher will show all messages except FIX field 35 type 0 messages, heartbeats
     */
    private static final Matcher<MessageHolder> NO_HEARTBEATS_MATCHER = new FIXStringMatcher(MsgType.FIELD,
                                                                                             MsgType.HEARTBEAT,
                                                                                             false);
	// messages

	private ShowHeartbeatsAction showHeartbeatsAction;

	private static final String SHOW_HEARTBEATS_SAVED_STATE_KEY = "SHOW_HEARTBEATS"; //$NON-NLS-1$

	@Override
	protected String getViewID() {
		return ID;
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
		                   getShowHeartbeat() ? 1 : 0);
	}

	protected void initializeToolBar(IToolBarManager theToolBarManager) {
	    super.initializeToolBar(theToolBarManager);
		theToolBarManager.add(showHeartbeatsAction);
        theToolBarManager.add(new OpenAdditionalViewAction(getViewSite().getWorkbenchWindow(),
                                                           FIX_MESSAGES_VIEW_LABEL.getText(),
                                                           ID));
	}
	@Override
	public void setFocus() {
	}

	protected FilterList<MessageHolder> getFilterList() {
		return (FilterList<MessageHolder>) getInput();
	}

	public EventList<MessageHolder> extractList(FIXMessageHistory input) {
		FilterList<MessageHolder> filterList = new FilterList<MessageHolder>(
				input.getAllMessagesList());
		return filterList;
	}

	public void setShowHeartbeats(boolean inShouldShow)
	{
	    mShowHeartbeat = inShouldShow;
        handleFilter(getFilterText());
	}

	@Override
	protected FIXMessageTableFormat<MessageHolder> createFIXMessageTableFormat(
			Table aMessageTable) {
		String viewID = getViewID();
		return new DirectionalMessageTableFormat<MessageHolder>(aMessageTable,
				viewID, MessageHolder.class);
	}
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.AbstractFIXMessagesView#getMessageList(org.marketcetera.messagehistory.FIXMessageHistory)
     */
    @Override
    protected FilterList<MessageHolder> getMessageList(FIXMessageHistory inHistory)
    {
        return new FilterList<MessageHolder>(inHistory.getAllMessagesList(),
                                             getFilterMatcherEditor());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.AbstractFIXMessagesView#createRegexMatcher(int, java.lang.String)
     */
    @Override
    protected Matcher<MessageHolder> createRegexMatcher(int inFixField,
                                                        String inValue)
    {
        return new HeartbeatMatcher(inFixField,
                                    inValue,
                                    super.createRegexMatcher(inFixField,
                                                             inValue));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.AbstractFIXMessagesView#createStringMatcher(int, java.lang.String)
     */
    @Override
    protected Matcher<MessageHolder> createStringMatcher(int inFixField,
                                                         String inValue)
    {
        return new HeartbeatMatcher(inFixField,
                                    inValue,
                                    super.createStringMatcher(inFixField,
                                                              inValue));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.AbstractFIXMessagesView#getDefaultMatcher()
     */
    @Override
    protected Matcher<MessageHolder> getDefaultMatcher()
    {
        if(getShowHeartbeat()) {
            return super.getDefaultMatcher();
        } else {
            return NO_HEARTBEATS_MATCHER;
        }
    }
    /**
     * {@link Matcher} implementation that chains another <code>Matcher</code> with
     * the state of the heartbeat widget.
     * 
     * <p>The parent <code>Matcher</code> that is supplied to this object upon creation
     * takes precedence over the heartbeat check.  If the parent <code>Matcher</code>
     * decides that a given row does not match, that decision stands.  If the parent
     * <code>Matcher</code> does match a row, the state of the heartbeat widget is
     * then considered in the final decision.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$") //$NON-NLS-1$
    private class HeartbeatMatcher
        extends FIXMatcher<String>
    {
        /**
         * the parent matcher
         */
        private final Matcher<MessageHolder> mParentMatcher;
        /**
         * Create a new <code>HeartbeatMatcher</code> instance.
         *
         * @param inFixField
         * @param inValue
         */
        private HeartbeatMatcher(int inFixField,
                                 String inValue,
                                 Matcher<MessageHolder> inParentMatcher)
        {
            super(inFixField,
                  inValue);
            mParentMatcher = inParentMatcher;
        }
        /**
         * this matcher is used to match heartbeat rows 
         */
        private final FIXStringMatcher mHeartbeatMatcher = new FIXStringMatcher(MsgType.FIELD,
                                                                                MsgType.HEARTBEAT,                                                                              
                                                                                false);
        /* (non-Javadoc)
         * @see ca.odell.glazedlists.matchers.Matcher#matches(java.lang.Object)
         */
        @Override
        public boolean matches(MessageHolder inItem)
        {
            // let the parent have the first crack at the item to match
            boolean parentResult = mParentMatcher.matches(inItem);
            // if the parent thinks we shouldn't show the row, then don't, no matter what
            if(!parentResult) {
                return false;
            }
            // however, if the parent thinks we *should* show the row, we need to make sure that
            //  we're allowed to show it (if it's a heartbeat and we don't want to show heartbeats, we
            //  need to exclude it)
            boolean showHeartbeats = getShowHeartbeat();
            if(showHeartbeats) {
                return true;
            }
            return mHeartbeatMatcher.matches(inItem);
        }
    }
    /**
     * Get the showHeartbeat value.
     *
     * @return a <code>FIXMessagesView</code> value
     */
    private boolean getShowHeartbeat()
    {
        return mShowHeartbeat;
    }
}
