package com.swtworkbench.community.xswt.dataparser.parsers;

import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.dataparser.IDataParserContext;
import com.swtworkbench.community.xswt.dataparser.NonDisposableDataParser;

public class ConstructorDataParser extends NonDisposableDataParser {

    protected String getRegex() {
		return "(.+)(\\(.*\\))";
	}

    public Object parse(String source, IDataParserContext context) throws XSWTException {
		if (! matches(source)) {
			return null;
		}
        Class c = (Class)getObject(1, Class.class, context);
        if (c == null) {
        	return null;
        }
        Class[] args = (Class[])getObject(2, Class[].class, context);
        if (args == null) {
        	return null;
        }
        try {
            return c.getConstructor(args);
        } catch (Exception e) {
        }
        return null;
    }
}
