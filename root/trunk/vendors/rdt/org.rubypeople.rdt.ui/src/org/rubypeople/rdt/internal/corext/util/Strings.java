package org.rubypeople.rdt.internal.corext.util;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.ILineTracker;
import org.eclipse.jface.text.IRegion;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.formatter.IndentManipulation;

public class Strings {

    /**
     * Returns the indent of the given string in indentation units. Odd spaces
     * are not counted.
     * 
     * @param line
     *            the text line
     * @param project
     *            the ruby project from which to get the formatter preferences,
     *            or <code>null</code> for global preferences
     * @since 3.1
     */
    public static int computeIndentUnits(String line, IRubyProject project) {
        return computeIndentUnits(line, CodeFormatterUtil.getTabWidth(project), CodeFormatterUtil
                .getIndentWidth(project));
    }

    /**
     * Returns the indent of the given string in indentation units. Odd spaces
     * are not counted.
     * 
     * @param line
     *            the text line
     * @param tabWidth
     *            the width of the '\t' character in space equivalents
     * @param indentWidth
     *            the width of one indentation unit in space equivalents
     * @since 3.1
     */
    public static int computeIndentUnits(String line, int tabWidth, int indentWidth) {
        if (indentWidth == 0) return -1;
        int visualLength = measureIndentLength(line, tabWidth);
        return visualLength / indentWidth;
    }

    /**
     * Computes the visual length of the indentation of a
     * <code>CharSequence</code>, counting a tab character as the size until
     * the next tab stop and every other whitespace character as one.
     * 
     * @param line
     *            the string to measure the indent of
     * @param tabSize
     *            the visual size of a tab in space equivalents
     * @return the visual length of the indentation of <code>line</code>
     * @since 3.1
     */
    public static int measureIndentLength(CharSequence line, int tabSize) {
        int length = 0;
        int max = line.length();
        for (int i = 0; i < max; i++) {
            char ch = line.charAt(i);
            if (ch == '\t') {
                int reminder = length % tabSize;
                length += tabSize - reminder;
            } else if (isIndentChar(ch)) {
                length++;
            } else {
                return length;
            }
        }
        return length;
    }
    
    /**
     * Indent char is a space char but not a line delimiters.
     * <code>== Character.isWhitespace(ch) && ch != '\n' && ch != '\r'</code>
     */
    public static boolean isIndentChar(char ch) {
        return Character.isWhitespace(ch) && !isLineDelimiterChar(ch);
    }
    
    /**
     * Line delimiter chars are  '\n' and '\r'.
     */
    public static boolean isLineDelimiterChar(char ch) {
        return ch == '\n' || ch == '\r';
    }

	/**
	 * Returns <code>true</code> if the given string only consists of
	 * white spaces according to Ruby. If the string is empty, <code>true
	 * </code> is returned.
	 * 
	 * @return <code>true</code> if the string only consists of white
	 * 	spaces; otherwise <code>false</code> is returned
	 * 
	 * @see java.lang.Character#isWhitespace(char)
	 */
	public static boolean containsOnlyWhitespaces(String s) {
		int size= s.length();
		for (int i= 0; i < size; i++) {
			if (!Character.isWhitespace(s.charAt(i)))
				return false;
		}
		return true;
	}

	public static boolean equals(String s, char[] c) {
		if (s.length() != c.length)
			return false;

		for (int i = c.length; --i >= 0;)
			if (s.charAt(i) != c[i])
				return false;
		return true;
	}

	public static boolean startsWithIgnoreCase(String text, String prefix) {
		int textLength= text.length();
		int prefixLength= prefix.length();
		if (textLength < prefixLength)
			return false;
		for (int i= prefixLength - 1; i >= 0; i--) {
			if (Character.toLowerCase(prefix.charAt(i)) != Character.toLowerCase(text.charAt(i)))
				return false;
		}
		return true;
	}

	/**
	 * tests if a char is lower case. Fix for 26529 
	 */
	public static boolean isLowerCase(char ch) {
		return Character.toLowerCase(ch) == ch;
	}

	public static String removeMnemonicIndicator(String string) {
		int length= string.length();
		StringBuffer result= new StringBuffer(length);
		char lastChar= ' '; // everything except & is OK as an initializer
		for(int i= 0; i < length; i++) {
			char ch= string.charAt(i);
			if (ch != '&' || lastChar == '&') {
				result.append(ch);				
			}
			lastChar= ch;
		}
		return result.toString();
	}

