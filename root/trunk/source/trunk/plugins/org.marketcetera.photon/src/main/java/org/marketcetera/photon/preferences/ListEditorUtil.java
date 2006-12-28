package org.marketcetera.photon.preferences;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class ListEditorUtil {
	private static final String UTF_8 = "UTF-8";  //$NON-NLS-1$

	/**
	 * Splits the given string into a list of strings. This method is the
	 * converse of <code>createList</code>.
	 * <p>
	 * Subclasses must implement this method.
	 * </p>
	 * 
	 * @param stringList
	 *            the string
	 * @return an array of <code>String</code>
	 * @see #createList
	 */
	public static String[] parseString(String stringList) {
		String [] pieces = stringList.split("&");
		String [] decodedPieces = new String[pieces.length];
		int i = 0;
		for (String string : pieces) {
			try {
				decodedPieces[i] = URLDecoder.decode(string, UTF_8);
			} catch (UnsupportedEncodingException e) {
				decodedPieces[i] = string;
			}
			i++;
		}
		return decodedPieces;
	}

	/**
	 * Combines the given list of items into a single string. This method is the
	 * converse of <code>parseString</code>.
	 * 
	 * @param items
	 *            the list of items
	 * @return the combined string
	 * @see #parseString
	 */
	public static String encodeList(String[] items) {
		StringBuffer buf = new StringBuffer();
		int i = 0;
		for (String string : items) {

			try {
				buf.append(URLEncoder.encode(string, UTF_8));
			} catch (UnsupportedEncodingException e) {
				buf.append(string);
			}
			if (i < items.length - 1) {
				buf.append("&");
			}
			i++;
		}
		return buf.toString();

	}

}
