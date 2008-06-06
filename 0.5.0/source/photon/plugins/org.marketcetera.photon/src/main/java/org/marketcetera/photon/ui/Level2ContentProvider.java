package org.marketcetera.photon.ui;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.marketcetera.photon.PhotonPlugin;

import quickfix.Message;
import quickfix.field.MDEntryType;
import quickfix.field.NoMDEntries;
import quickfix.fix42.MarketDataSnapshotFullRefresh;

public class Level2ContentProvider extends ObservableListContentProvider {

	private Message currentMessage;
	private WritableList currentList;
	private final char entryType;
	
	public Level2ContentProvider(char entryType) {
		this.entryType = entryType;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		WritableList writableList = new WritableList();
		if (newInput instanceof Message)
		{
			currentMessage = (Message)newInput;
			try {
				int noEntries = currentMessage.getInt(NoMDEntries.FIELD);
				for (int i = 0; i < noEntries; i++){
					MarketDataSnapshotFullRefresh.NoMDEntries group = new MarketDataSnapshotFullRefresh.NoMDEntries();
					currentMessage.getGroup(i+1, group);
					char currentEntryType = group.getChar(MDEntryType.FIELD);
					if (entryType==currentEntryType){
						writableList.add(group);
					}
				}
			} catch (Throwable ex){
				PhotonPlugin.getMainConsoleLogger().error("Exception parsing level 2 data", ex);
			}
		}
		super.inputChanged(viewer, currentList, writableList);
		currentList = writableList;
	}
	
}