	/**
	 * Converts the given string into an array of lines. The lines 
	 * don't contain any line delimiter characters.
	 *
	 * @return the string converted into an array of strings. Returns <code>
	 * 	null</code> if the input string can't be converted in an array of lines.
	 */
	public static String[] convertIntoLines(String input) {
		try {
			ILineTracker tracker= new DefaultLineTracker();
			tracker.set(input);
			int size= tracker.getNumberOfLines();
			String result[]= new String[size];
			for (int i= 0; i < size; i++) {
				IRegion region= tracker.getLineInformation(i);
				int offset= region.getOffset();
				result[i]= input.substring(offset, offset + region.getLength());
			}
			return result;
		} catch (BadLocationException e) {
			return null;
		}
	}
	
	/**
	 * Removes the common number of indents from all lines. If a line
	 * only consists out of white space it is ignored.

	 * @param project the java project from which to get the formatter
	 *        preferences, or <code>null</code> for global preferences
	 * @since 3.1
	 */
	public static void trimIndentation(String[] lines, IRubyProject project) {
		trimIndentation(lines, CodeFormatterUtil.getTabWidth(project), CodeFormatterUtil.getIndentWidth(project), true);
	}
	
	/**
	 * Removes the common number of indents from all lines. If a line
	 * only consists out of white space it is ignored. If <code>
	 * considerFirstLine</code> is false the first line will be ignored.
	 * @since 3.1
	 */
	public static void trimIndentation(String[] lines, int tabWidth, int indentWidth, boolean considerFirstLine) {
		String[] toDo= new String[lines.length];
		// find indentation common to all lines
		int minIndent= Integer.MAX_VALUE; // very large
		for (int i= considerFirstLine ? 0 : 1; i < lines.length; i++) {
			String line= lines[i];
			if (containsOnlyWhitespaces(line))
				continue;
			toDo[i]= line;
			int indent= computeIndentUnits(line, tabWidth, indentWidth);
			if (indent < minIndent) {
				minIndent= indent;
			}
		}
		
		if (minIndent > 0) {
			// remove this indent from all lines
			for (int i= considerFirstLine ? 0 : 1; i < toDo.length; i++) {
				String s= toDo[i];
				if (s != null)
					lines[i]= trimIndent(s, minIndent, tabWidth, indentWidth);
				else {
					String line= lines[i];
					int indent= computeIndentUnits(line, tabWidth, indentWidth);
					if (indent > minIndent)
						lines[i]= trimIndent(line, minIndent, tabWidth, indentWidth);
					else
						lines[i]= trimLeadingTabsAndSpaces(line);
				}
			}
		}
	}
	
	/**
	 * Removes the given number of indents from the line. Asserts that the given line 
	 * has the requested number of indents. If <code>indentsToRemove <= 0</code>
	 * the line is returned.
	 * 
	 * @since 3.1
	 */
	public static String trimIndent(String line, int indentsToRemove, int tabWidth, int indentWidth) {
		return IndentManipulation.trimIndent(line, indentsToRemove, tabWidth, indentWidth);
	}
	
	/**
	 * Removes leading tabs and spaces from the given string. If the string
	 * doesn't contain any leading tabs or spaces then the string itself is 
	 * returned.
	 */
	public static String trimLeadingTabsAndSpaces(String line) {
		int size= line.length();
		int start= size;
		for (int i= 0; i < size; i++) {
			char c= line.charAt(i);
			if (!IndentManipulation.isIndentChar(c)) {
				start= i;
				break;
			}
		}
		if (start == 0)
			return line;
		else if (start == size)
			return ""; //$NON-NLS-1$
		else
			return line.substring(start);
	}
	
	/**
	 * Concatenate the given strings into one strings using the passed line delimiter as a
	 * delimiter. No delimiter is added to the last line.
	 */
	public static String concatenate(String[] lines, String delimiter) {
		StringBuffer buffer= new StringBuffer();
		for (int i= 0; i < lines.length; i++) {
			if (i > 0)
				buffer.append(delimiter);
			buffer.append(lines[i]);
		}
		return buffer.toString();
	}

}
