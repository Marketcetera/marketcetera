package org.marketcetera.photon.ui;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;

import quickfix.Message;
import quickfix.field.MDEntryType;
import quickfix.field.NoMDEntries;
import quickfix.fix42.MarketDataSnapshotFullRefresh;

/* $License$ */

/**
 * Organizes Level II or Depth-of-Book data to be displayed in a UI list.
 *
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.6.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class Level2ContentProvider
    extends ObservableListContentProvider
    implements Messages
{
    /**
     * stores the last message received
     */
	private Message currentMessage;
	/**
	 * the most recent state of the display list
	 */
	private WritableList currentList;
	/**
	 * indicates the type of provider, containing a <code>MDEntryType</code>
	 */
	private final char entryType;
	/**
	 * the list to which the UI widget is bound
	 */
	private final WritableList mViewList;
	/**
	 * Create a new Level2ContentProvider instance.
	 *
	 * @param entryType a <code>char</code> value indicating the <code>MDEntryType</code> to use 
	 * @param inViewList a <code>WritableList</code> value containing the <code>Collection</code> to which the display widget is bound
	 */
	public Level2ContentProvider(char entryType,
	                             WritableList inViewList) 
	{
		this.entryType = entryType;
		mViewList = inViewList;
	}
	@Override
	public void inputChanged(Viewer viewer, 
	                         Object oldInput, 
	                         Object newInput) 
	{
        if(newInput instanceof Message) {
            currentMessage = (Message)newInput;
			try {
				int noEntries = currentMessage.getInt(NoMDEntries.FIELD);
				for (int i = 0; i < noEntries; i++) {
					MarketDataSnapshotFullRefresh.NoMDEntries group = new MarketDataSnapshotFullRefresh.NoMDEntries();
					currentMessage.getGroup(i+1, group);
					char currentEntryType = group.getChar(MDEntryType.FIELD);
					if (entryType==currentEntryType) {
						mViewList.add(group);
					}
				}
			} catch (Throwable ex){
                PhotonPlugin.getMainConsoleLogger().error(CANNOT_PARSE_LEVEL_TWO_DATA.getText());
			}
		}
		super.inputChanged(viewer, 
		                   currentList, 
		                   mViewList);
		currentList = mViewList;
	}	
}
