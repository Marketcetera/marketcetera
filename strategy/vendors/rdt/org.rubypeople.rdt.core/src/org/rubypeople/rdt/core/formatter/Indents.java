package org.rubypeople.rdt.core.formatter;

import java.util.Map;

import org.eclipse.jface.text.Assert;
import org.rubypeople.rdt.core.RubyCore;

public class Indents {

    private Indents() {
    }
    
    /**
     * Returns the tab width as configured in the given map.
     * @param options the map to get the formatter settings from. Use {@link org.eclipse.jdt.core.IJavaProject#getOptions(boolean)} to
     * get the most current project options.
     * @return the tab width
     */
    public static int getTabWidth(Map options) {
        if (options == null) {
            throw new IllegalArgumentException();
        }
        return getIntValue(options, DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, 4);
    }
    
    /**
     * Returns the tab width as configured in the given map.
     * @param options the map to get the formatter settings from. Use {@link org.eclipse.jdt.core.IJavaProject#getOptions(boolean)} to
     * get the most current project options.
     * @return the indent width
     */
    public static int getIndentWidth(Map options) {
        if (options == null) {
            throw new IllegalArgumentException();
        }
        int tabWidth=getTabWidth(options);
        boolean isMixedMode= DefaultCodeFormatterConstants.MIXED.equals(options.get(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR));
        if (isMixedMode) {
            return getIntValue(options, DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE, tabWidth);
        }
        return tabWidth;
    }
    
    private static int getIntValue(Map options, String key, int def) {
        try {
            return Integer.parseInt((String) options.get(key));
        } catch (NumberFormatException e) {
            return def;
        }
    }
    
    /**
     * Returns the indentation of the given line in indentation units. Odd spaces are
     * not counted. This method only analyzes the content of <code>line</code> up to the first
     * non-whitespace character.
     * 
     * @param line the string to measure the indent of
     * @param tabWidth the width of one tab character in space equivalents
     * @param indentWidth the width of one indentation unit in space equivalents
     * @return the number of indentation units that line is indented by
     */
    public static int measureIndentUnits(CharSequence line, int tabWidth, int indentWidth) {
        if (indentWidth <= 0 || tabWidth < 0 || line == null) {
            throw new IllegalArgumentException();
        }
        
        int visualLength= measureIndentInSpaces(line, tabWidth);
        return visualLength / indentWidth;
    }
    
    /**
     * Creates a string that represents the given number of indentation units.
     * The returned string can contain tabs and/or spaces depending on the core
     * formatter preferences.
     * 
     * @param indentationUnits the number of indentation units to generate
     * @param options the options to get the formatter settings from. Use {@link org.eclipse.jdt.core.IJavaProject#getOptions(boolean)} to
     * get the most current project options.
     * @return the indent string
     */
    public static String createIndentString(int indentationUnits, Map options) {
        if (indentationUnits < 0) {
           return "" ;
        }
        if (options == null || indentationUnits < 0) {
            throw new IllegalArgumentException();
        }
        
        String tabChar= getStringValue(options, DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, RubyCore.TAB);
        
        final int tabs, spaces;
        if (RubyCore.SPACE.equals(tabChar)) {
            tabs= 0;
            spaces= indentationUnits * getIndentWidth(options);
        } else if (RubyCore.TAB.equals(tabChar)) {
            // indentWidth == tabWidth
            tabs= indentationUnits;
            spaces= 0;
        } else if (DefaultCodeFormatterConstants.MIXED.equals(tabChar)){
            int tabWidth= getTabWidth(options);
            int spaceEquivalents= indentationUnits * getIndentWidth(options);
            if (tabWidth > 0) {
                tabs= spaceEquivalents / tabWidth;
                spaces= spaceEquivalents % tabWidth;
            } else {
                tabs= 0;
                spaces= spaceEquivalents;
            }
        } else {
            // new indent type not yet handled
            Assert.isTrue(false);
            return null;
        }
        
        StringBuffer buffer= new StringBuffer(tabs + spaces);
        for(int i= 0; i < tabs; i++)
            buffer.append('\t');
        for(int i= 0; i < spaces; i++)
            buffer.append(' ');
        return buffer.toString();
    }
    
