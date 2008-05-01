package com.swtworkbench.community.xswt.scripting;

import java.lang.reflect.Method;

import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.dataparser.IDataParserContext;
import com.swtworkbench.community.xswt.dataparser.NonDisposableDataParser;

public class MethodFunctionDataParser extends NonDisposableDataParser {

	public Object parse(String source, Class klass, IDataParserContext context) throws XSWTException {
		Method m = (Method)super.parse(source, Function.class, context);
		return (m != null ? new MethodFunction(m) : null);
	}
}
