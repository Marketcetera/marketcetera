package org.rubypeople.rdt.internal.formatter;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.formatter.CodeFormatter;
import org.rubypeople.rdt.core.formatter.DefaultCodeFormatterConstants;
import org.rubypeople.rdt.core.formatter.Indents;

public class OldCodeFormatter extends CodeFormatter {

    private final static String BLOCK_BEGIN_RE = "(class|module|def|if|unless|case|while|until|for|begin|do)";
    private String BLOCK_MID_RE = "(else|elsif|rescue|ensure)";
    private final static String BLOCK_END_RE = "(end)";
    private final static String DELIMITER_RE = "[?$/(){}#\\`.:\\]\\[]";
    private final static String[] LITERAL_BEGIN_LITERALS = { "\"", "'", "=begin", "%[Qqrxw]?.",
            "/", "<<[\\-]?[']?[a-zA-Z_]+[']?"}; // FIXME Need to handle array concats that looks like heredocs properly i.e. "@recipients<< '<'<<recipients<<'>'"
    private final static String[] LITERAL_END_RES = { "[^\\\\](\\\\\\\\)*\"",
            "[^\\\\](\\\\\\\\)*'", "=end", "", "", ""};
    private final int BLOCK_BEGIN_PAREN = 2;
    private final int BLOCK_MID_PAREN = 5;
    private final int BLOCK_END_PAREN = 8;
    private final int LITERAL_BEGIN_PAREN = 10;

    private static Pattern MODIFIER_RE;
    private static Pattern OPERATOR_RE;
    private static Pattern NON_BLOCK_DO_RE;

    private static String LITERAL_BEGIN_RE; // automatically concatenated from
                                            // LITERAL_BEGIN_LITERALS
    private static Pattern[] LITERAL_END_RES_COMPILED;
    static {
        LITERAL_END_RES_COMPILED = new Pattern[LITERAL_END_RES.length];
        for (int i = 0; i < LITERAL_END_RES.length; i++) {
            try {
                LITERAL_END_RES_COMPILED[i] = Pattern.compile(LITERAL_END_RES[i]);
            } catch (PatternSyntaxException e) {
                System.out.println(e);
            }
        }
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        for (int i = 0; i < LITERAL_BEGIN_LITERALS.length; i++) {
            sb.append(LITERAL_BEGIN_LITERALS[i]);
            if (i < LITERAL_BEGIN_LITERALS.length - 1) {
                sb.append("|");
            }
        }
        sb.append(")");
        LITERAL_BEGIN_RE = sb.toString();
        try {
            MODIFIER_RE = Pattern.compile("if|unless|while|until|rescue");
            OPERATOR_RE = Pattern.compile("[\\-,.+*/%&|\\^~=<>:]");
            NON_BLOCK_DO_RE = Pattern.compile("(^|[\\s])(while|until|for|rescue)[\\s]");
        } catch (PatternSyntaxException e) {
            System.out.println(e);
        }
    }

    private DefaultCodeFormatterOptions preferences;
    private Map options;

    public OldCodeFormatter() {
        this(new DefaultCodeFormatterOptions(DefaultCodeFormatterConstants
                .getRubyConventionsSettings()), null);
    }

    public OldCodeFormatter(DefaultCodeFormatterOptions preferences) {
        this(preferences, null);
    }

    public OldCodeFormatter(DefaultCodeFormatterOptions defaultCodeFormatterOptions, Map options) {
        if (options != null) {
            this.options = options;
            this.preferences = new DefaultCodeFormatterOptions(options);
        } else {
            this.options = RubyCore.getOptions();
            this.preferences = new DefaultCodeFormatterOptions(DefaultCodeFormatterConstants
                    .getRubyConventionsSettings());
        }
        if (defaultCodeFormatterOptions != null) {
            this.preferences.set(defaultCodeFormatterOptions.getMap());
        }
    }

    public OldCodeFormatter(Map options) {
        this(null, options);
    }

