package com.swtworkbench.community.xswt.dataparser.parsers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.dataparser.NonDisposableDataParser;

public class BundleClassDataParser extends NonDisposableDataParser {

	private Map classes = new HashMap();
	
	public Object parse(String source) throws XSWTException {
		Class c = (Class)classes.get(source);
		if (c != null) {
			return c;
		}
		int pos = source.indexOf('/');
		if (pos < 0) {
			pos = source.indexOf(':');
		}
		if (pos < 0) {
			return null;
		}
		Bundle bundle = Platform.getBundle(source.substring(0, pos));
		try {
			c = bundle.loadClass(source.substring(pos + 1));
		} catch (ClassNotFoundException e) {
		}
		if (c != null) {
			classes.put(source.replace(':', '/'), c);
		}
		return c;
	}
}
