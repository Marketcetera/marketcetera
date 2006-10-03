package org.marketcetera.photon.ui;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

public class EventListContentProvider<T> implements IStructuredContentProvider, ListEventListener<T> {
	EventList<T> inputList;
	EventList<T> viewedList;
	private TableViewer viewer;
	
	public Object[] getElements(Object inputElement) {
		if (inputList != null){
			return inputList.toArray();
		} else {
			return new Object[0];
		}
	}

	public void dispose() {
		if (inputList != null){
			inputList.removeListEventListener(this);
		}
		viewedList = null;
	}

	@SuppressWarnings("unchecked")
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TableViewer) viewer;
		inputList = ((EventList<T>) newInput);
		viewedList = null;
		if (newInput != null){
			inputList.addListEventListener(this);
			viewedList = new BasicEventList<T>(inputList.size());
			viewedList.addAll(inputList);
		}
		if (oldInput != null)
			((EventList<T>) oldInput).removeListEventListener(this);
	}

	public void listChanged(ListEvent<T> event) {
		while (event.next()){
			int index = event.getIndex();
			switch (event.getType()){
			case ListEvent.DELETE:
				viewer.remove(viewedList.get(index));
				viewedList.remove(index);
				break;
			case ListEvent.INSERT:
				viewedList.add(index, inputList.get(index));
				viewer.insert(viewedList.get(index), index);
				break;
			case ListEvent.UPDATE:
				viewer.refresh(viewedList.get(index));
				break;
			default:
			}
		}
	}

}
