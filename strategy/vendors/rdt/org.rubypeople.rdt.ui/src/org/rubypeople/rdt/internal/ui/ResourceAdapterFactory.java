package org.rubypeople.rdt.internal.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyCore;

public class ResourceAdapterFactory implements IAdapterFactory {

	private static Class[] PROPERTIES = new Class[] { IRubyElement.class};

	public Class[] getAdapterList() {
		return PROPERTIES;
	}

	public Object getAdapter(Object element, Class key) {
		if (IRubyElement.class.equals(key)) { return RubyCore.create((IResource) element); }
		return null;
	}

}