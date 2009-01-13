package com.swtworkbench.community.xswt.dataparser.parsers;

import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.dataparser.IDataParserContext;
import com.swtworkbench.community.xswt.dataparser.NonDisposableDataParser;

public class MethodDataParser extends NonDisposableDataParser {

    protected String getRegex() {
		return "(.+)\\.(\\w+)(\\(.*\\))";
	}

	public Object parse(String source, IDataParserContext context) throws XSWTException {
		if (! matches(source)) {
			return null;
		}
        Class c = (Class)getObject(1, Class.class, context);
        if (c == null) {
        	return null;
        }
        String methodName = getSubstring(2);
        Class[] args = (Class[])getObject(3, Class[].class, context);
        if (args == null) {
        	return null;
        }
        try {
            return c.getMethod(methodName, args);
        } catch (Exception e) {
        }
        return null;
    }
}