    /**
     * @deprecated As of 0.8.0 we're moving to use CodeFormatters that spit out TextEdits.
     * @param unformatted
     * @return
     */
    public synchronized String formatString(String unformatted) {
        AbstractBlockMarker firstAbstractBlockMarker = this.createBlockMarkerList(unformatted);
        if (isDebug()) {
            firstAbstractBlockMarker.print();
        }
        int initialIndentLevel = Indents.measureIndentUnits(unformatted, preferences.tab_size, preferences.indentation_size);        
        try {
            return this.formatString(unformatted, firstAbstractBlockMarker, initialIndentLevel);
        } catch (PatternSyntaxException ex) {
            return unformatted;
        }
    }

    private boolean isDebug() {
        String codeFormatterOption = Platform.getDebugOption(RubyCore.PLUGIN_ID + "/codeformatter");
        boolean isDebug = codeFormatterOption == null ? false : codeFormatterOption
                .equalsIgnoreCase("true");
        return isDebug;
    }

    protected String formatString(String unformatted, AbstractBlockMarker abstractBlockMarker, int initialIndentLevel)
            throws PatternSyntaxException {
        Pattern pat = Pattern.compile("\n");
        String[] lines = pat.split(unformatted);
        IndentationState state = null;
        StringBuffer formatted = new StringBuffer();
        Pattern whitespacePattern = Pattern.compile("^[\t ]*");
        for (int i = 0; i < lines.length; i++) {                 
            Matcher whitespaceMatcher = whitespacePattern.matcher(lines[i]);
            whitespaceMatcher.find();
            int leadingWhitespace = whitespaceMatcher.end(0);
            if (state == null) {
            	  state = new IndentationState(unformatted, leadingWhitespace, initialIndentLevel);
            }
            state.incPos(leadingWhitespace);
            String strippedLine = lines[i].substring(leadingWhitespace);
            AbstractBlockMarker newBlockMarker = this.findNextBlockMarker(abstractBlockMarker,
                    state.getPos(), state);
            if (newBlockMarker != null) {
                newBlockMarker.indentBeforePrint(state);
                newBlockMarker.appendIndentedLine(formatted, state, lines[i], strippedLine, options);
                newBlockMarker.indentAfterPrint(state);
                abstractBlockMarker = newBlockMarker;
            } else {
                abstractBlockMarker.appendIndentedLine(formatted, state, lines[i], strippedLine, options);
            }
            if (i != lines.length - 1) {
                formatted.append("\n");
            }
            state.incPos(strippedLine.length() + 1);
        }
        if (unformatted.lastIndexOf("\n") == unformatted.length() - 1) {
            formatted.append("\n");
        }
        return formatted.toString();
    }

    private AbstractBlockMarker findNextBlockMarker(AbstractBlockMarker abstractBlockMarker,
            int pos, IndentationState state) {
        AbstractBlockMarker startBlockMarker = abstractBlockMarker;
        while (abstractBlockMarker.getNext() != null
                && abstractBlockMarker.getNext().getPos() <= pos) {
            if (abstractBlockMarker != startBlockMarker) {
                abstractBlockMarker.indentBeforePrint(state);
                abstractBlockMarker.indentAfterPrint(state);
            }
            abstractBlockMarker = abstractBlockMarker.getNext();
        }
        return startBlockMarker == abstractBlockMarker ? null : abstractBlockMarker;
    }

