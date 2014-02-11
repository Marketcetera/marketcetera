package com.swtworkbench.community.xswt.scripting;

public interface BindingsListener {
	public void bindingAdded(Bindings bindings, String name, Object value);
	public void bindingRemoved(Bindings bindings, String name, Object value);
}
