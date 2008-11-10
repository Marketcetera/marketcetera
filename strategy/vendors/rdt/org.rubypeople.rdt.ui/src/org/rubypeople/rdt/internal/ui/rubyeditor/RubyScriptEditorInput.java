package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.internal.core.ExternalRubyScript;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;

public class RubyScriptEditorInput implements IRubyScriptEditorInput, IPersistableElement {

	private ExternalRubyScript fScript;

	public RubyScriptEditorInput(ExternalRubyScript script) {
		this.fScript = script;
	}

	public IRubyScript getRubyScript() {
		return fScript;
	}
	
	/*
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof RubyScriptEditorInput))
			return false;
		RubyScriptEditorInput other= (RubyScriptEditorInput) obj;
		return fScript.equals(other.fScript);
	}

	/*
	 * @see Object#hashCode
	 */
	public int hashCode() {
		return fScript.hashCode();
	}

	/*
	 * @see IEditorInput#getPersistable()
	 */
	public IPersistableElement getPersistable() {
		return this;
	}

	/*
	 * @see IEditorInput#getName()
	 */
	public String getName() {
		return fScript.getElementName();
	}

	/*
	 * @see IEditorInput#getToolTipText()
	 */
	public String getToolTipText() {
		return fScript.getElementName();
	}

	/*
	 * @see IEditorInput#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		return RubyPluginImages.DESC_OBJS_SCRIPT;
	}

	/*
	 * @see IEditorInput#exists()
	 */
	public boolean exists() {
		return fScript.exists();
	}

	/*
	 * @see IAdaptable#getAdapter(Class)
	 */
	public Object getAdapter(Class adapter) {
		if (adapter == IRubyScript.class)
			return fScript;
		return fScript.getAdapter(adapter);
	}

	public String getFactoryId() {
		return RubyScriptEditorInputFactory.ID;
	}

	public void saveState(IMemento memento) {
		RubyScriptEditorInputFactory.saveState(memento, this);
	}
}
