package org.rubypeople.rdt.core.formatter;

import java.util.Map;

import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.formatter.DefaultCodeFormatterOptions;

public class DefaultCodeFormatterConstants {

    /**
     * <pre>
     *            FORMATTER / Possible value for the option FORMATTER_TAB_CHAR
     * </pre>
     * 
     * @since 0.8.0
     * @see RubyCore#TAB
     * @see RubyCore#SPACE
     * @see #FORMATTER_TAB_CHAR
     */
    public static final String MIXED = "mixed"; //$NON-NLS-1$

    /**
     * <pre>
     *            FORMATTER / Option to specify the equivalent number of spaces that represents one indentation 
     *                - option id:         &quot;org.rubypeople.rdt.core.formatter.indentation.size&quot;
     *                - possible values:   &quot;&lt;n&gt;&quot;, where n is zero or a positive integer
     *                - default:           &quot;4&quot;
     * </pre>
     * 
     * <p>
     * This option is used only if the tab char is set to MIXED.
     * </p>
     * 
     * @see #FORMATTER_TAB_CHAR
     * @since 0.8.0
     */
    public static final String FORMATTER_INDENTATION_SIZE = RubyCore.PLUGIN_ID
            + ".formatter.indentation.size"; //$NON-NLS-1$

    /**
     * <pre>
     *            FORMATTER / Option to specify the tabulation size
     *                - option id:         &quot;org.rubypeople.rdt.core.formatter.tabulation.char&quot;
     *                - possible values:   { TAB, SPACE, MIXED }
     *                - default:           TAB
     * </pre>
     * 
     * More values may be added in the future.
     * 
     * @see RubyCore#TAB
     * @see RubyCore#SPACE
     * @see #MIXED
     * @since 0.8.0
     */
    public static final String FORMATTER_TAB_CHAR = RubyCore.PLUGIN_ID
            + ".formatter.tabulation.char"; //$NON-NLS-1$
    /**
     * <pre>
     *            FORMATTER / Option to specify the equivalent number of spaces that represents one tabulation 
     *                - option id:         &quot;org.rubypeople.rdt.core.formatter.tabulation.size&quot;
     *                - possible values:   &quot;&lt;n&gt;&quot;, where n is zero or a positive integer
     *                - default:           &quot;4&quot;
     * </pre>
     * 
     * @since 0.8.0
     */
    public static final String FORMATTER_TAB_SIZE = RubyCore.PLUGIN_ID
            + ".formatter.tabulation.size"; //$NON-NLS-1$

    /**
     * <pre>
     *        FORMATTER / Value to set an option to true.
     * </pre>
     * 
     * @since 0.8.0
     */
    public static final String FALSE = "false"; //$NON-NLS-1$

    /**
     * <pre>
     *      FORMATTER / Value to set an option to false.
     * </pre>
     * 
     * @since 0.8.0
     */
    public static final String TRUE = "true"; //$NON-NLS-1$

    /**
     * <pre>
     *     FORMATTER / Option to specify the length of the page. Beyond this length, the formatter will try to split the code
     *         - option id:         &quot;org.rubypeople.rdt.core.formatter.lineSplit&quot;
     *         - possible values:   &quot;&lt;n&gt;&quot;, where n is zero or a positive integer
     *         - default:           &quot;80&quot;
     * </pre>
     * 
     * @since 0.8.0
     */
    public static final String FORMATTER_LINE_SPLIT = RubyCore.PLUGIN_ID + ".formatter.lineSplit"; //$NON-NLS-1$

    /**
     * <pre>
     *    FORMATTER / Option to ident empty lines
     *        - option id:         &quot;org.rubypeople.rdt.core.formatter.indent_empty_lines&quot;
     *        - possible values:   { TRUE, FALSE }
     *        - default:           FALSE
     * </pre>
     * 
     * @see #TRUE
     * @see #FALSE
     * @since 0.8.0
     */
    public static final String FORMATTER_INDENT_EMPTY_LINES = RubyCore.PLUGIN_ID
            + ".formatter.indent_empty_lines"; //$NON-NLS-1$   
    