    public static String createFixIndentString(int fixIndentation, Map options) {
        if (options == null || fixIndentation < 0) {
            throw new IllegalArgumentException();
        }
        
        String tabChar= getStringValue(options, DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, RubyCore.TAB);
        
        final int tabs, spaces;
        if (RubyCore.SPACE.equals(tabChar)) {
            tabs= 0;
            spaces= fixIndentation;
        } else if (RubyCore.TAB.equals(tabChar)) {
        	int tabWidth= getTabWidth(options);
        	tabs= fixIndentation / tabWidth;
            spaces= 0;
        } else if (DefaultCodeFormatterConstants.MIXED.equals(tabChar)){
        	int tabWidth= getTabWidth(options);
            if (tabWidth > 0) {
                tabs= fixIndentation / tabWidth;
                spaces= fixIndentation % tabWidth;
            } else {
                tabs= 0;
                spaces= fixIndentation;
            }
        } else {
            // new indent type not yet handled
            Assert.isTrue(false);
            return null;
        }
        
        StringBuffer buffer= new StringBuffer(tabs + spaces);
        for(int i= 0; i < tabs; i++)
            buffer.append('\t');
        for(int i= 0; i < spaces; i++)
            buffer.append(' ');
        return buffer.toString();
    	
    }
    
    private static String getStringValue(Map options, String key, String def) {
        Object value= options.get(key);
        if (value instanceof String)
            return (String) value;
        return def;
    }
    
    /**
     * Returns the indentation of the given line in space equivalents.
     * Tab characters are counted using the given <code>tabWidth</code> and every other indent
     * character as one. This method analyzes the content of <code>line</code> up to the first
     * non-whitespace character.
     * 
     * @param line the string to measure the indent of
     * @param tabWidth the width of one tab in space equivalents
     * @return the measured indent width in space equivalents
     */
    public static int measureIndentInSpaces(CharSequence line, int tabWidth) {
        if (tabWidth < 0 || line == null) {
            throw new IllegalArgumentException();
        }
        
        int length= 0;
        int max= line.length();
        for (int i= 0; i < max; i++) {
            char ch= line.charAt(i);
            if (ch == '\t') {
                int reminder= length % tabWidth;
                length += tabWidth - reminder;
            } else if (isIndentChar(ch)) {
                length++;
            } else {
                return length;
            }
        }
        return length;
    }

    /**
     * Returns the leading indentation string of the given line. Note that the
     * returned string need not be equal to the leading whitespace as odd spaces
     * are not considered part of the indentation.
     * 
     * @param line
     *            the line to scan
     * @param tabWidth
     *            the size of one tab in space equivalents
     * @param indentWidth
     *            the width of one indentation unit in space equivalents
     * @return the indent part of <code>line</code>, but no odd spaces
     */
    public static String extractIndentString(String line, int tabWidth, int indentWidth) {
        if (tabWidth < 0 || indentWidth <= 0 || line == null) { throw new IllegalArgumentException(); }

        int size = line.length();
        int end = 0;

        int spaceEquivs = 0;
        int characters = 0;
        for (int i = 0; i < size; i++) {
            char c = line.charAt(i);
            if (c == '\t') {
                int remainder = spaceEquivs % tabWidth;
                spaceEquivs += tabWidth - remainder;
                characters++;
            } else if (isIndentChar(c)) {
                spaceEquivs++;
                characters++;
            } else {
                break;
            }
            if (spaceEquivs >= indentWidth) {
                end += characters;
                characters = 0;
                spaceEquivs = spaceEquivs % indentWidth;
            }
        }
        if (end == 0)
            return ""; //$NON-NLS-1$
        else if (end == size)
            return line;
        else
            return line.substring(0, end);
    }

    /**
     * Tests if a character is an indent character. Indent character are all
     * whitespace characters except the line delimiter characters.
     * 
     * @param ch
     *            The character to test
     * @return Returns <code>true</code> if this the character is a indent
     *         character
     */
    public static boolean isIndentChar(char ch) {
        return Character.isWhitespace(ch) && !isLineDelimiterChar(ch);
    }

    /**
     * Tests if a character is a line delimiter character.
     * 
     * @param ch
     *            The character to test
     * @return Returns <code>true</code> if this the character is a line
     *         delimiter character
     */
    public static boolean isLineDelimiterChar(char ch) {
        return ch == '\n' || ch == '\r';
    }

	public static String extractIndentString(String line, Map options) {
		return extractIndentString(line, getTabWidth(options), getIndentWidth(options));		
	}

}
