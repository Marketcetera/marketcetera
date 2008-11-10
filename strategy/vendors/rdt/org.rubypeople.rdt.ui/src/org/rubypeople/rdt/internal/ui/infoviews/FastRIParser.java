package org.rubypeople.rdt.internal.ui.infoviews;

import java.util.LinkedList;
import java.util.List;

import org.rubypeople.rdt.ui.text.ansi.ANSIParser;
import org.rubypeople.rdt.ui.text.ansi.ANSIToken;

public class FastRIParser  extends ANSIParser {
	
	public List<ANSIToken> parse(String s) {
		if (s == null)
			return null;
		
		List<ANSIToken> tokens = new LinkedList<ANSIToken>();
		ANSIToken t = new ANSIToken();
		char open = 0;
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '_' || c == '+') {
				if (open != 0 && (i + 1 < s.length())) {
					char next = s.charAt(i + 1);
					if (!Character.isWhitespace(next) && next != ',' && next != '.') {
						t.add(c);
						continue;
					}
				}
				tokens.add(t);
				t = new ANSIToken();
				if (open == 0) {
					t.addProperty(getColor(c));
					open = c;
				} else {
					open = 0;
				}
			} else {
				t.add(c);
			}			
		}		
		tokens.add(t);
		return tokens;
	}

	private static int getColor(char c) {
		if (c == '_') return ANSIToken.YELLOW;
		if (c == '+') return ANSIToken.CYAN;
		return ANSIToken.RED;
	}
}