    protected AbstractBlockMarker createBlockMarkerList(String unformatted) {
        Pattern pat = null;

        try {
            pat = Pattern.compile("(^|[\\s]|;)" + BLOCK_BEGIN_RE + "($|[\\s]|" + DELIMITER_RE
                    + ")|(^|[\\s])" + getBlockMiddleRegex() + "($|[\\s]|" + DELIMITER_RE + ")|(^|[\\s]|;)"
                    + BLOCK_END_RE + "($|[\\s]|;)|" + LITERAL_BEGIN_RE + "|" + DELIMITER_RE);
        } catch (PatternSyntaxException e) {
            System.out.println(e);
        }
        int pos = 0;
        AbstractBlockMarker lastBlockMarker = new NeutralMarker("start", 0);
        AbstractBlockMarker firstBlockMarker = lastBlockMarker;
        Matcher re = pat.matcher(unformatted);
        while (pos != -1 && re.find(pos)) {
            AbstractBlockMarker newBlockMarker = null;
            if (re.group(BLOCK_BEGIN_PAREN) != null) {
                pos = re.end(BLOCK_BEGIN_PAREN);
                String blockBeginStr = re.group(BLOCK_BEGIN_PAREN);
                if (MODIFIER_RE.matcher(blockBeginStr).matches()
                        && !this.isRubyExprBegin(unformatted, re.start(BLOCK_BEGIN_PAREN),
                                "modifier")) {
                    continue;
                }
                if (blockBeginStr.equals("do")
                        && this.isNonBlockDo(unformatted, re.start(BLOCK_BEGIN_PAREN))) {
                    continue;
                }
                newBlockMarker = new BeginBlockMarker(re.group(BLOCK_BEGIN_PAREN), re
                        .start(BLOCK_BEGIN_PAREN));
            } else if (re.group(BLOCK_MID_PAREN) != null) {
                pos = re.end(BLOCK_MID_PAREN);       
                String blockMiddleStr = re.group(BLOCK_MID_PAREN);
                if (MODIFIER_RE.matcher(blockMiddleStr).matches()
                        && !this.isRubyExprBegin(unformatted, re.start(BLOCK_MID_PAREN),
                                "modifier")) {
                    continue;
                }
                newBlockMarker = new MidBlockMarker(re.group(BLOCK_MID_PAREN), re
                        .start(BLOCK_MID_PAREN));
            } else if (re.group(BLOCK_END_PAREN) != null) {
                pos = re.end(BLOCK_END_PAREN);
                newBlockMarker = new EndBlockMarker(re.group(BLOCK_END_PAREN), re
                        .start(BLOCK_END_PAREN));
            } else if (re.group(LITERAL_BEGIN_PAREN) != null) {
                pos = re.end(LITERAL_BEGIN_PAREN);
                String matchedLiteralBegin = re.group(LITERAL_BEGIN_PAREN);
                if (matchedLiteralBegin.startsWith("%")) {
                    int delimitChar = matchedLiteralBegin.charAt(matchedLiteralBegin.length() - 1);
                    boolean expand = matchedLiteralBegin.charAt(1) != 'q';
                    if (delimitChar == '[') {
                        pos = this.forwardString(unformatted, pos, '[', ']', expand);
                    } else if (delimitChar == '(') {
                        pos = this.forwardString(unformatted, pos, '(', ')', expand);
                    } else if (delimitChar == '{') {
                        pos = this.forwardString(unformatted, pos, '{', '}', expand);
                    } else if (delimitChar == '<') {
                        pos = this.forwardString(unformatted, pos, '<', '>', expand);
                    } else {
                        pos = unformatted.indexOf(delimitChar, pos);
                    }
                } else if (matchedLiteralBegin.startsWith("/")) {
                    // we do not consider reg exp over multiple lines. Therefore
                    // a reg exp over
                    // mutliple lines might get formatted. On the other hand
                    // code between
                    // two division slashes on several lines is being formatted.
                    // We could avoid that
                    // behaviour only if we could make a difference between
                    // slashes for division and
                    // slashes for regular expressions.
                    int posClosingSlash = this.forwardString(unformatted, pos, ' ', "/", true);
                    if (posClosingSlash == pos) {
                        continue;
                    }
                    int posNextLine = unformatted.indexOf("\n", pos);
                    if (posNextLine != -1 && posClosingSlash > posNextLine) {
                        continue;
                    }
                    pos = posClosingSlash;
                } else if (matchedLiteralBegin.startsWith("'")) {
                    if (pos > 1 && unformatted.charAt(pos - 2) == '$') {
                        continue;
                    }
                    pos = this.forwardString(unformatted, pos, ' ', "'", true);
                } else if (matchedLiteralBegin.startsWith("<<")) {
                    int startId = 2;
                    int endId = matchedLiteralBegin.length();
                    boolean isMinus = (matchedLiteralBegin.charAt(startId) == '-');
                    if (isMinus) {
                        startId += 1;
                    }
                    if (startId < matchedLiteralBegin.length() - 1
                            && matchedLiteralBegin.charAt(startId) == '\'') {
                        startId += 1;
                        endId -= 1;
                    }
                    String reStr = (isMinus ? "" : "\n")
                            + matchedLiteralBegin.substring(startId, endId);
                    try {
                        Pattern idSearch = Pattern.compile(reStr);
                        Matcher matcher = idSearch.matcher(unformatted);
                        if (matcher.find(pos)) {
                            pos = matcher.end(0);
                        } else {
                            pos = -1;
                        }
                    } catch (PatternSyntaxException e1) {
                        continue;
                    }
                } else {
                    for (int i = 0; i < LITERAL_BEGIN_LITERALS.length; i++) {
                        if (LITERAL_BEGIN_LITERALS[i].equals(matchedLiteralBegin)) {
                            Pattern matchEnd = LITERAL_END_RES_COMPILED[i];
                            pos = -1;
                            Matcher tmpMatch = matchEnd.matcher(unformatted);
                            if (tmpMatch.find(re.end(LITERAL_BEGIN_PAREN) - 1)) {
                                pos = tmpMatch.end(0);
                            }
                            break;
                        }
                    }
                }
                newBlockMarker = new NoFormattingMarker(matchedLiteralBegin, re
                        .start(LITERAL_BEGIN_PAREN));
                if (pos != -1) {
                    lastBlockMarker.setNext(newBlockMarker);
                    lastBlockMarker = newBlockMarker;
                    newBlockMarker = new NeutralMarker("", pos);
                }

            } else {
                String delimiter = re.group(0);
                if (delimiter.equals("#")) {
                    pos = unformatted.indexOf("\n", re.end(0));
                    continue;
                } else if (delimiter.equals("{")) {
                    newBlockMarker = new BeginBlockMarker("{", re.start(0));
                } else if (delimiter.equals("}")) {
                    newBlockMarker = new EndBlockMarker("}", re.start(0));
                } else if (delimiter.equals("(")) {
                    newBlockMarker = new FixLengthMarker("(", re.start(0));
                } else if (delimiter.equals(")")) {
                    newBlockMarker = new NeutralMarker(")", re.start(0));
                }
                pos = re.end(0);
            }

            if (newBlockMarker == null) {
                continue;
            }

            if (lastBlockMarker != null) {
//            	if (!lastBlockMarker.getKeyword().equals("begin") && newBlockMarker.getKeyword().equals("rescue")) {
//            		// we have a rescue modifier?
//            		continue;
//            	}
                lastBlockMarker.setNext(newBlockMarker);
            }
            lastBlockMarker = newBlockMarker;
        }
        return firstBlockMarker;
    }

