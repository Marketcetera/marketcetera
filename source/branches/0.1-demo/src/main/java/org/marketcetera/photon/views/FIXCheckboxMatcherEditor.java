package org.marketcetera.photon.views;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.marketcetera.photon.model.MessageHolder;

import ca.odell.glazedlists.matchers.CompositeMatcherEditor;

public class FIXCheckboxMatcherEditor extends
		CompositeMatcherEditor<MessageHolder> implements SelectionListener {

	public FIXCheckboxMatcherEditor()
	{
		setMode(OR);
	}
	
	public void widgetSelected(SelectionEvent e) {
		Button source = (Button)e.getSource();
		FIXMatcherEditor matcherEditor = ((FIXMatcherEditor)(source).getData());
		if (source.getSelection()){
			this.getMatcherEditors().add(matcherEditor);
		} else {
			this.getMatcherEditors().remove(matcherEditor);
		}
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		// do nothing
	}

	
}
