package com.swtworkbench.community.xswt;

import com.swtworkbench.community.xswt.scripting.Bindings;
import com.swtworkbench.community.xswt.scripting.BindingsListener;

public interface XSWTListener extends BindingsListener {
	public void processDocument(XSWT xswt, Object parent);
	public void documentProcessed(XSWT xswt, Object result);

	public void processElement(XSWT xswt, String name, Object parent);
	public void elementProcessed(XSWT xswt, String name, Object result);
	
	public void processAttribute(XSWT xswt, String name, String value, Object parent);
	public void attributeProcessed(XSWT xswt, String name, String value, Object result);

	public void setProperty(Object o, String name, Object value);
	public void propertySet(Object o, String name, Object value);
	
	public void error(XSWT xswt, Exception e);
	
	public static class Stub implements XSWTListener {
		
		public void bindingAdded(Bindings bindings, String name, Object value) {
		}
		public void bindingRemoved(Bindings bindings, String name, Object value) {
		}

		public void attributeProcessed(XSWT xswt, String name, String value, Object result) {
		}
		public void documentProcessed(XSWT xswt, Object result) {
		}
		public void elementProcessed(XSWT xswt, String name, Object result) {
		}
		public void error(XSWT xswt, Exception e) {
		}
		public void processAttribute(XSWT xswt, String name, String value, Object parent) {
		}
		public void processDocument(XSWT xswt, Object parent) {
		}
		public void processElement(XSWT xswt, String name, Object parent) {
		}
		public void propertySet(Object o, String name, Object value) {
		}
		public void setProperty(Object o, String name, Object value) {
		}
	}
}
