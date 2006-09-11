package org.marketcetera.photon.views;

import ca.odell.glazedlists.matchers.MatcherEditor;

public interface IMatcherEditorFactory<T> {
	public MatcherEditor<T> createMatcherEditor(Object obj);
}
