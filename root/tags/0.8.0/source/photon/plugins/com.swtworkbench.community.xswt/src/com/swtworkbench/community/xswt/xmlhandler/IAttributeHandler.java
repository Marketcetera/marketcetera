package com.swtworkbench.community.xswt.xmlhandler;

import com.swtworkbench.community.xswt.XSWTException;

public interface IAttributeHandler {
	public boolean handleAttribute(String name, String value, String namespace, Object parent, IHandlerContext context) throws XSWTException;
}
