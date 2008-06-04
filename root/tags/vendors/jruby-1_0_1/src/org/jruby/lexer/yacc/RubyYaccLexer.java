/***** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
 * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
 * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
 * Copyright (C) 2004-2006 Thomas E Enebo <enebo@acm.org>
 * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
 * Copyright (C) 2004-2005 David Corbin <dcorbin@users.sourceforge.net>
 * Copyright (C) 2005 Zach Dennis <zdennis@mktec.com>
 * Copyright (C) 2006 Thomas Corbat <tcorbat@hsr.ch>
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/
package org.jruby.lexer.yacc;

import java.io.IOException;

import java.math.BigInteger;

import org.jruby.ast.BackRefNode;
import org.jruby.ast.BignumNode;
import org.jruby.ast.CommentNode;
import org.jruby.ast.FixnumNode;
import org.jruby.ast.FloatNode;
import org.jruby.ast.NthRefNode;
import org.jruby.common.IRubyWarnings;
import org.jruby.parser.BlockStaticScope;
import org.jruby.parser.ParserSupport;
import org.jruby.parser.StaticScope;
import org.jruby.parser.Tokens;
import org.jruby.util.IdUtil;

/** This is a port of the MRI lexer to Java it is compatible to Ruby 1.8.1.
 */
public class RubyYaccLexer {
    // Last token read via yylex().
    private int token;
    
    // Value of last token which had a value associated with it.
    Object yaccValue;

    // Stream of data that yylex() examines.
    private LexerSource src;
    
    // Used for tiny smidgen of grammar in lexer (see setParserSupport())
    private ParserSupport parserSupport = null;

    // What handles warnings
    private IRubyWarnings warnings;

    // Additional context surrounding tokens that both the lexer and
    // grammar use.
    private LexState lex_state;
    
    // Tempory buffer to build up a potential token.  Consumer takes responsibility to reset 
    // this before use.
    private StringBuffer tokenBuffer = new StringBuffer(60);

    private StackState conditionState = new StackState();
    private StackState cmdArgumentState = new StackState();
    private StrTerm lex_strterm;
    private boolean commandStart;

    // Give a name to a value.  Enebo: This should be used more.
    static final int EOF = 0;

    // ruby constants for strings (should this be moved somewhere else?)
    static final int STR_FUNC_ESCAPE=0x01;
    static final int STR_FUNC_EXPAND=0x02;
    static final int STR_FUNC_REGEXP=0x04;
    static final int STR_FUNC_QWORDS=0x08;
    static final int STR_FUNC_SYMBOL=0x10;
    static final int STR_FUNC_INDENT=0x20;

    private final int str_squote = 0;
    private final int str_dquote = STR_FUNC_EXPAND;
    private final int str_xquote = STR_FUNC_EXPAND;
    private final int str_regexp = STR_FUNC_REGEXP | STR_FUNC_ESCAPE | STR_FUNC_EXPAND;
    private final int str_ssym   = STR_FUNC_SYMBOL;
    private final int str_dsym   = STR_FUNC_SYMBOL | STR_FUNC_EXPAND;
    
    public RubyYaccLexer() {
    	reset();
    }
    
    public void reset() {
    	token = 0;
    	yaccValue = null;
    	src = null;
        lex_state = null;
        resetStacks();
        lex_strterm = null;
        commandStart = true;
    }
    
    /**
     * How the parser advances to the next token.
     * 
     * @return true if not at end of file (EOF).
     */
    public boolean advance() throws IOException {
        return (token = yylex()) != EOF;
    }
    
    /**
     * Last token read from the lexer at the end of a call to yylex()
     * 
     * @return last token read
     */
    public int token() {
        return token;
    }

    public StringBuffer getTokenBuffer() {
        return tokenBuffer;
    }
    
    /**
     * Value of last token (if it is a token which has a value).
     * 
     * @return value of last value-laden token
     */
    public Object value() {
        return yaccValue;
    }

    public ISourcePositionFactory getPositionFactory() {
        return src.getPositionFactory();
    }
    
    /**
     * Get position information for Token/Node that follows node represented by startPosition 
     * and current lexer location.
     * 
     * @param startPosition previous node/token
     * @param inclusive include previous node into position information of current node
     * @return a new position
     */
    public ISourcePosition getPosition(ISourcePosition startPosition, boolean inclusive) {
    	return src.getPosition(startPosition, inclusive); 
    }
    
    public ISourcePosition getPosition() {
        return src.getPosition(null, false);
    }

    /**
     * Parse must pass its support object for some check at bottom of
     * yylex().  Ruby does it this way as well (i.e. a little parsing
     * logic in the lexer).
     * 
     * @param parserSupport
     */
    public void setParserSupport(ParserSupport parserSupport) {
        this.parserSupport = parserSupport;
    }

    /**
     * Allow the parser to set the source for its lexer.
     * 
     * @param source where the lexer gets raw data
     */
    public void setSource(LexerSource source) {
        this.src = source;
    }

    public StrTerm getStrTerm() {
        return lex_strterm;
    }
    
    public void setStrTerm(StrTerm strterm) {
        this.lex_strterm = strterm;
    }

    public void resetStacks() {
        conditionState.reset();
        cmdArgumentState.reset();
    }
    
    public void setWarnings(IRubyWarnings warnings) {
        this.warnings = warnings;
    }


    public void setState(LexState state) {
        this.lex_state = state;
    }

    public StackState getCmdArgumentState() {
        return cmdArgumentState;
    }

    public StackState getConditionState() {
        return conditionState;
    }
    
    public void setValue(Object yaccValue) {
        this.yaccValue = yaccValue;
    }

    private boolean isNext_identchar() throws IOException {
        char c = src.read();
        src.unread(c);

        return c != EOF && (Character.isLetterOrDigit(c) || c == '_');
    }
    
    private Object getInteger(String value, int radix) {
        try {
            return new FixnumNode(getPosition(), Long.parseLong(value, radix));
        } catch (NumberFormatException e) {
            return new BignumNode(getPosition(), new BigInteger(value, radix));
        }
    }

    /**
	 * Do the next characters from the source match provided String in a case insensitive manner.  
     * If so, then consume those characters and that string.  Otherwise, consume none of them and 
     * return null.
	 * 
	 * @param s to be matched against
     * @return string if string matches, null otherwise
     */ 
    private String isNextNoCase(String s) throws IOException {
    	StringBuffer buf = new StringBuffer();
    	
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            char r = src.read();
            buf.append(r);
            
            if (Character.toLowerCase(c) != r &&
                Character.toUpperCase(c) != r) {
            	src.unreadMany(buf);
                return null;
            }
        }

