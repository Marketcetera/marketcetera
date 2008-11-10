package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.rubypeople.rdt.core.IRubyElement;


public class ExternalRubyEditor extends RubyAbstractEditor {
	
	public ExternalRubyEditor() {
		// TODO: should support ruler context menu for adding breakpoints
		// but have to solve problem regarding markers and resources first. 
		super();
	}
	
	public boolean isEditable() {
		return false;
	}

    protected IRubyElement getElementAt(int caret, boolean b) {
        return null;
    }

    protected IRubyElement getElementAt(int offset) {
        return null;
    }

}
