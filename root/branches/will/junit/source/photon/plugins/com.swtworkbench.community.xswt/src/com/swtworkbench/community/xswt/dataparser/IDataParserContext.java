package com.swtworkbench.community.xswt.dataparser;

import com.swtworkbench.community.xswt.XSWTException;

public interface IDataParserContext {
	public Object parse(String value, Class klass) throws XSWTException;
}
