package org.marketcetera.photon;

public class RCPUtils {

	public static String escapeAmpersands(String input) {
		StringBuffer title = new StringBuffer(input.length());
		for (int i = 0; i < input.length(); i++) {
			char character = input.charAt(i);
			title.append(character);
			if (character == '&') {
				title.append(character); // escape ampersand
			}
		}
		return title.toString();
	}

}
