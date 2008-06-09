package org.marketcetera.photon.ui;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.impl.swt.SWTThreadProxyEventList;

public class EventListContentProvider<T> implements IStructuredContentProvider, ListEventListener<T> {
	SWTThreadProxyEventList swtList;
	private IndexedTableViewer viewer;
	
	public Object[] getElements(Object inputElement) {
		if (swtList != null){
			try {
				swtList.getReadWriteLock().readLock().lock();
				return swtList.toArray();
			} finally {
				swtList.getReadWriteLock().readLock().unlock();
			}
		} else {
			return new Object[0];
		}
	}

	@SuppressWarnings("unchecked") //SWTThreadProxyEventList is not parameterized
	public void dispose() {
		if (swtList != null){
			swtList.removeListEventListener(this);
		}
	}

	@SuppressWarnings("unchecked")
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (IndexedTableViewer) viewer;
		
		if(swtList != null){
			swtList.removeListEventListener(this);
			swtList.dispose();
			swtList = null;
		}
		if (newInput == null){
			viewer.refresh();
		} else {
			swtList = new SWTThreadProxyEventList((EventList<T>) newInput, Display.getDefault());
			swtList.addListEventListener(this);
		}
	}

	public void listChanged(ListEvent<T> event) {
		try {
			swtList.getReadWriteLock().readLock().lock();

			if (event.isReordering()){
				viewer.refresh();
			} else {
				while (event.next()){
					int index = event.getIndex();
					switch (event.getType()){
					case ListEvent.DELETE:
						viewer.remove(index);
						break;
					case ListEvent.INSERT:
						viewer.insert(swtList.get(index), index);
						break;
					case ListEvent.UPDATE:
						viewer.replace(swtList.get(index), index);
						break;
					default:
					}
				}
			}
		} finally {
			swtList.getReadWriteLock().readLock().unlock();
		}
	}

}