	private String getBlockMiddleRegex() {
		if (this.preferences.indent_case_body) {
			return BLOCK_MID_RE;
		}
		StringBuffer buffer = new StringBuffer(BLOCK_MID_RE);
		buffer.insert(1, "when|");
		return buffer.toString();		
	}

    /*
     * (defun ruby-forward-string (term &optional end no-error expand) (let ((n
     * 1) (c (string-to-char term)) (re (if expand (concat
     * "[^\\]\\(\\\\\\\\\\)*\\([" term "]\\|\\(#{\\)\\)") (concat
     * "[^\\]\\(\\\\\\\\\\)*[" term "]")))) (while (and (re-search-forward re
     * end no-error) (if (match-beginning 3) (ruby-forward-string "}{" end
     * no-error nil) (> (setq n (if (eq (char-before (point)) c) (1- n) (1+ n)))
     * 0))) (forward-char -1)) (cond ((zerop n)) (no-error nil) (error
     * "unterminated string"))))
     */

    protected int forwardString(String unformatted, int pos, char opening, char closing,
            boolean expand) {
        return this.forwardString(unformatted, pos, opening, "\\" + opening + "\\" + closing,
                expand);
    }

    protected int forwardString(String unformatted, int pos, char opening, String term,
            boolean expand) {
        int n = 1;
        try {
            Pattern pat = Pattern.compile(expand ? "[" + term + "]|(#\\{)" : "[" + term + "]");
            Matcher re = pat.matcher(unformatted);
            while (re.find(pos) && n > 0) {
                if (re.group(1) != null) {
                    pos = this.forwardString(unformatted, re.end(1), '{', "\\{\\}", expand);
                } else {
                    pos = re.end(0);
                    if (pos > 2 && unformatted.charAt(pos - 2) == '\\'
                            && unformatted.charAt(pos - 3) != '\\') {
                        continue;
                    }
                    if (re.group(0).charAt(0) == opening) {
                        n += 1;
                    } else {
                        n -= 1;
                    }
                }
            }
        } catch (PatternSyntaxException e) {
            e.printStackTrace();

        }
        return pos;
    }