    /**
     * <pre>
     *    FORMATTER / Option to ident empty lines
     *        - option id:         &quot;org.rubypeople.rdt.core.formatter.indent_case_body&quot;
     *        - possible values:   { TRUE, FALSE }
     *        - default:           FALSE
     * </pre>
     * 
     * @see #TRUE
     * @see #FALSE
     * @since 1.0
     */
    public static final String FORMATTER_INDENT_CASE_BODY = RubyCore.PLUGIN_ID
            + ".formatter.indent_case_body"; //$NON-NLS-1$   

    /**
     * <pre>
     *   FORMATTER / Option to use tabulations only for leading indentations 
     *       - option id:         &quot;org.rubypeople.rdt.core.formatter.use_tabs_only_for_leading_indentations&quot;
     *       - possible values:   { TRUE, FALSE }
     *       - default:           FALSE
     * </pre>
     * 
     * @see #TRUE
     * @see #FALSE
     * @since 0.8.0
     */
    public static final String FORMATTER_USE_TABS_ONLY_FOR_LEADING_INDENTATIONS = RubyCore.PLUGIN_ID
            + ".formatter.use_tabs_only_for_leading_indentations"; //$NON-NLS-1$

    /**
     * <pre>
     *  FORMATTER / Option to control whether comments are formatted
     *      - option id:         &quot;org.rubypeople.rdt.core.formatter.comment.format_comments&quot;
     *      - possible values:   { TRUE, FALSE }
     *      - default:           TRUE
     * </pre>
     * 
     * @see #TRUE
     * @see #FALSE
     * @since 0.8.0
     */
    public final static String FORMATTER_COMMENT_FORMAT = RubyCore.PLUGIN_ID
            + ".formatter.comment.format_comments"; //$NON-NLS-1$

    /**
     * <pre>
     *  FORMATTER / Option to control whether the header comment of a Ruby source file is formatted
     *      - option id:         &quot;org.rubypeople.rdt.core.formatter.comment.format_header&quot;
     *      - possible values:   { TRUE, FALSE }
     *      - default:           FALSE
     * </pre>
     * 
     * @see #TRUE
     * @see #FALSE
     * @since 0.8.0
     */
    public final static String FORMATTER_COMMENT_FORMAT_HEADER = RubyCore.PLUGIN_ID
            + ".formatter.comment.format_header"; //$NON-NLS-1$

    /**
     * <pre>
     *  FORMATTER / Option to specify the line length for comments.
     *      - option id:         &quot;org.rubypeople.rdt.core.formatter.comment.line_length&quot;
     *      - possible values:   &quot;&lt;n&gt;&quot;, where n is zero or a positive integer
     *      - default:           &quot;80&quot;
     * </pre>
     * 
     * @since 0.8.0
     */
    public final static String FORMATTER_COMMENT_LINE_LENGTH = RubyCore.PLUGIN_ID + ".formatter.comment.line_length"; //$NON-NLS-1$

    /**
     * <pre>
     * FORMATTER / Option to control whether blank lines are cleared inside comments
     *     - option id:         "org.rubypeople.rdt.core.formatter.comment.clear_blank_lines"
     *     - possible values:   { TRUE, FALSE }
     *     - default:           FALSE
     * </pre>
     * @see #TRUE
     * @see #FALSE
     * @since 0.8.0
     */ 
    public final static String FORMATTER_COMMENT_CLEAR_BLANK_LINES = RubyCore.PLUGIN_ID +  ".formatter.comment.clear_blank_lines"; //$NON-NLS-1$
    
    
    /**
     * Returns the default Eclipse formatter settings
     * 
     * @return the Eclipse default settings
     * @since 0.8.0
     */
    public static Map getEclipseDefaultSettings() {
        return DefaultCodeFormatterOptions.getEclipseDefaultSettings().getMap();
    }

    public static Map getRubyConventionsSettings() {
        return DefaultCodeFormatterOptions.getRubyConventionsSettings().getMap();
    }

}
