package org.rubypeople.rdt.internal.core.search;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.rubypeople.rdt.internal.core.util.Util;


public class MethodPatternParser {

	private String selector;
	private String typeName;
	List<String> params = new ArrayList<String>();

	public char[] getSelector() {
		if (selector == null) return null;
		return selector.toCharArray();
	}

	public void parse(String string) {
		if (string == null) return;
		int index = string.indexOf(".");
		if (index == -1) {
			index = string.indexOf("#");
		}
		if (index != -1) {
			typeName = string.substring(0, index);
			selector = string.substring(index + 1);
		} else {
			selector = string;
			typeName = null;
		}
		
		index = selector.indexOf('(');		
		if (index != -1) {
			String raw = selector.substring(index + 1, selector.length() - 1);
			selector = selector.substring(0, index);
			StringTokenizer tokenizer = new StringTokenizer(raw, " ,");
			while (tokenizer.hasMoreTokens()) {
				String param = tokenizer.nextToken();
				params.add(param);
			}			
		}
	}

	public char[] getTypeSimpleName() {
		if (typeName == null) return null;
		String name = Util.getSimpleName(typeName);
		if (name == null) return null;
		return name.toCharArray();
	}

	public char[] getQualifiedTypeName() {
		if (typeName == null) return null;
		return typeName.toCharArray();
	}

	public char[][] getParameterNames() {
		if (params.isEmpty()) return null;
		char[][] parameters = new char[params.size()][];
		int i = 0;
		for (String param : params) {
			parameters[i++] = param.toCharArray();
		}
		return parameters;
	}

}