    /*
     * (defun ruby-expr-beg (&optional option) (save-excursion (store-match-data
     * nil) (skip-chars-backward " \t") (cond ((bolp) t) ((looking-at "\\?") (or
     * (bolp) (forward-char -1)) (not (looking-at "\\sw"))) (t (forward-char -1)
     * (or (looking-at ruby-operator-re) (looking-at "[\\[({,;]") (and (not (eq
     * option 'modifier)) (looking-at "[!?]")) (and (looking-at ruby-symbol-re)
     * (skip-chars-backward ruby-symbol-chars) (cond ((or (looking-at
     * ruby-block-beg-re) (looking-at ruby-block-op-re) (looking-at
     * ruby-block-mid-re)) (goto-char (match-end 0)) (looking-at "\\>")) (t (and
     * (not (eq option 'expr-arg)) (looking-at "[a-zA-Z][a-zA-z0-9_]* +/[^
     * \t]"))))))))))
     */

    protected int skipCharsBackward(String unformatted, int pos) {
        // skipCharsBackward returns the position of the first char which is not
        // tab or space left from pos and is in the
        // same line as pos
        do {
            if (pos == 0) { return 0; }
            if (unformatted.charAt(pos - 1) == '\n') { return pos; }
            pos -= 1;
        } while (unformatted.charAt(pos) == '\t' || unformatted.charAt(pos) == ' ');
        return pos;

    }

    protected int backToIndentation(String unformatted, int pos) {
        do {
            if (pos == 0) { return 0; }
            if (unformatted.charAt(pos - 1) == '\n') {
                break;
            }
            pos -= 1;
        } while (true);
        while (unformatted.charAt(pos) == '\t' || unformatted.charAt(pos) == ' ') {
            pos += 1;
            if (pos == unformatted.length()) {
                break;
            }
        }
        return pos;
    }

    protected int posOfLineStart(String unformatted, int pos) {
        do {
            if (pos == 0) { return 0; }
            if (unformatted.charAt(pos - 1) == '\n') {
                break;
            }
            pos -= 1;
        } while (true);
        return pos;
    }

    protected boolean matchREBackward(String str, Pattern re) {
        int pos = str.length() - 1;
        while (pos >= 0) {
            if (str.charAt(pos) == ';') { return false; }
            if (re.matcher(str).find(pos)) { return true; }
            pos -= 1;
        }
        return false;
    }

    protected boolean isRubyExprBegin(String unformatted, int pos, String option) {
        int firstNonSpaceCharInLine = this.skipCharsBackward(unformatted, pos);
        if (firstNonSpaceCharInLine == 0 || unformatted.charAt(firstNonSpaceCharInLine - 1) == '\n') { return true; }
        char c = unformatted.charAt(firstNonSpaceCharInLine);
        if (c == ';') { return true; }
        String c_str = "" + c;
        if (OPERATOR_RE.matcher(c_str).matches()) { return true; }
        return false;
    }

    protected boolean isNonBlockDo(String unformatted, int pos) {
        int lineStart = this.posOfLineStart(unformatted, pos);
        return this.matchREBackward(unformatted.substring(lineStart, pos), NON_BLOCK_DO_RE);
    }

    public TextEdit format(int kind, String source, int offset, int length, int indentationLevel, String lineSeparator) {
        String newText = formatString(source.substring(offset, length), indentationLevel);
        return new ReplaceEdit(offset, length, newText);
    }

	private String formatString(String unformatted, int indentationLevel) {
		AbstractBlockMarker firstAbstractBlockMarker = this.createBlockMarkerList(unformatted);
        if (isDebug()) {
            firstAbstractBlockMarker.print();
        }
        try {
            return this.formatString(unformatted, firstAbstractBlockMarker, indentationLevel);
        } catch (PatternSyntaxException ex) {
            return unformatted;
        }
	}

}
