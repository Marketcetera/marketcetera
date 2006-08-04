package org.marketcetera.photon.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.marketcetera.core.ClassVersion;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swt.EventTableViewer;

@ClassVersion("$Id$")
public class ViewerSelectionAdapter<LIST_TYPE> implements ISelectionProvider {

	List<ISelectionChangedListener> listeners = new ArrayList<ISelectionChangedListener>();

	private EventList<LIST_TYPE> selectedList;

	public ViewerSelectionAdapter(EventTableViewer adaptable) {
		selectedList = adaptable.getSelected();
		selectedList.addListEventListener(new PrivateListEventListener<LIST_TYPE>());
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.add(listener);
	}

	public ISelection getSelection() {
		return new StructuredSelection(selectedList);
	}

	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		listeners.remove(listener);
	}

	public void setSelection(ISelection selection) {
		throw new UnsupportedOperationException();
	}

	private void fireSelectionChanged() {
		final SelectionChangedEvent e = new SelectionChangedEvent(this,
				new StructuredSelection(selectedList));
		for (final ISelectionChangedListener l : listeners) {
			Platform.run(new SafeRunnable() {
				public void run() {
					l.selectionChanged(e);
				}
			});
		}
	}

	class PrivateListEventListener<INNER_LIST_TYPE> implements ListEventListener<INNER_LIST_TYPE> {
		public void listChanged(ListEvent listChanges) {
			fireSelectionChanged();
		}
	}
}
