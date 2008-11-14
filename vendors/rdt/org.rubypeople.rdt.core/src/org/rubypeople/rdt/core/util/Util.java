package org.rubypeople.rdt.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.rubypeople.rdt.internal.core.util.CharOperation;

public class Util {

	private static final String[] KEYWORDS = new String[] {"alias", "and", "BEGIN", "begin", "break", "case", "class", "def", "defined",
		"do", "else", "elsif", "END", "end", "ensure", "false", "for", "if",
		"in", "module", "next", "nil", "not", "or", "redo", "rescue", "retry",
		"return", "self", "super", "then", "true", "undef", "unless", "until", "when",
		"while", "yield"};
	
	private static final String[] OPERATORS = new String[] {
	"::", ".",
	"[]",
	"**",
	"-", "+", "!", "~",
	"*", "/", " %",
	"+", "-",
	"<<", ">>",
	"&",
	"|", "^",
	">", ">=", "<", "<=",
	"<=>", "==", "===", "!=", "=~", "!~",
	"&&",
	"||",
	"..", "...",
	"=", "+=", "-=", "*=", "/=", "||="
	};
	public interface Displayable {
		public String displayString(Object o);
	}

	private static final int DEFAULT_READING_SIZE = 8192;
	public final static String UTF_8 = "UTF-8"; //$NON-NLS-1$
	public static String LINE_SEPARATOR = System.getProperty("line.separator"); //$NON-NLS-1$

