package com.swtworkbench.community.xswt.xmlhandler;

import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.dataparser.IDataParserContext;
import com.swtworkbench.community.xswt.xmlparser.IMinimalOM;

public interface IHandlerContext {
	public IMinimalOM getMinimalOM();
	public IDataParserContext getDataParserContext();
	public void processChildren(Object element, Object context, String uri) throws XSWTException ;
	public void processAttributes(Object element, Object context) throws XSWTException;
}
