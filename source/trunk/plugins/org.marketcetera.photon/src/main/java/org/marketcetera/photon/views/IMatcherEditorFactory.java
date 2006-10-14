package org.marketcetera.photon.views;

import ca.odell.glazedlists.matchers.MatcherEditor;

@Deprecated
public interface IMatcherEditorFactory<T> {
	public MatcherEditor<T> createMatcherEditor(Object obj);
}
