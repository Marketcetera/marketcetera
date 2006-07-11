package org.marketcetera.photon.views;

import org.marketcetera.photon.model.MessageHolder;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.CompositeMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.MatcherEditor;

public class SelectionListMatcherEditor extends
		AbstractMatcherEditor<MessageHolder> {

//	private EventList<MatcherEditor> source = null;
//
//	private EventList<MatcherEditor> selectedItems;

	private CompositeMatcherEditor<MessageHolder> compositeMatcherEditor;

//	private final IMatcherEditorFactory<MessageHolder> factory;

	/**
	 * Create a filter list that filters the specified source list, which must
	 * contain only Issue objects.
	 */
	public SelectionListMatcherEditor(
//			EventList<MatcherEditor> source,
			EventList<MatcherEditor> selected
//			, IMatcherEditorFactory<MessageHolder> factory
			) {
//		this.source = source;
//		this.factory = factory;
//		this.selectedItems = selected;
//		selectedItems.addListEventListener(new SelectionChangeEventList());
		this.compositeMatcherEditor = new CompositeMatcherEditor(
				selected);
		
		this.compositeMatcherEditor.setMode(CompositeMatcherEditor.OR);
	}

	public void dispose() {
	}

	@Override
	public Matcher<MessageHolder> getMatcher() {
		return compositeMatcherEditor.getMatcher();
	}

//	/**
//	 * An EventList to respond to changes in selection from the ListEventViewer.
//	 */
//	private final class SelectionChangeEventList implements
//			ListEventListener<MatcherEditor> {
//
//		/** {@inheritDoc} */
//		public void listChanged(ListEvent<MatcherEditor> listChanges) {
//
//			// if we have all or no users selected, match all users
//			if (selectedItems.isEmpty()
//					|| selectedItems.size() == source.size()) {
//				fireMatchAll();
//				return;
//			}
//
//			compositeMatcherEditor.getMatcherEditors().retainAll(selectedItems);
//			fireChanged(getMatcher());
//		}
//	}
}
