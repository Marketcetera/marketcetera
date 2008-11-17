package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;

public class RubyScriptEditorInputFactory implements IElementFactory {

	public final static String ID=  "org.rubypeople.rdt.ui.RubyScriptEditorInputFactory"; //$NON-NLS-1$
	public final static String KEY= "org.rubypeople.rdt.ui.RubyScriptIdentifier"; //$NON-NLS-1$
	
	/**
	 * @see IElementFactory#createElement
	 */
	public IAdaptable createElement(IMemento memento) {
		String identifier= memento.getString(KEY);
		if (identifier != null) {
			IRubyElement element= RubyCore.create(identifier);
			try {
				return EditorUtility.getEditorInput(element);
			} catch (RubyModelException x) {
			}
		}
		return null;
	}

	public static void saveState(IMemento memento, RubyScriptEditorInput input) {
		IRubyScript c= input.getRubyScript();
		memento.putString(KEY, c.getHandleIdentifier());
	}
}
