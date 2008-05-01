package com.swtworkbench.community.xswt.scripting;

import java.util.Iterator;

public interface Bindings {
	public boolean has(String name);
	public Object get(String name);
	public void set(String name, Object value);
	public Iterator symbols();
	
	public void addBindingsListener(BindingsListener listener);
	public void removeBindingsListener(BindingsListener listener);
}
