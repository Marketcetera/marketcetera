package org.rubypeople.rdt.internal.formatter;

import java.util.HashMap;
import java.util.Map;

import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.formatter.DefaultCodeFormatterConstants;

public class DefaultCodeFormatterOptions {

    private static final int DEFAULT_INDENT_SIZE = 4;
    private static final int DEFAULT_TAB_SIZE = 4;
    
    public static final int TAB = 1;
    public static final int SPACE = 2;
    public static final int MIXED = 4;

    public int indentation_size;
    public int tab_char;
    public int tab_size;
    public int comment_line_length;
    
	public boolean indent_case_body = false;
	public boolean indent_empty_lines = true;

    public static DefaultCodeFormatterOptions getDefaultSettings() {
        DefaultCodeFormatterOptions options = new DefaultCodeFormatterOptions();
        options.setDefaultSettings();
        return options;
    }

    public static DefaultCodeFormatterOptions getEclipseDefaultSettings() {
        DefaultCodeFormatterOptions options = new DefaultCodeFormatterOptions();
        options.setEclipseDefaultSettings();
        return options;
    }

    public static DefaultCodeFormatterOptions getRubyConventionsSettings() {
        DefaultCodeFormatterOptions options = new DefaultCodeFormatterOptions();
        options.setRubyConventionsSettings();
        return options;
    }

    private DefaultCodeFormatterOptions() {
        // cannot be instantiated
    }

    public DefaultCodeFormatterOptions(Map settings) {
        setDefaultSettings();
        if (settings == null) return;
        set(settings);
    }

    public void setDefaultSettings() {
        this.tab_char = TAB;
        this.tab_size = DEFAULT_TAB_SIZE;
        this.indentation_size = DEFAULT_INDENT_SIZE;
        this.indent_case_body = true;
        this.indent_empty_lines = true;
    }

    public void setEclipseDefaultSettings() {
        setRubyConventionsSettings();
    }

    public void setRubyConventionsSettings() {
        setDefaultSettings();
        this.tab_char = SPACE;
        this.tab_size = 2;
        this.indentation_size = 2;
        this.indent_case_body = true;
        this.indent_empty_lines = true;
    }

    public Map getMap() {
        Map options = new HashMap();
        options.put(DefaultCodeFormatterConstants.FORMATTER_COMMENT_LINE_LENGTH, Integer.toString(this.comment_line_length));
        options.put(DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE, Integer
                .toString(this.indentation_size));
        switch (this.tab_char) {
        case SPACE:
            options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, RubyCore.SPACE);
            break;
        case TAB:
            options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, RubyCore.TAB);
            break;
        case MIXED:
            options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR,
                    DefaultCodeFormatterConstants.MIXED);
            break;
        }
        options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, Integer
                .toString(this.tab_size));
        options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_CASE_BODY, Boolean
                .toString(this.indent_case_body));
        options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_EMPTY_LINES, Boolean
                .toString(this.indent_empty_lines));
        return options;
    }

    public void set(Map settings) {
    	final Object indentCaseBodyOption = settings.get(DefaultCodeFormatterConstants.FORMATTER_INDENT_CASE_BODY);
    	if (indentCaseBodyOption != null) {
            try {
                this.indent_case_body = Boolean.parseBoolean((String) indentCaseBodyOption);
            } catch (NumberFormatException e) {
                this.indent_case_body = false;
            } catch(ClassCastException e) {
                this.indent_case_body = false;
            }
        }    	
    	final Object indentEmptyLinesOption = settings.get(DefaultCodeFormatterConstants.FORMATTER_INDENT_EMPTY_LINES);
    	if (indentEmptyLinesOption != null) {
            try {
                this.indent_empty_lines = Boolean.parseBoolean((String) indentEmptyLinesOption);
            } catch (NumberFormatException e) {
                this.indent_empty_lines = true;
            } catch(ClassCastException e) {
                this.indent_empty_lines = true;
            }
        }    	
        final Object commentLineLengthOption = settings.get(DefaultCodeFormatterConstants.FORMATTER_COMMENT_LINE_LENGTH);
        if (commentLineLengthOption != null) {
            try {
                this.comment_line_length = Integer.parseInt((String) commentLineLengthOption);
            } catch (NumberFormatException e) {
                this.comment_line_length = 80;
            } catch(ClassCastException e) {
                this.comment_line_length = 80;
            }
        }
        final Object indentationSizeOption = settings
                .get(DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE);
        if (indentationSizeOption != null) {
            try {
                this.indentation_size = Integer.parseInt((String) indentationSizeOption);
            } catch (NumberFormatException e) {
                this.indentation_size = DEFAULT_INDENT_SIZE;
            } catch (ClassCastException e) {
                this.indentation_size = DEFAULT_INDENT_SIZE;
            }
        }
        final Object tabSizeOption = settings.get(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
        if (tabSizeOption != null) {
            try {
                this.tab_size = Integer.parseInt((String) tabSizeOption);
            } catch (NumberFormatException e) {
                this.tab_size = DEFAULT_TAB_SIZE;
            } catch (ClassCastException e) {
                this.tab_size = DEFAULT_TAB_SIZE;
            }
        }
        final Object useTabOption = settings.get(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR);
        if (useTabOption != null) {
            if (RubyCore.TAB.equals(useTabOption)) {
                this.tab_char = TAB;
            } else if (RubyCore.SPACE.equals(useTabOption)) {
                this.tab_char = SPACE;
            } else {
                this.tab_char = MIXED;
            }
        }
    }

}
