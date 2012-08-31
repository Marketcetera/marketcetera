package com.swtworkbench.community.xswt.dataparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.swtworkbench.community.xswt.XSWTException;

public abstract class AbstractDataParser implements IDataParser {

	private Pattern pattern;
	private Matcher matcher;
	
	protected String getRegex() {
		return null;
	}
	
	protected Matcher getMatcher() {
		return matcher;
	}

	protected String getSubstring(int i) {
		return (matcher != null ? matcher.group(i) : null);
	}
	
	protected Object getObject(int i, Class klass, IDataParserContext context) throws XSWTException {
		String s = getSubstring(i);
		return (s != null ? context.parse(s, klass) : null);
	}
	
	protected boolean matches(String source) {
		if (getRegex() != null) {
			if (pattern == null) {
				pattern = Pattern.compile(getRegex(), Pattern.DOTALL);
			}
			matcher = pattern.matcher(source);
			if (matcher.matches()) {
				return true;
			}
		}
		return false;
	}

	public Object parse(String source, Class klass, IDataParserContext context) throws XSWTException {
		return parse(source, context);
	}

	public Object parse(String source, IDataParserContext context) throws XSWTException {
		return parse(source);
	}
	
	public Object parse(String source) throws XSWTException {
		return null;
	}
}