        return buf.toString();
    }

	/**
	 * @param c the character to test
	 * @return true if character is a hex value (0-9a-f)
	 */
    static final boolean isHexChar(char c) {
        return Character.isDigit(c) || ('a' <= c && c <= 'f') || ('A' <= c && c <= 'F');
    }

    /**
	 * @param c the character to test
     * @return true if character is an octal value (0-7)
	 */
    static final boolean isOctChar(char c) {
        return '0' <= c && c <= '7';
    }
    
    /**
     * @param c is character to be compared
     * @return whether c is an identifier or not
     */
    private static final boolean isIdentifierChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }
    
    /**
     * What type/kind of quote are we dealing with?
     * 
     * @param c first character the the quote construct
     * @return a token that specifies the quote type
     */
    private int parseQuote(char c) throws IOException {
        char begin, end;
        boolean shortHand;
        
        // Short-hand (e.g. %{,%.,%!,... versus %Q{).
        if (!Character.isLetterOrDigit(c)) {
            begin = c;
            c = 'Q';
            shortHand = true;
        // Long-hand (e.g. %Q{}).
        } else {
            shortHand = false;
            begin = src.read();
            if (Character.isLetterOrDigit(begin) /* no mb || ismbchar(term)*/) {
                throw new SyntaxException(getPosition(), "unknown type of %string");
            }
        }
        if (c == EOF || begin == EOF) {
            throw new SyntaxException(getPosition(), "unterminated quoted string meets end of file");
        }
        
        // Figure end-char.  '\0' is special to indicate begin=end and that no nesting?
        if (begin == '(') end = ')';
        else if (begin == '[') end = ']';
        else if (begin == '{') end = '}';
        else if (begin == '<') end = '>';
        else { end = begin; begin = '\0'; };

        switch (c) {
        case 'Q':
            lex_strterm = new StringTerm(str_dquote, end, begin);
            yaccValue = new Token("%"+ (shortHand ? (""+end) : ("" + c + begin)), getPosition());
            return Tokens.tSTRING_BEG;

        case 'q':
            lex_strterm = new StringTerm(str_squote, end, begin);
            yaccValue = new Token("%"+c+begin, getPosition());
            return Tokens.tSTRING_BEG;

        case 'W':
            lex_strterm = new StringTerm(str_dquote | STR_FUNC_QWORDS, end, begin);
            do {c = src.read();} while (Character.isWhitespace(c));
            src.unread(c);
            yaccValue = new Token("%"+c+begin, getPosition());
            return Tokens.tWORDS_BEG;

        case 'w':
            lex_strterm = new StringTerm(str_squote | STR_FUNC_QWORDS, end, begin);
            do {c = src.read();} while (Character.isWhitespace(c));
            src.unread(c);
            yaccValue = new Token("%"+c+begin, getPosition());
            return Tokens.tQWORDS_BEG;

        case 'x':
            lex_strterm = new StringTerm(str_xquote, end, begin);
            yaccValue = new Token("%"+c+begin, getPosition());
            return Tokens.tXSTRING_BEG;

        case 'r':
            lex_strterm = new StringTerm(str_regexp, end, begin);
            yaccValue = new Token("%"+c+begin, getPosition());
            return Tokens.tREGEXP_BEG;

        case 's':
            lex_strterm = new StringTerm(str_ssym, end, begin);
            lex_state = LexState.EXPR_FNAME;
            yaccValue = new Token("%"+c+begin, getPosition());
            return Tokens.tSYMBEG;

        default:
            throw new SyntaxException(getPosition(), "Unknown type of %string. Expected 'Q', 'q', 'w', 'x', 'r' or any non letter character, but found '" + c + "'.");
        }
    }
    
    private int hereDocumentIdentifier() throws IOException {
        char c = src.read(); 
        int term;

        int func = 0;
        if (c == '-') {
            c = src.read();
            func = STR_FUNC_INDENT;
        }
        
        if (c == '\'' || c == '"' || c == '`') {
            if (c == '\'') {
                func |= str_squote;
            } else if (c == '"') {
                func |= str_dquote;
            } else {
                func |= str_xquote; 
            }

            tokenBuffer.setLength(0);
            term = c;
            while ((c = src.read()) != EOF && c != term) {
                tokenBuffer.append(c);
            }
            if (c == EOF) {
                throw new SyntaxException(getPosition(), "unterminated here document identifier");
            }	
        } else {
            if (!isIdentifierChar(c)) {
                src.unread(c);
                if ((func & STR_FUNC_INDENT) != 0) {
                    src.unread('-');
                }
                return 0;
            }
            tokenBuffer.setLength(0);
            term = '"';
            func |= str_dquote;
            do {
                tokenBuffer.append(c);
            } while ((c = src.read()) != EOF && isIdentifierChar(c));
            src.unread(c);
        }

        String line = src.readLine() + '\n';
        String tok = tokenBuffer.toString();
        lex_strterm = new HeredocTerm(tok, func, line);

        if (term == '`') {
            yaccValue = new Token("`", getPosition());
            return Tokens.tXSTRING_BEG;
        }
        
        yaccValue = new Token("\"", getPosition());
        // Hacky: Advance position to eat newline here....
        getPosition();
        return Tokens.tSTRING_BEG;
    }
    
    private void arg_ambiguous() {
        warnings.warning(getPosition(), "Ambiguous first argument; make sure.");
    }

    /**
     * Read a comment up to end of line.  When found each comment will get stored away into
     * the parser result so that any interested party can use them as they seem fit.  One idea
     * is that IDE authors can do distance based heuristics to associate these comments to the
     * AST node they think they belong to.
     * 
     * @param c last character read from lexer source
     * @return newline or eof value 
     */
    protected int readComment(char c) throws IOException {
        ISourcePosition startPosition = src.getPosition();
        tokenBuffer.setLength(0);
        tokenBuffer.append(c);

        // FIXME: Consider making a better LexerSource.readLine
        while ((c = src.read()) != '\n') {
            if (c == EOF) {
                break;
            }
            tokenBuffer.append(c);
        }
        src.unread(c);
        
        // Store away each comment to parser result so IDEs can do whatever they want with them.
        ISourcePosition position = startPosition.union(getPosition());
        parserSupport.getResult().addComment(new CommentNode(position, tokenBuffer.toString()));
        
        return c;
    }
    
    /*
     * Not normally used, but is left in here since it can be useful in debugging
     * grammar and lexing problems.
    private void printToken(int token) {
        //System.out.print("LOC: " + support.getPosition() + " ~ ");
        
        switch (token) {
        	case Tokens.yyErrorCode: System.err.print("yyErrorCode,"); break;
        	case Tokens.kCLASS: System.err.print("kClass,"); break;
        	case Tokens.kMODULE: System.err.print("kModule,"); break;
        	case Tokens.kDEF: System.err.print("kDEF,"); break;
        	case Tokens.kUNDEF: System.err.print("kUNDEF,"); break;
        	case Tokens.kBEGIN: System.err.print("kBEGIN,"); break;
        	case Tokens.kRESCUE: System.err.print("kRESCUE,"); break;
        	case Tokens.kENSURE: System.err.print("kENSURE,"); break;
        	case Tokens.kEND: System.err.print("kEND,"); break;
        	case Tokens.kIF: System.err.print("kIF,"); break;
        	case Tokens.kUNLESS: System.err.print("kUNLESS,"); break;
        	case Tokens.kTHEN: System.err.print("kTHEN,"); break;
        	case Tokens.kELSIF: System.err.print("kELSIF,"); break;
        	case Tokens.kELSE: System.err.print("kELSE,"); break;
        	case Tokens.kCASE: System.err.print("kCASE,"); break;
        	case Tokens.kWHEN: System.err.print("kWHEN,"); break;
        	case Tokens.kWHILE: System.err.print("kWHILE,"); break;
        	case Tokens.kUNTIL: System.err.print("kUNTIL,"); break;
        	case Tokens.kFOR: System.err.print("kFOR,"); break;
        	case Tokens.kBREAK: System.err.print("kBREAK,"); break;
        	case Tokens.kNEXT: System.err.print("kNEXT,"); break;
        	case Tokens.kREDO: System.err.print("kREDO,"); break;
        	case Tokens.kRETRY: System.err.print("kRETRY,"); break;
        	case Tokens.kIN: System.err.print("kIN,"); break;
        	case Tokens.kDO: System.err.print("kDO,"); break;
        	case Tokens.kDO_COND: System.err.print("kDO_COND,"); break;
        	case Tokens.kDO_BLOCK: System.err.print("kDO_BLOCK,"); break;
        	case Tokens.kRETURN: System.err.print("kRETURN,"); break;
        	case Tokens.kYIELD: System.err.print("kYIELD,"); break;
        	case Tokens.kSUPER: System.err.print("kSUPER,"); break;
        	case Tokens.kSELF: System.err.print("kSELF,"); break;
        	case Tokens.kNIL: System.err.print("kNIL,"); break;
        	case Tokens.kTRUE: System.err.print("kTRUE,"); break;
        	case Tokens.kFALSE: System.err.print("kFALSE,"); break;
        	case Tokens.kAND: System.err.print("kAND,"); break;
        	case Tokens.kOR: System.err.print("kOR,"); break;
        	case Tokens.kNOT: System.err.print("kNOT,"); break;
        	case Tokens.kIF_MOD: System.err.print("kIF_MOD,"); break;
        	case Tokens.kUNLESS_MOD: System.err.print("kUNLESS_MOD,"); break;
        	case Tokens.kWHILE_MOD: System.err.print("kWHILE_MOD,"); break;
        	case Tokens.kUNTIL_MOD: System.err.print("kUNTIL_MOD,"); break;
        	case Tokens.kRESCUE_MOD: System.err.print("kRESCUE_MOD,"); break;
        	case Tokens.kALIAS: System.err.print("kALIAS,"); break;
        	case Tokens.kDEFINED: System.err.print("kDEFINED,"); break;
        	case Tokens.klBEGIN: System.err.print("klBEGIN,"); break;
        	case Tokens.klEND: System.err.print("klEND,"); break;
        	case Tokens.k__LINE__: System.err.print("k__LINE__,"); break;
        	case Tokens.k__FILE__: System.err.print("k__FILE__,"); break;
        	case Tokens.tIDENTIFIER: System.err.print("tIDENTIFIER["+ value() + "],"); break;
        	case Tokens.tFID: System.err.print("tFID[" + value() + "],"); break;
        	case Tokens.tGVAR: System.err.print("tGVAR[" + value() + "],"); break;
        	case Tokens.tIVAR: System.err.print("tIVAR[" + value() +"],"); break;
        	case Tokens.tCONSTANT: System.err.print("tCONSTANT["+ value() +"],"); break;
        	case Tokens.tCVAR: System.err.print("tCVAR,"); break;
        	case Tokens.tINTEGER: System.err.print("tINTEGER,"); break;
        	case Tokens.tFLOAT: System.err.print("tFLOAT,"); break;
            case Tokens.tSTRING_CONTENT: System.err.print("tSTRING_CONTENT[" + yaccValue + "],"); break;
            case Tokens.tSTRING_BEG: System.err.print("tSTRING_BEG,"); break;
            case Tokens.tSTRING_END: System.err.print("tSTRING_END,"); break;
            case Tokens.tSTRING_DBEG: System.err.print("STRING_DBEG,"); break;
            case Tokens.tSTRING_DVAR: System.err.print("tSTRING_DVAR,"); break;
            case Tokens.tXSTRING_BEG: System.err.print("tXSTRING_BEG,"); break;
            case Tokens.tREGEXP_BEG: System.err.print("tREGEXP_BEG,"); break;
            case Tokens.tREGEXP_END: System.err.print("tREGEXP_END,"); break;
            case Tokens.tWORDS_BEG: System.err.print("tWORDS_BEG,"); break;
            case Tokens.tQWORDS_BEG: System.err.print("tQWORDS_BEG,"); break;
        	case Tokens.tBACK_REF: System.err.print("tBACK_REF,"); break;
        	case Tokens.tNTH_REF: System.err.print("tNTH_REF,"); break;
        	case Tokens.tUPLUS: System.err.print("tUPLUS"); break;
        	case Tokens.tUMINUS: System.err.print("tUMINUS,"); break;
        	case Tokens.tPOW: System.err.print("tPOW,"); break;
        	case Tokens.tCMP: System.err.print("tCMP,"); break;
        	case Tokens.tEQ: System.err.print("tEQ,"); break;
        	case Tokens.tEQQ: System.err.print("tEQQ,"); break;
        	case Tokens.tNEQ: System.err.print("tNEQ,"); break;
        	case Tokens.tGEQ: System.err.print("tGEQ,"); break;
        	case Tokens.tLEQ: System.err.print("tLEQ,"); break;
        	case Tokens.tANDOP: System.err.print("tANDOP,"); break;
        	case Tokens.tOROP: System.err.print("tOROP,"); break;
        	case Tokens.tMATCH: System.err.print("tMATCH,"); break;
        	case Tokens.tNMATCH: System.err.print("tNMATCH,"); break;
        	case Tokens.tDOT2: System.err.print("tDOT2,"); break;
        	case Tokens.tDOT3: System.err.print("tDOT3,"); break;
        	case Tokens.tAREF: System.err.print("tAREF,"); break;
        	case Tokens.tASET: System.err.print("tASET,"); break;
        	case Tokens.tLSHFT: System.err.print("tLSHFT,"); break;
        	case Tokens.tRSHFT: System.err.print("tRSHFT,"); break;
        	case Tokens.tCOLON2: System.err.print("tCOLON2,"); break;
        	case Tokens.tCOLON3: System.err.print("tCOLON3,"); break;
        	case Tokens.tOP_ASGN: System.err.print("tOP_ASGN,"); break;
        	case Tokens.tASSOC: System.err.print("tASSOC,"); break;
        	case Tokens.tLPAREN: System.err.print("tLPAREN,"); break;
        	case Tokens.tLPAREN_ARG: System.err.print("tLPAREN_ARG,"); break;
        	case Tokens.tLBRACK: System.err.print("tLBRACK,"); break;
        	case Tokens.tLBRACE: System.err.print("tLBRACE,"); break;
        	case Tokens.tSTAR: System.err.print("tSTAR,"); break;
        	case Tokens.tAMPER: System.err.print("tAMPER,"); break;
        	case Tokens.tSYMBEG: System.err.print("tSYMBEG,"); break;
        	case '\n': System.err.println("NL"); break;
        	default: System.err.print("'" + (int)token + "',"); break;
        }
    }

    // DEBUGGING HELP 
    private int yylex() throws IOException {
        int token = yylex2();
        
        printToken(token);
        
        return token;
    }
    */

    /**
     *  Returns the next token. Also sets yyVal is needed.
     *
     *@return    Description of the Returned Value
     */
    private int yylex() throws IOException {
        char c;
        boolean spaceSeen = false;
        boolean commandState;
        
        if (lex_strterm != null) {
			int tok = lex_strterm.parseString(this, src);
			if (tok == Tokens.tSTRING_END || tok == Tokens.tREGEXP_END) {
			    lex_strterm = null;
			    lex_state = LexState.EXPR_END;
			}
			return tok;
        }

        commandState = commandStart;
        commandStart = false;

        LexState last_state = lex_state;
        
        retry: for(;;) {
            c = src.read();
            switch(c) {
            case '\004':		/* ^D */
            case '\032':		/* ^Z */
            case 0:			/* end of script. */
                return 0;
           
                /* white spaces */
            case ' ': case '\t': case '\f': case '\r':
            case '\13': /* '\v' */
                getPosition();
                spaceSeen = true;
                continue retry;
            case '#':		/* it's a comment */
                if (readComment(c) == 0) return 0;
                    
                /* fall through */
            case '\n':
            	// Replace a string of newlines with a single one
                while((c = src.read()) == '\n') {
                    
                }
                src.unread( c );
                getPosition();

                if (lex_state == LexState.EXPR_BEG ||
                    lex_state == LexState.EXPR_FNAME ||
                    lex_state == LexState.EXPR_DOT ||
                    lex_state == LexState.EXPR_CLASS) {
                    continue retry;
                } 

                commandStart = true;
                lex_state = LexState.EXPR_BEG;
                return '\n';
                
            case '*':
                if ((c = src.read()) == '*') {
                    if ((c = src.read()) == '=') {
                        lex_state = LexState.EXPR_BEG;
                        yaccValue = new Token("**", getPosition());
                        return Tokens.tOP_ASGN;
                    }
                    src.unread(c);
                    yaccValue = new Token("**", getPosition());
                    c = Tokens.tPOW;
                } else {
                    if (c == '=') {
                        lex_state = LexState.EXPR_BEG;
                        yaccValue = new Token("*", getPosition());
                        return Tokens.tOP_ASGN;
                    }
                    src.unread(c);
                    if (lex_state.isArgument() && spaceSeen && !Character.isWhitespace(c)) {
                        warnings.warning(getPosition(), "`*' interpreted as argument prefix");
                        c = Tokens.tSTAR;
                    } else if (lex_state == LexState.EXPR_BEG || 
                            lex_state == LexState.EXPR_MID) {
                        c = Tokens.tSTAR;
                    } else {
                        c = Tokens.tSTAR2;
                    }
                    yaccValue = new Token("*", getPosition());
                }
                if (lex_state == LexState.EXPR_FNAME ||
                    lex_state == LexState.EXPR_DOT) {
                    lex_state = LexState.EXPR_ARG;
                } else {
                    lex_state = LexState.EXPR_BEG;
                }
                return c;

            case '!':
                lex_state = LexState.EXPR_BEG;
                if ((c = src.read()) == '=') {
                 yaccValue = new Token("!=",getPosition());
                 return Tokens.tNEQ;
                }
                if (c == '~') {
                    yaccValue = new Token("!~",getPosition());
                    return Tokens.tNMATCH;
                }
                src.unread(c);
                yaccValue = new Token("!",getPosition());
                return Tokens.tBANG;

            case '=':
                // documentation nodes
                if (src.wasBeginOfLine()) {
                    String equalLabel;
                    if ((equalLabel = isNextNoCase("begin")) != null) {
                        tokenBuffer.setLength(0);
                        tokenBuffer.append(equalLabel);
                        c = src.read();
                        
                        if (Character.isWhitespace(c)) {
                            // In case last next was the newline.
                            src.unread(c);
                            for (;;) {
                                c = src.read();
                                tokenBuffer.append(c);

                                // If a line is followed by a blank line put
                                // it back.
                                while (c == '\n') {
                                    c = src.read();
                                    tokenBuffer.append(c);
                                }
                                if (c == EOF) {
                                    throw new SyntaxException(getPosition(), "embedded document meets end of file");
                                }
                                if (c != '=') continue;
                                if (src.wasBeginOfLine() && (equalLabel = isNextNoCase("end")) != null) {
                                    tokenBuffer.append(equalLabel);
                                    tokenBuffer.append(src.readLine());
                                    src.unread('\n');
                                    break;
                                }
                            }
                            
                            parserSupport.getResult().addComment(new CommentNode(getPosition(), tokenBuffer.toString()));
                            continue retry;
                        }
						src.unread(c);
                    }
                }

                if (lex_state == LexState.EXPR_FNAME || lex_state == LexState.EXPR_DOT) {
                    lex_state = LexState.EXPR_ARG;
                } else { 
                    lex_state = LexState.EXPR_BEG;
                }

                c = src.read();
                if (c == '=') {
                    c = src.read();
                    if (c == '=') {
                        yaccValue = new Token("===", getPosition());
                        return Tokens.tEQQ;
                    }
                    src.unread(c);
                    yaccValue = new Token("==", getPosition());
                    return Tokens.tEQ;
                }
                if (c == '~') {
                    yaccValue = new Token("=~", getPosition());
                    return Tokens.tMATCH;
                } else if (c == '>') {
                    yaccValue = new Token("=>", getPosition());
                    return Tokens.tASSOC;
                }
                src.unread(c);
                yaccValue = new Token("=", getPosition());
                return '=';
                
            case '<':
                c = src.read();
                if (c == '<' &&
                        lex_state != LexState.EXPR_END &&
                        lex_state != LexState.EXPR_DOT &&
                        lex_state != LexState.EXPR_ENDARG && 
                        lex_state != LexState.EXPR_CLASS &&
                        (!lex_state.isArgument() || spaceSeen)) {
                    int tok = hereDocumentIdentifier();
                    if (tok != 0) return tok;
                }
                if (lex_state == LexState.EXPR_FNAME ||
                    lex_state == LexState.EXPR_DOT) {
                    lex_state = LexState.EXPR_ARG;
                } else {
                    lex_state = LexState.EXPR_BEG;
                }
                if (c == '=') {
                    if ((c = src.read()) == '>') {
                        yaccValue = new Token("<=>", getPosition());
                        return Tokens.tCMP;
                    }
                    src.unread(c);
                    yaccValue = new Token("<=", getPosition());
                    return Tokens.tLEQ;
                }
                if (c == '<') {
                    if ((c = src.read()) == '=') {
                        lex_state = LexState.EXPR_BEG;
                        yaccValue = new Token("<<", getPosition());
                        return Tokens.tOP_ASGN;
                    }
                    src.unread(c);
                    yaccValue = new Token("<<", getPosition());
                    return Tokens.tLSHFT;
                }
                yaccValue = new Token("<", getPosition());
                src.unread(c);
                return Tokens.tLT;
                
            case '>':
                if (lex_state == LexState.EXPR_FNAME ||
                    lex_state == LexState.EXPR_DOT) {
                    lex_state = LexState.EXPR_ARG;
                } else {
                    lex_state = LexState.EXPR_BEG;
                }

                if ((c = src.read()) == '=') {
                    yaccValue = new Token(">=", getPosition());
                    return Tokens.tGEQ;
                }
                if (c == '>') {
                    if ((c = src.read()) == '=') {
                        lex_state = LexState.EXPR_BEG;
                        yaccValue = new Token(">>", getPosition());
                        return Tokens.tOP_ASGN;
                    }
                    src.unread(c);
                    yaccValue = new Token(">>", getPosition());
                    return Tokens.tRSHFT;
                }
                src.unread(c);
                yaccValue = new Token(">", getPosition());
                return Tokens.tGT;

            case '"':
                lex_strterm = new StringTerm(str_dquote, '"', '\0');
                yaccValue = new Token("\"", getPosition());
                return Tokens.tSTRING_BEG;

            case '`':
                yaccValue = new Token("`", getPosition());
                if (lex_state == LexState.EXPR_FNAME) {
                    lex_state = LexState.EXPR_END;
                    return Tokens.tBACK_REF2;
                }
                if (lex_state == LexState.EXPR_DOT) {
                    if (commandState) {
                        lex_state = LexState.EXPR_CMDARG;
                    } else {
                        lex_state = LexState.EXPR_ARG;
                    }
                    return Tokens.tBACK_REF2;
                }
                lex_strterm = new StringTerm(str_xquote, '`', '\0');
                return Tokens.tXSTRING_BEG;

            case '\'':
                lex_strterm = new StringTerm(str_squote, '\'', '\0');
                yaccValue = new Token("'", getPosition());
                return Tokens.tSTRING_BEG;

            case '?':
                if (lex_state == LexState.EXPR_END || 
                    lex_state == LexState.EXPR_ENDARG) {
                    lex_state = LexState.EXPR_BEG;
                    yaccValue = new Token("?",getPosition());
                    return '?';
                }
                c = src.read();
                if (c == EOF) {
                    throw new SyntaxException(getPosition(), "incomplete character syntax");
                }
                if (Character.isWhitespace(c)){
                    if (!lex_state.isArgument()){
                        int c2 = 0;
                        switch (c) {
                        case ' ':
                            c2 = 's';
                            break;
                        case '\n':
                            c2 = 'n';
                            break;
                        case '\t':
                            c2 = 't';
                            break;
                            /* What is \v in C?
                        case '\v':
                            c2 = 'v';
                            break;
                            */
                        case '\r':
                            c2 = 'r';
                            break;
                        case '\f':
                            c2 = 'f';
                            break;
                        }
                        if (c2 != 0) {
                            warnings.warn(getPosition(), "invalid character syntax; use ?\\" + c2);
                        }
                    }
                    src.unread(c);
                    lex_state = LexState.EXPR_BEG;
                    yaccValue = new Token("?", getPosition());
                    return '?';
                /*} else if (ismbchar(c)) { // ruby - we don't support them either?
                    rb_warn("multibyte character literal not supported yet; use ?\\" + c);
                    support.unread(c);
                    lexState = LexState.EXPR_BEG;
                    return '?';*/
                } else if ((Character.isLetterOrDigit(c) || c == '_') &&
                        !src.peek('\n') && isNext_identchar()) {
                    src.unread(c);
                    lex_state = LexState.EXPR_BEG;
                    yaccValue = new Token("?", getPosition());
                    return '?';
                } else if (c == '\\') {
                    c = src.readEscape();
                }
                c &= 0xff;
                lex_state = LexState.EXPR_END;
                yaccValue = new FixnumNode(getPosition(), c);
                return Tokens.tINTEGER;

            case '&':
                if ((c = src.read()) == '&') {
                    lex_state = LexState.EXPR_BEG;
                    if ((c = src.read()) == '=') {
                        yaccValue = new Token("&&", getPosition());
                        lex_state = LexState.EXPR_BEG;
                        return Tokens.tOP_ASGN;
                    }
                    src.unread(c);
                    yaccValue = new Token("&&", getPosition());
                    return Tokens.tANDOP;
                }
                else if (c == '=') {
                    yaccValue = new Token("&", getPosition());
                    lex_state = LexState.EXPR_BEG;
                    return Tokens.tOP_ASGN;
                }
                src.unread(c);
                //tmpPosition is required because getPosition()'s side effects.
                //if the warning is generated, the getPosition() on line 954 (this line + 18) will create
                //a wrong position if the "inclusive" flag is not set.
                ISourcePosition tmpPosition = getPosition();
                if (lex_state.isArgument() && spaceSeen && !Character.isWhitespace(c)){
                    warnings.warning(tmpPosition, "`&' interpreted as argument prefix");
                    c = Tokens.tAMPER;
                } else if (lex_state == LexState.EXPR_BEG || 
                        lex_state == LexState.EXPR_MID) {
                    c = Tokens.tAMPER;
                } else {
                    c = Tokens.tAMPER2;
                }
                
                if (lex_state == LexState.EXPR_FNAME ||
                    lex_state == LexState.EXPR_DOT) {
                    lex_state = LexState.EXPR_ARG;
                } else {
                    lex_state = LexState.EXPR_BEG;
                }
                yaccValue = new Token("&", tmpPosition);
                return c;
                
            case '|':
                if ((c = src.read()) == '|') {
                    lex_state = LexState.EXPR_BEG;
                    if ((c = src.read()) == '=') {
                        lex_state = LexState.EXPR_BEG;
                        yaccValue = new Token("||", getPosition());
                        return Tokens.tOP_ASGN;
                    }
                    src.unread(c);
                    yaccValue = new Token("||", getPosition());
                    return Tokens.tOROP;
                }
                if (c == '=') {
                    lex_state = LexState.EXPR_BEG;
                    yaccValue = new Token("|", getPosition());
                    return Tokens.tOP_ASGN;
                }
                if (lex_state == LexState.EXPR_FNAME || 
                    lex_state == LexState.EXPR_DOT) {
                    lex_state = LexState.EXPR_ARG;
                } else {
                    lex_state = LexState.EXPR_BEG;
                }
                src.unread(c);
                yaccValue = new Token("|", getPosition());
                return Tokens.tPIPE;

            case '+':
                c = src.read();
                if (lex_state == LexState.EXPR_FNAME || 
                    lex_state == LexState.EXPR_DOT) {
                    lex_state = LexState.EXPR_ARG;
                    if (c == '@') {
                        yaccValue = new Token("+@", getPosition());
                        return Tokens.tUPLUS;
                    }
                    src.unread(c);
                    yaccValue = new Token("+", getPosition());
                    return Tokens.tPLUS;
                }
                if (c == '=') {
                    lex_state = LexState.EXPR_BEG;
                    yaccValue = new Token("+", getPosition());
                    return Tokens.tOP_ASGN;
                }
                if (lex_state == LexState.EXPR_BEG ||
                    lex_state == LexState.EXPR_MID ||
                        (lex_state.isArgument() && spaceSeen && !Character.isWhitespace(c))) {
                    if (lex_state.isArgument()) arg_ambiguous();
                    lex_state = LexState.EXPR_BEG;
                    src.unread(c);
                    if (Character.isDigit(c)) {
                        c = '+';
                        return parseNumber(c);
                    }
                    yaccValue = new Token("+", getPosition());
                    return Tokens.tUPLUS;
                }
                lex_state = LexState.EXPR_BEG;
                src.unread(c);
                yaccValue = new Token("+", getPosition());
                return Tokens.tPLUS;

            case '-':
                c = src.read();
                if (lex_state == LexState.EXPR_FNAME || lex_state == LexState.EXPR_DOT) {
                    lex_state = LexState.EXPR_ARG;
                    if (c == '@') {
                        yaccValue = new Token("-@", getPosition());
                        return Tokens.tUMINUS;
                    }
                    src.unread(c);
                    yaccValue = new Token("-", getPosition());
                    return Tokens.tMINUS;
                }
                if (c == '=') {
                    lex_state = LexState.EXPR_BEG;
                    yaccValue = new Token("-", getPosition());
                    return Tokens.tOP_ASGN;
                }
                if (lex_state == LexState.EXPR_BEG || lex_state == LexState.EXPR_MID ||
                        (lex_state.isArgument() && spaceSeen && !Character.isWhitespace(c))) {
                    if (lex_state.isArgument()) arg_ambiguous();
                    lex_state = LexState.EXPR_BEG;
                    src.unread(c);
                    yaccValue = new Token("-", getPosition());
                    if (Character.isDigit(c)) {
                        return Tokens.tUMINUS_NUM;
                    }
                    return Tokens.tUMINUS;
                }
                lex_state = LexState.EXPR_BEG;
                src.unread(c);
                yaccValue = new Token("-", getPosition());
                return Tokens.tMINUS;
                
            case '.':
                lex_state = LexState.EXPR_BEG;
                if ((c = src.read()) == '.') {
                    if ((c = src.read()) == '.') {
                        yaccValue = new Token("...", getPosition());
                        return Tokens.tDOT3;
                    }
                    src.unread(c);
                    yaccValue = new Token("..", getPosition());
                    return Tokens.tDOT2;
                }
                src.unread(c);
                if (Character.isDigit(c)) {
                    throw new SyntaxException(getPosition(), "no .<digit> floating literal anymore; put 0 before dot"); 
                }
                lex_state = LexState.EXPR_DOT;
                yaccValue = new Token(".", getPosition());
                return Tokens.tDOT;
            case '0' : case '1' : case '2' : case '3' : case '4' :
            case '5' : case '6' : case '7' : case '8' : case '9' :
                return parseNumber(c);
                
            case ')':
                conditionState.restart();
                cmdArgumentState.restart();
                lex_state = LexState.EXPR_END;
                yaccValue = new Token(")", getPosition());
                return Tokens.tRPAREN;
            case ']':
                conditionState.restart();
                cmdArgumentState.restart();
                lex_state = LexState.EXPR_END;
                yaccValue = new Token(")", getPosition());
                return Tokens.tRBRACK;
            case '}':
                conditionState.restart();
                cmdArgumentState.restart();
                lex_state = LexState.EXPR_END;
                yaccValue = new Token("}",getPosition());
                return Tokens.tRCURLY;

            case ':':
                c = src.read();
                if (c == ':') {
                    if (lex_state == LexState.EXPR_BEG ||
                        lex_state == LexState.EXPR_MID ||
                        lex_state == LexState.EXPR_CLASS || 
                        (lex_state.isArgument() && spaceSeen)) {
                        lex_state = LexState.EXPR_BEG;
                        yaccValue = new Token("::", getPosition());
                        return Tokens.tCOLON3;
                    }
                    lex_state = LexState.EXPR_DOT;
                    yaccValue = new Token(":",getPosition());
                    return Tokens.tCOLON2;
                }
                if (lex_state == LexState.EXPR_END || 
                    lex_state == LexState.EXPR_ENDARG || Character.isWhitespace(c)) {
                    src.unread(c);
                    lex_state = LexState.EXPR_BEG;
                    yaccValue = new Token(":",getPosition());
                    return ':';
                }
                switch (c) {
                case '\'':
                    lex_strterm = new StringTerm(str_ssym, c, '\0');
                    break;
                case '"':
                    lex_strterm = new StringTerm(str_dsym, c, '\0');
                    break;
                default:
                    src.unread(c);
                    break;
                }
                lex_state = LexState.EXPR_FNAME;
                yaccValue = new Token(":", getPosition());
                return Tokens.tSYMBEG;

            case '/':
                if (lex_state == LexState.EXPR_BEG || 
                    lex_state == LexState.EXPR_MID) {
                    lex_strterm = new StringTerm(str_regexp, '/', '\0');
                    yaccValue = new Token("/",getPosition());
                    return Tokens.tREGEXP_BEG;
                }
                
                if ((c = src.read()) == '=') {
                    yaccValue = new Token("/", getPosition());
                    lex_state = LexState.EXPR_BEG;
                    return Tokens.tOP_ASGN;
                }
                src.unread(c);
                if (lex_state.isArgument() && spaceSeen) {
                    if (!Character.isWhitespace(c)) {
                        arg_ambiguous();
                        lex_strterm = new StringTerm(str_regexp, '/', '\0');
                        yaccValue = new Token("/",getPosition());
                        return Tokens.tREGEXP_BEG;
                    }
                }
                if (lex_state == LexState.EXPR_FNAME || 
                    lex_state == LexState.EXPR_DOT) {
                    lex_state = LexState.EXPR_ARG;
                } else {
                    lex_state = LexState.EXPR_BEG;
                }
                yaccValue = new Token("/", getPosition());
                return Tokens.tDIVIDE;

            case '^':
                if ((c = src.read()) == '=') {
                    lex_state = LexState.EXPR_BEG;
                    yaccValue = new Token("^", getPosition());
                    return Tokens.tOP_ASGN;
                }
                if (lex_state == LexState.EXPR_FNAME || 
                    lex_state == LexState.EXPR_DOT) {
                    lex_state = LexState.EXPR_ARG;
                } else {
                    lex_state = LexState.EXPR_BEG;
                }
                src.unread(c);
                yaccValue = new Token("^", getPosition());
                return Tokens.tCARET;

            case ';':
                commandStart = true;
            case ',':
                lex_state = LexState.EXPR_BEG;
                yaccValue = new Token(",", getPosition());
                return c;

            case '~':
                if (lex_state == LexState.EXPR_FNAME || 
                    lex_state == LexState.EXPR_DOT) {
                    if ((c = src.read()) != '@') {
                        src.unread(c);
                    }
                }
                if (lex_state == LexState.EXPR_FNAME || 
                        lex_state == LexState.EXPR_DOT) {
                    lex_state = LexState.EXPR_ARG;
                } else {
                    lex_state = LexState.EXPR_BEG;
                }
                yaccValue = new Token("~", getPosition());
                return Tokens.tTILDE;
            case '(':
            	c = Tokens.tLPAREN2;
                commandStart = true;
                if (lex_state == LexState.EXPR_BEG || 
                    lex_state == LexState.EXPR_MID) {
                    c = Tokens.tLPAREN;
                } else if (spaceSeen) {
                    if (lex_state == LexState.EXPR_CMDARG) {
                        c = Tokens.tLPAREN_ARG;
                    } else if (lex_state == LexState.EXPR_ARG) {
                        warnings.warn(getPosition(), "don't put space before argument parentheses");
                        c = Tokens.tLPAREN2;
                    }
                }
                conditionState.stop();
                cmdArgumentState.stop();
                lex_state = LexState.EXPR_BEG;
                yaccValue = new Token("(", getPosition());
                return c;

            case '[':
                if (lex_state == LexState.EXPR_FNAME || 
                    lex_state == LexState.EXPR_DOT) {
                    lex_state = LexState.EXPR_ARG;
                    if ((c = src.read()) == ']') {
                        if (src.peek('=')) {
                            c = src.read();
                            yaccValue = new Token("[]=", getPosition());
                            return Tokens.tASET;
                        }
                        yaccValue = new Token("[]", getPosition());
                        return Tokens.tAREF;
                    }
                    src.unread(c);
                    yaccValue = new Token("[", getPosition());
                    return '[';
                } else if (lex_state == LexState.EXPR_BEG || 
                           lex_state == LexState.EXPR_MID) {
                    c = Tokens.tLBRACK;
                } else if (lex_state.isArgument() && spaceSeen) {
                    c = Tokens.tLBRACK;
                }
                lex_state = LexState.EXPR_BEG;
                conditionState.stop();
                cmdArgumentState.stop();
                yaccValue = new Token("[", getPosition());
                return c;
                
            case '{':
            	c = Tokens.tLCURLY;
            	
                if (lex_state.isArgument() || lex_state == LexState.EXPR_END) {
                    c = Tokens.tLCURLY;          /* block (primary) */
                } else if (lex_state == LexState.EXPR_ENDARG) {
                    c = Tokens.tLBRACE_ARG;  /* block (expr) */
                } else {
                    c = Tokens.tLBRACE;      /* hash */
                }
                conditionState.stop();
                cmdArgumentState.stop();
                lex_state = LexState.EXPR_BEG;
                yaccValue = new Token("{", getPosition());
                return c;

            case '\\':
                c = src.read();
                if (c == '\n') {
                    spaceSeen = true;
                    continue retry; /* skip \\n */
                }
                src.unread(c);
                yaccValue = new Token("\\", getPosition());
                return '\\';

            case '%':
                if (lex_state == LexState.EXPR_BEG || 
                    lex_state == LexState.EXPR_MID) {
                    return parseQuote(src.read());
                }
                if ((c = src.read()) == '=') {
                    lex_state = LexState.EXPR_BEG;
                    yaccValue = new Token("%", getPosition());
                    return Tokens.tOP_ASGN;
                }
                if (lex_state.isArgument() && spaceSeen && !Character.isWhitespace(c)) {
                    return parseQuote(c);
                }
                if (lex_state == LexState.EXPR_FNAME || 
                    lex_state == LexState.EXPR_DOT) {
                    lex_state = LexState.EXPR_ARG;
                } else {
                    lex_state = LexState.EXPR_BEG;
                }
                src.unread(c);
                yaccValue = new Token("%", getPosition());
                return Tokens.tPERCENT;

            case '$':
                lex_state = LexState.EXPR_END;
                tokenBuffer.setLength(0);
                c = src.read();
                switch (c) {
                case '_':		/* $_: last read line string */
                    c = src.read();
                    if (isIdentifierChar(c)) {
                        tokenBuffer.append('$');
                        tokenBuffer.append('_');
                        break;
                    }
                    src.unread(c);
                    c = '_';
                    /* fall through */
                case '~':       /* $~: match-data */
                case '*':		/* $*: argv */
                case '$':		/* $$: pid */
                case '?':		/* $?: last status */
                case '!':		/* $!: error string */
                case '@':		/* $@: error position */
                case '/':		/* $/: input record separator */
                case '\\':		/* $\: output record separator */
                case ';':		/* $;: field separator */
                case ',':		/* $,: output field separator */
                case '.':		/* $.: last read line number */
                case '=':		/* $=: ignorecase */
                case ':':		/* $:: load path */
                case '<':		/* $<: reading filename */
                case '>':		/* $>: default output handle */
                case '\"':		/* $": already loaded files */
                    tokenBuffer.append('$');
                    tokenBuffer.append(c);
                    yaccValue = new Token(tokenBuffer.toString(), getPosition());
                    return Tokens.tGVAR;

                case '-':
                    tokenBuffer.append('$');
                    tokenBuffer.append(c);
                    c = src.read();
                    if (isIdentifierChar(c)) {
                        tokenBuffer.append(c);
                    } else {
                        src.unread(c);
                    }
                    yaccValue = new Token(tokenBuffer.toString(), getPosition());
                    /* xxx shouldn't check if valid option variable */
                    return Tokens.tGVAR;

                case '&':		/* $&: last match */
                case '`':		/* $`: string before last match */
                case '\'':		/* $': string after last match */
                case '+':		/* $+: string matches last paren. */
                    yaccValue = new BackRefNode(getPosition(), c);
                    return Tokens.tBACK_REF;

                case '1': case '2': case '3':
                case '4': case '5': case '6':
                case '7': case '8': case '9':
                    tokenBuffer.append('$');
                    do {
                        tokenBuffer.append(c);
                        c = src.read();
                    } while (Character.isDigit(c));
                    src.unread(c);
                    if(last_state == LexState.EXPR_FNAME) {
                        yaccValue = new Token(tokenBuffer.toString(), getPosition());
                        return Tokens.tGVAR;
                    } else {
                        yaccValue = new NthRefNode(getPosition(), Integer.parseInt(tokenBuffer.substring(1)));
                        return Tokens.tNTH_REF;
                    }
                default:
                    if (!isIdentifierChar(c)) {
                        src.unread(c);
                        yaccValue = new Token("$", getPosition());
                        return '$';
                    }
                case '0':
                    tokenBuffer.append('$');
                }
                break;

            case '@':
                c = src.read();
                tokenBuffer.setLength(0);
                tokenBuffer.append('@');
                if (c == '@') {
                    tokenBuffer.append('@');
                    c = src.read();
                }
                if (Character.isDigit(c)) {
                    if (tokenBuffer.length() == 1) {
                        throw new SyntaxException(getPosition(), "`@" + c + "' is not allowed as an instance variable name");
                    }
                    throw new SyntaxException(getPosition(), "`@@" + c + "' is not allowed as a class variable name");
                }
                if (!isIdentifierChar(c)) {
                    src.unread(c);
                    yaccValue = new Token("@", getPosition());
                    return '@';
                }
                break;

            case '_':
                if (src.wasBeginOfLine() && src.matchString("_END__\n", false)) {
                	parserSupport.getResult().setEndSeen(true);
                    return 0;
                }
                tokenBuffer.setLength(0);
                break;

            default:
                if (!isIdentifierChar(c)) {
                    throw new SyntaxException(getPosition(), "Invalid char `\\" + Integer.parseInt(""+c, 8) + "' in expression");
                }
            
                tokenBuffer.setLength(0);
                break;
            }
    
            do {
                tokenBuffer.append(c);
                /* no special multibyte character handling is needed in Java
                 * if (ismbchar(c)) {
                    int i, len = mbclen(c)-1;

                    for (i = 0; i < len; i++) {
                        c = src.read();
                        tokenBuffer.append(c);
                    }
                }*/
                c = src.read();
            } while (isIdentifierChar(c));
            
            char peek = src.read();
            if ((c == '!' || c == '?') && 
                isIdentifierChar(tokenBuffer.charAt(0)) && peek != '=') {
                src.unread(peek);
                tokenBuffer.append(c);
            } else {
            	src.unread(peek);
            	src.unread(c);
            }
            
            int result = 0;

            switch (tokenBuffer.charAt(0)) {
                case '$':
                    lex_state = LexState.EXPR_END;
                    result = Tokens.tGVAR;
                    break;
                case '@':
                    lex_state = LexState.EXPR_END;
                    if (tokenBuffer.charAt(1) == '@') {
                        result = Tokens.tCVAR;
                    } else {
                        result = Tokens.tIVAR;
                    }
                    break;

                default:
                	char last = tokenBuffer.charAt(tokenBuffer.length() - 1);
                    if (last == '!' || last == '?') {
                        result = Tokens.tFID;
                    } else {
                        if (lex_state == LexState.EXPR_FNAME) {
                            if ((c = src.read()) == '=') { 
                            	char c2 = src.read();
                        	
                            	if (c2 != '~' && c2 != '>' &&
                            		(c2 != '=' || (c2 == '\n' && src.peek('>')))) {
                            		result = Tokens.tIDENTIFIER;
                            		tokenBuffer.append(c);
                                    src.unread(c2);
                            	} else { 
                            		src.unread(c2);
                            		src.unread(c);
                            	}
                        	} else {
                            	src.unread(c);
                            }
                        }
                        if (result == 0 && Character.isUpperCase(tokenBuffer.charAt(0))) {
                            result = Tokens.tCONSTANT;
                        } else {
                            result = Tokens.tIDENTIFIER;
                        }
                    }

                    if (lex_state != LexState.EXPR_DOT) {
                        /* See if it is a reserved word.  */
                        Keyword keyword = Keyword.getKeyword(tokenBuffer.toString(), tokenBuffer.length());
                        if (keyword != null) {
                            // enum lex_state
                            LexState state = lex_state;

                            lex_state = keyword.state;
                            if (state.isExprFName()) {
                                yaccValue = new Token(keyword.name, getPosition());
                            } else {
                                yaccValue = new Token(tokenBuffer.toString(), getPosition());
                            }
                            if (keyword.id0 == Tokens.kDO) {
                                if (conditionState.isInState()) {
                                    return Tokens.kDO_COND;
                                }
                                if (cmdArgumentState.isInState() && state != LexState.EXPR_CMDARG) {
                                    return Tokens.kDO_BLOCK;
                                }
                                if (state == LexState.EXPR_ENDARG) {
                                    return Tokens.kDO_BLOCK;
                                }
                                return Tokens.kDO;
                            }

                            if (state == LexState.EXPR_BEG) {
                                return keyword.id0;
                            }
							if (keyword.id0 != keyword.id1) {
								lex_state = LexState.EXPR_BEG;
							}
							return keyword.id1;
                        }
                    }

                    if (lex_state == LexState.EXPR_BEG ||
                            lex_state == LexState.EXPR_MID ||
                            lex_state == LexState.EXPR_DOT ||
                            lex_state == LexState.EXPR_ARG ||
                            lex_state == LexState.EXPR_CMDARG) {
                        if (commandState) {
                            lex_state = LexState.EXPR_CMDARG;
                        } else {
                            lex_state = LexState.EXPR_ARG;
                        }
                    } else {
                        lex_state = LexState.EXPR_END;
                    }
            }
            
            String tempVal = tokenBuffer.toString();

            // Lame: parsing logic made it into lexer in ruby...So we
            // are emulating
            // FIXME:  I believe this is much simpler now...
            StaticScope scope = parserSupport.getCurrentScope();
            if (IdUtil.getVarType(tempVal) == IdUtil.LOCAL_VAR &&
                    (scope instanceof BlockStaticScope && (scope.isDefined(tempVal) >= 0)) ||
                    (scope.getLocalScope().isDefined(tempVal) >= 0)) {
                lex_state = LexState.EXPR_END;
            }

            yaccValue = new Token(tempVal, getPosition());

            return result;
        }
    }

    /**
     *  Parse a number from the input stream.
     *
     *@param c The first character of the number.
     *@return A int constant wich represents a token.
     */
    private int parseNumber(char c) throws IOException {
        lex_state = LexState.EXPR_END;

        tokenBuffer.setLength(0);

        if (c == '-') {
        	tokenBuffer.append(c);
            c = src.read();
        } else if (c == '+') {
        	// We don't append '+' since Java number parser gets confused
            c = src.read();
        }
        
        char nondigit = '\0';

        if (c == '0') {
            int startLen = tokenBuffer.length();

            switch (c = src.read()) {
                case 'x' :
                case 'X' : //  hexadecimal
                    c = src.read();
                    if (isHexChar(c)) {
                        for (;; c = src.read()) {
                            if (c == '_') {
                                if (nondigit != '\0') {
                                    break;
                                }
								nondigit = c;
                            } else if (isHexChar(c)) {
                                nondigit = '\0';
                                tokenBuffer.append(c);
                            } else {
                                break;
                            }
                        }
                    }
                    src.unread(c);

                    if (tokenBuffer.length() == startLen) {
                        throw new SyntaxException(getPosition(), "Hexadecimal number without hex-digits.");
                    } else if (nondigit != '\0') {
                        throw new SyntaxException(getPosition(), "Trailing '_' in number.");
                    }
                    yaccValue = getInteger(tokenBuffer.toString(), 16);
                    return Tokens.tINTEGER;
                case 'b' :
                case 'B' : // binary
                    c = src.read();
                    if (c == '0' || c == '1') {
                        for (;; c = src.read()) {
                            if (c == '_') {
                                if (nondigit != '\0') {
                                    break;
                                }
								nondigit = c;
                            } else if (c == '0' || c == '1') {
                                nondigit = '\0';
                                tokenBuffer.append(c);
                            } else {
                                break;
                            }
                        }
                    }
                    src.unread(c);

                    if (tokenBuffer.length() == startLen) {
                        throw new SyntaxException(getPosition(), "Binary number without digits.");
                    } else if (nondigit != '\0') {
                        throw new SyntaxException(getPosition(), "Trailing '_' in number.");
                    }
                    yaccValue = getInteger(tokenBuffer.toString(), 2);
                    return Tokens.tINTEGER;
                case 'd' :
                case 'D' : // decimal
                    c = src.read();
                    if (Character.isDigit(c)) {
                        for (;; c = src.read()) {
                            if (c == '_') {
                                if (nondigit != '\0') {
                                    break;
                                }
								nondigit = c;
                            } else if (Character.isDigit(c)) {
                                nondigit = '\0';
                                tokenBuffer.append(c);
                            } else {
                                break;
                            }
                        }
                    }
                    src.unread(c);

                    if (tokenBuffer.length() == startLen) {
                        throw new SyntaxException(getPosition(), "Binary number without digits.");
                    } else if (nondigit != '\0') {
                        throw new SyntaxException(getPosition(), "Trailing '_' in number.");
                    }
                    yaccValue = getInteger(tokenBuffer.toString(), 2);
                    return Tokens.tINTEGER;
                case '0' : case '1' : case '2' : case '3' : case '4' : //Octal
                case '5' : case '6' : case '7' : case '_' : 
                    for (;; c = src.read()) {
                        if (c == '_') {
                            if (nondigit != '\0') {
                                break;
                            }
							nondigit = c;
                        } else if (c >= '0' && c <= '7') {
                            nondigit = '\0';
                            tokenBuffer.append(c);
                        } else {
                            break;
                        }
                    }
                    if (tokenBuffer.length() > startLen) {
                        src.unread(c);

                        if (nondigit != '\0') {
                            throw new SyntaxException(getPosition(), "Trailing '_' in number.");
                        }

                        yaccValue = getInteger(tokenBuffer.toString(), 8);
                        return Tokens.tINTEGER;
                    }
                case '8' :
                case '9' :
                    throw new SyntaxException(getPosition(), "Illegal octal digit.");
                case '.' :
                case 'e' :
                case 'E' :
                	tokenBuffer.append('0');
                    break;
                default :
                    src.unread(c);
                    yaccValue = new FixnumNode(getPosition(), 0);
                    return Tokens.tINTEGER;
            }
        }

        boolean seen_point = false;
        boolean seen_e = false;

        for (;; c = src.read()) {
            switch (c) {
                case '0' :
                case '1' :
                case '2' :
                case '3' :
                case '4' :
                case '5' :
                case '6' :
                case '7' :
                case '8' :
                case '9' :
                    nondigit = '\0';
                    tokenBuffer.append(c);
                    break;
                case '.' :
                    if (nondigit != '\0') {
                        src.unread(c);
                        throw new SyntaxException(getPosition(), "Trailing '_' in number.");
                    } else if (seen_point || seen_e) {
                        src.unread(c);
                        return getNumberToken(tokenBuffer.toString(), true, nondigit);
                    } else {
                    	char c2;
                        if (!Character.isDigit(c2 = src.read())) {
                            src.unread(c2);
                        	src.unread('.');
                            if (c == '_') { 
                            		// Enebo:  c can never be antrhign but '.'
                            		// Why did I put this here?
                            } else {
                                yaccValue = getInteger(tokenBuffer.toString(), 10);
                                return Tokens.tINTEGER;
                            }
                        } else {
                            tokenBuffer.append('.');
                            tokenBuffer.append(c2);
                            seen_point = true;
                            nondigit = '\0';
                        }
                    }
                    break;
                case 'e' :
                case 'E' :
                    if (nondigit != '\0') {
                        throw new SyntaxException(getPosition(), "Trailing '_' in number.");
                    } else if (seen_e) {
                        src.unread(c);
                        return getNumberToken(tokenBuffer.toString(), true, nondigit);
                    } else {
                        tokenBuffer.append(c);
                        seen_e = true;
                        nondigit = c;
                        c = src.read();
                        if (c == '-' || c == '+') {
                            tokenBuffer.append(c);
                            nondigit = c;
                        } else {
                            src.unread(c);
                        }
                    }
                    break;
                case '_' : //  '_' in number just ignored
                    if (nondigit != '\0') {
                        throw new SyntaxException(getPosition(), "Trailing '_' in number.");
                    }
                    nondigit = c;
                    break;
                default :
                    src.unread(c);
                return getNumberToken(tokenBuffer.toString(), seen_e || seen_point, nondigit);
            }
        }
    }

    private int getNumberToken(String number, boolean isFloat, char nondigit) {
        if (nondigit != '\0') {
            throw new SyntaxException(getPosition(), "Trailing '_' in number.");
        }
        if (isFloat) {
            double d;
            try {
                d = Double.parseDouble(number);
            } catch (NumberFormatException e) {
                warnings.warn(getPosition(), "Float " + number + " out of range.");
                
                d = number.startsWith("-") ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            }
            yaccValue = new FloatNode(getPosition(), d);
            return Tokens.tFLOAT;
        }
		yaccValue = getInteger(number, 10);
		return Tokens.tINTEGER;
    }
}