	public static byte[] getFileByteContent(File file) throws IOException {
		InputStream stream = null;
		try {
			stream = new FileInputStream(file);
			return getInputStreamAsByteArray(stream, (int) file.length());
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	/**
	 * Converts an array of Objects into String.
	 */
	public static String toString(Object[] objects, Displayable renderer) {
		if (objects == null) return ""; //$NON-NLS-1$
		StringBuffer buffer = new StringBuffer(10);
		for (int i = 0; i < objects.length; i++){
			if (i > 0) buffer.append(", "); //$NON-NLS-1$
			buffer.append(renderer.displayString(objects[i]));
		}
		return buffer.toString();
	}
	
	/**
	 * Returns the given input stream's contents as a byte array. If a length is
	 * specified (ie. if length != -1), only length bytes are returned.
	 * Otherwise all bytes in the stream are returned. Note this doesn't close
	 * the stream.
	 * 
	 * @throws IOException
	 *             if a problem occured reading the stream.
	 */
	public static byte[] getInputStreamAsByteArray(InputStream stream,
			int length) throws IOException {
		byte[] contents;
		if (length == -1) {
			contents = new byte[0];
			int contentsLength = 0;
			int amountRead = -1;
			do {
				int amountRequested = Math.max(stream.available(),
						DEFAULT_READING_SIZE); // read at least 8K

				// resize contents if needed
				if (contentsLength + amountRequested > contents.length) {
					System.arraycopy(contents, 0,
							contents = new byte[contentsLength
									+ amountRequested], 0, contentsLength);
				}

				// read as many bytes as possible
				amountRead = stream.read(contents, contentsLength,
						amountRequested);

				if (amountRead > 0) {
					// remember length of contents
					contentsLength += amountRead;
				}
			} while (amountRead != -1);

			// resize contents if necessary
			if (contentsLength < contents.length) {
				System.arraycopy(contents, 0,
						contents = new byte[contentsLength], 0, contentsLength);
			}
		} else {
			contents = new byte[length];
			int len = 0;
			int readSize = 0;
			while ((readSize != -1) && (len != length)) {
				// See PR 1FMS89U
				// We record first the read size. In this case len is the actual
				// read size.
				len += readSize;
				readSize = stream.read(contents, len, length - len);
			}
		}

		return contents;
	}

	/**
	 * Converts an array of Objects into String.
	 */
	public static String toString(Object[] objects) {
		return toString(objects, 
			new Displayable(){ 
				public String displayString(Object o) { 
					if (o == null) return "null"; //$NON-NLS-1$
					return o.toString(); 
				}
			});
	}

		/**
	 * Returns the contents of the given file as a char array.
	 * When encoding is null, then the platform default one is used
	 * @throws IOException if a problem occured reading the file.
	 */
	public static char[] getFileCharContent(File file, String encoding) throws IOException {
		InputStream stream = null;
		try {
			stream = new FileInputStream(file);
			return getInputStreamAsCharArray(stream, (int) file.length(), encoding);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}
	
	/**
	 * Returns the given input stream's contents as a character array.
	 * If a length is specified (ie. if length != -1), this represents the number of bytes in the stream.
	 * Note this doesn't close the stream.
	 * @throws IOException if a problem occured reading the stream.
	 */
	public static char[] getInputStreamAsCharArray(InputStream stream, int length, String encoding)
		throws IOException {
		InputStreamReader reader = null;
		try {
			reader = encoding == null
						? new InputStreamReader(stream)
						: new InputStreamReader(stream, encoding);
		} catch (UnsupportedEncodingException e) {
			// encoding is not supported
			reader =  new InputStreamReader(stream);
		}
		char[] contents;
		int totalRead = 0;
		if (length == -1) {
			contents = CharOperation.NO_CHAR;
		} else {
			// length is a good guess when the encoding produces less or the same amount of characters than the file length
			contents = new char[length]; // best guess
		}

		while (true) {
			int amountRequested;
			if (totalRead < length) {
				// until known length is met, reuse same array sized eagerly
				amountRequested = length - totalRead;
			} else {
				// reading beyond known length
				int current = reader.read(); 
				if (current < 0) break;
				
				amountRequested = Math.max(stream.available(), DEFAULT_READING_SIZE);  // read at least 8K
				
				// resize contents if needed
				if (totalRead + 1 + amountRequested > contents.length)
					System.arraycopy(contents, 	0, 	contents = new char[totalRead + 1 + amountRequested], 0, totalRead);
				
				// add current character
				contents[totalRead++] = (char) current; // coming from totalRead==length
			}
			// read as many chars as possible
			int amountRead = reader.read(contents, totalRead, amountRequested);
			if (amountRead < 0) break;
			totalRead += amountRead;
		}

		// Do not keep first character for UTF-8 BOM encoding
		int start = 0;
		if (totalRead > 0 && UTF_8.equals(encoding)) {
			if (contents[0] == 0xFEFF) { // if BOM char then skip
				totalRead--;
				start = 1;
			}
		}
		
		// resize contents if necessary
		if (totalRead < contents.length)
			System.arraycopy(contents, start, contents = new char[totalRead], 	0, 	totalRead);

		return contents;
	}

	public static String camelCaseToUnderscores(String name) {
		if (name == null) return null;
		if (name.length() == 0) return "";
		StringBuffer newName = new StringBuffer();
		boolean lastWasUpper = false;
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			newName.append(Character.toLowerCase(c));
			if (lastWasUpper && Character.isLowerCase(c)) {
				if (newName.length() > 2) newName.insert(newName.length() - 2, "_");
				lastWasUpper = false;
			}
			if (Character.isUpperCase(c)) {				
				lastWasUpper = true;
			} 
		}
		return newName.toString();
	}

	public static String underscoresToCamelCase(String name) {
		if (name == null) return null;
		if (name.length() == 0) return "";
		StringBuffer newName = new StringBuffer();
		boolean lastWasUnderScore = false;
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (lastWasUnderScore || i == 0) {
				newName.append(Character.toUpperCase(c));
				lastWasUnderScore = false;
			} else if (c == '_') {				
				lastWasUnderScore = true;
			} else {
				newName.append(c);
			}
		}
		return newName.toString();
	}

	public static boolean isOperator(String word) {
		return contains(word, OPERATORS);
	}

	public static boolean isKeyword(String word) {
		return contains(word, KEYWORDS);
	}
	
	private static boolean contains(String word, String[] array) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(word)) return true;
		}
		return false;
	}
}
