package com.swtworkbench.community.xswt.xmlhandler;

import com.swtworkbench.community.xswt.XSWTException;

public interface IElementHandler {
	public boolean handlesChildElements(Object element, IHandlerContext context);
	public Object handleElement(Object element, Object parent, IHandlerContext context) throws XSWTException;
}
