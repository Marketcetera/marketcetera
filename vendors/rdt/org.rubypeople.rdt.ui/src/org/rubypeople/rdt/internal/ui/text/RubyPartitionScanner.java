package org.rubypeople.rdt.internal.ui.text;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.jruby.ast.CommentNode;
import org.jruby.ast.Node;
import org.jruby.common.NullWarnings;
import org.jruby.lexer.yacc.LexerSource;
import org.jruby.lexer.yacc.RubyYaccLexer;
import org.jruby.lexer.yacc.SyntaxException;
import org.jruby.lexer.yacc.RubyYaccLexer.LexState;
import org.jruby.parser.ParserConfiguration;
import org.jruby.parser.ParserSupport;
import org.jruby.parser.RubyParserResult;
import org.jruby.parser.Tokens;
import org.rubypeople.rdt.internal.core.util.ASTUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

public class RubyPartitionScanner implements IPartitionTokenScanner {
		
	private static final String BEGIN = "=begin";

	private static class QueuedToken {
		private IToken token;
		private int length;
		private int offset;

		QueuedToken(IToken token, int offset, int length) {
			this.token = token;
			this.length = length;
			this.offset = offset;
		}
		
		public int getLength() {
			return length;
		}
		
		public int getOffset() {
			return offset;
		}
		
		public IToken getToken() {
			return token;
		}
		
		@Override
		public String toString() {
			return getToken().getData() + ": offset: " + getOffset() + ", length: " + getLength();
		}
	}
	
	private RubyYaccLexer lexer;
	private ParserSupport parserSupport;
	private RubyParserResult result;
	private String fContents;
	private LexerSource lexerSource;
	private int origOffset;
	private int origLength;
	private int fLength;
	private int fOffset;
	
	private List<QueuedToken> fQueue = new ArrayList<QueuedToken>();
	private String fContentType = RUBY_DEFAULT;
	private boolean inSingleQuote;
	private String fOpeningString;
	

	public final static String RUBY_MULTI_LINE_COMMENT = IRubyPartitions.RUBY_MULTI_LINE_COMMENT;
	public final static String RUBY_SINGLE_LINE_COMMENT = IRubyPartitions.RUBY_SINGLE_LINE_COMMENT;
	public final static String RUBY_STRING = IRubyPartitions.RUBY_STRING;
	public final static String RUBY_REGULAR_EXPRESSION = IRubyPartitions.RUBY_REGULAR_EXPRESSION;
	public static final String RUBY_DEFAULT = IRubyPartitions.RUBY_DEFAULT;
	public static final String RUBY_COMMAND = IRubyPartitions.RUBY_COMMAND;
	
	public static final String[] LEGAL_CONTENT_TYPES = {
			RUBY_DEFAULT, RUBY_MULTI_LINE_COMMENT, RUBY_SINGLE_LINE_COMMENT, RUBY_REGULAR_EXPRESSION, RUBY_STRING, RUBY_COMMAND
			};

	public RubyPartitionScanner() {
		lexer = new RubyYaccLexer();
		parserSupport = new ParserSupport();
		ParserConfiguration config = new ParserConfiguration(0, false);
		config.setExtraPositionInformation(true);
		parserSupport.setConfiguration(config);
		result = new RubyParserResult();
		parserSupport.setResult(result);
		lexer.setParserSupport(parserSupport);
		lexer.setWarnings(new NullWarnings());
	}

	public void setPartialRange(IDocument document, int offset, int length,
			String contentType, int partitionOffset) {
		reset();
		int myOffset = offset;
		if (contentType != null) {
			int diff = offset - partitionOffset;
			myOffset = partitionOffset; // backtrack to beginning of partition so we don't get in weird state
			length += diff;
		}
		if (myOffset == -1) myOffset = 0;
		ParserConfiguration config = new ParserConfiguration(0, true, false);
		try {			
			fContents = document.get(myOffset, length);			
			lexerSource = LexerSource.getSource("filename", new StringReader(fContents), null, config);
			lexer.setSource(lexerSource);
		} catch (BadLocationException e) {
			lexerSource = LexerSource.getSource("filename", new StringReader(""), null, config);
			lexer.setSource(lexerSource);
		}
		origOffset = myOffset;
		origLength = length;
	}

	private void reset() {
		lexer.reset();
		lexer.setState(LexState.EXPR_BEG);
		parserSupport.initTopLocalVariables();
		fQueue.clear();
		inSingleQuote = false;
	}

	public int getTokenLength() {
		return fLength;
	}

	public int getTokenOffset() {
		return fOffset;
	}

	public IToken nextToken() {
		if (!fQueue.isEmpty()) {
			return popTokenOffQueue();
		}
		fOffset = getOffset();
		fLength = 0;
		IToken returnValue = new Token(RUBY_DEFAULT);
		boolean isEOF = false;
		try {
			isEOF = !lexer.advance();			
			if (isEOF) {
				returnValue = Token.EOF;
			} else {
				int lexerToken = lexer.token();
				if (!inSingleQuote && lexerToken == Tokens.tSTRING_DVAR) { // we hit a single dynamic variable
					addPoundToken();
					scanDynamicVariable();
					setLexerPastDynamicSectionOfString();
					return popTokenOffQueue();
				} else if (!inSingleQuote && lexerToken == Tokens.tSTRING_DBEG) { // if we hit dynamic code inside a string
					addPoundBraceToken();
					scanTokensInsideDynamicPortion();			
					addClosingBraceToken();
					setLexerPastDynamicSectionOfString();
					return popTokenOffQueue();
				} else if (lexerToken == Tokens.tSTRING_BEG) {			
					String opening = getOpeningString();
					int index = indexOf(opening, ", +");
					if (opening.trim().startsWith("<<") && index != -1) {
						addHereDocStartToken(index);
						addCommaToken(index); 
						scanRestOfLine(opening, index);
						// set up state variables
						fOpeningString = opening.substring(0, index).trim() + "\n";
						fContentType = RUBY_STRING;
						return popTokenOffQueue();
					}
				}
				returnValue = getToken(lexerToken);				
			}
			List comments = result.getCommentNodes();
			if (comments != null && !comments.isEmpty()) {
				parseOutComments(comments);
				addQueuedToken(returnValue, isEOF); // Queue the normal token we just ate up
				comments.clear();
				return popTokenOffQueue();
			}
		} catch (SyntaxException se) {
			if (se.getMessage().equals("embedded document meets end of file")) {
				// Add to the queue (at end), then try to just do the rest of the file...
				// TODO recover somehow by removing this chunk out of the fContents?
				int start = se.getPosition().getStartOffset();
				int length = fContents.length() - start;
				QueuedToken qtoken = new QueuedToken(new Token(RUBY_MULTI_LINE_COMMENT), start + origOffset, length);
				if (fOffset == origOffset) { // If we never got to read in beginning contents
					RubyPartitionScanner scanner = new RubyPartitionScanner();
					String possible = fContents.substring(0, start);
					IDocument document = new Document(possible);
					scanner.setRange(document, origOffset, possible.length());
					IToken token;
					while (!(token = scanner.nextToken()).isEOF()) {
						push(new QueuedToken(token, scanner.getTokenOffset() + fOffset, scanner.getTokenLength()));
					}
				}
				push(qtoken);
				push(new QueuedToken(Token.EOF, start + origOffset + length, 0));
				return popTokenOffQueue();
			} else if (se.getMessage().equals("unterminated string meets end of file")) {
				// Add to the queue (at end), then try to just do the rest of the file...
				// TODO recover somehow by removing this chunk out of the fContents?
				int start = se.getPosition().getStartOffset();
				int length = fContents.length() - start;
				QueuedToken qtoken = new QueuedToken(new Token(fContentType), start + origOffset, length);
				if (fOffset == origOffset) { // If we never got to read in beginning contents
					RubyPartitionScanner scanner = new RubyPartitionScanner();
					String possible = fContents.substring(0, start);
					IDocument document = new Document(possible);
					scanner.setRange(document, origOffset, possible.length());
					IToken token;
					while (!(token = scanner.nextToken()).isEOF()) {
						push(new QueuedToken(token, scanner.getTokenOffset() + fOffset, scanner.getTokenLength()));
					}
				}
				push(qtoken);
				push(new QueuedToken(Token.EOF, start + origOffset + length, 0));
				return popTokenOffQueue();
			}
			
			if (lexerSource.getOffset() - origLength == 0)
				return Token.EOF; // return eof if we hit a problem found at
									// end of parsing
			else
				fLength = getOffset() - fOffset;
			Assert.isTrue(fLength >= 0);
			return new Token(RUBY_DEFAULT);
		} catch (IOException e) {
			RubyPlugin.log(e);
		}
		if (!isEOF) {
			fLength = getOffset() - fOffset;
			Assert.isTrue(fLength >= 0);
		}
		return returnValue;
	}

	private int indexOf(String opening, String string) {
		String trimmed = opening.trim();
		int diff;
		if (trimmed.length() == 0) {
			diff = opening.length();
		} else {
		    diff = opening.indexOf(trimmed.charAt(0)); // Count leading whitespace
		}
		int lowest = -1;
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			int value = trimmed.indexOf(c);
			if (value == -1) continue;
			value +=  diff;
			if (lowest == -1) {
				lowest = value;
				continue;
			}			
			if (value < lowest) lowest = value;
		}
		return lowest;
	}

	private void scanRestOfLine(String opening, int index) {
		String possible = opening.substring(index + 1);
		RubyPartitionScanner scanner = new RubyPartitionScanner();
		IDocument document = new Document(possible);
		scanner.setRange(document, 0, possible.length());
		IToken token;
		while (!(token = scanner.nextToken()).isEOF()) {
			push(new QueuedToken(token, scanner.getTokenOffset() + fOffset + index + 1, scanner.getTokenLength()));
		}
		setOffset(fOffset + index + 1 + possible.length());
	}

	private void addCommaToken(int index) {
		push(new QueuedToken(new Token(RUBY_DEFAULT), fOffset + index, 1));
	}

	private void addHereDocStartToken(int index) {
		push(new QueuedToken(new Token(RUBY_STRING), fOffset, index));
	}

	private void setOffset(int offset) {
		fOffset = offset;
	}

	private void addPoundToken() {
		addStringToken(1);// add token for the #
	}

	private void scanDynamicVariable() {
		int whitespace = fContents.indexOf(' ', fOffset - origOffset); // read until whitespace or '"'
		if (whitespace == -1) whitespace = Integer.MAX_VALUE;
		int doubleQuote = fContents.indexOf('"', fOffset - origOffset);
		if (doubleQuote == -1) doubleQuote = Integer.MAX_VALUE;
		int end = Math.min(whitespace, doubleQuote);
		// FIXME If we can't find whitespace or doubleQuote, we are pretty screwed.
		String possible = null;
		if (end == -1) {
			possible = fContents.substring(fOffset - origOffset);
		} else {
			possible = fContents.substring(fOffset - origOffset, end);
		}
		RubyPartitionScanner scanner = new RubyPartitionScanner();
		IDocument document = new Document(possible);
		scanner.setRange(document, 0, possible.length());
		IToken token;
		while (!(token = scanner.nextToken()).isEOF()) {
			push(new QueuedToken(token, scanner.getTokenOffset() + (fOffset), scanner.getTokenLength()));
		}
		setOffset(fOffset + possible.length());
	}

	private void scanTokensInsideDynamicPortion() {
		String possible = new String(fContents.substring(fOffset - origOffset));		
		int end = findEnd(possible);
		if (end != -1) {
			possible = possible.substring(0, end); 
		} else {
			possible = possible.substring(0);
		}
		RubyPartitionScanner scanner = new RubyPartitionScanner();
		IDocument document = new Document(possible);
		scanner.setRange(document, 0, possible.length());
		IToken token;
		while (!(token = scanner.nextToken()).isEOF()) {
			push(new QueuedToken(token, scanner.getTokenOffset() + fOffset, scanner.getTokenLength()));
		}
		setOffset(fOffset + possible.length());
	}

	private int findEnd(String possible) {
		return new EndBraceFinder(possible).find();
	}

	private void addPoundBraceToken() {
		addStringToken(2); // add token for the #{
	}
	
	private void addStringToken(int length) {
		push(new QueuedToken(new Token(fContentType), fOffset, length));
		setOffset(fOffset + length); // move past token
	}

	private void addClosingBraceToken() {
		addStringToken(1);
	}

	private void setLexerPastDynamicSectionOfString() throws IOException {
		IDocument document;
		StringBuffer fakeContents = new StringBuffer();
		int start = fOffset - fOpeningString.length();
		for (int i = 0; i < start; i++) {
			fakeContents.append(" ");
		}
		fakeContents.append(fOpeningString);
		if ((fOffset - origOffset) < origLength) {
			fakeContents.append(new String(fContents.substring((fOffset - origOffset)))); // BLAH removed + 1 from end here
		}
		document = new Document(fakeContents.toString());
		List<QueuedToken> queueCopy = new ArrayList<QueuedToken>(fQueue);
		setPartialRange(document, start, fakeContents.length() - start, null, start);
		fQueue = new ArrayList<QueuedToken>(queueCopy);
		lexer.advance();
	}

	private void parseOutComments(List comments) {
		for (Iterator iter = comments.iterator(); iter.hasNext();) {
			CommentNode comment = (CommentNode) iter.next();
			int offset = correctOffset(comment);
			int length = comment.getContent().length();
			if (isCommentMultiLine(comment)) {
				length = (origOffset + comment.getPosition().getEndOffset()) - offset;
				if (comment.getContent().charAt(0) != '=') {
					length++;
				}
			}
			Token token = new Token(getContentType(comment));
			push(new QueuedToken(token, offset, length));
		}
	}

	private IToken popTokenOffQueue() {
		QueuedToken token = fQueue.remove(0);
		setOffset(token.getOffset());
		Assert.isTrue(token.getLength() >= 0);
		fLength = token.getLength();
		return token.getToken();
	}

	private IToken getToken(int i) {
		// If we hit a 32 (space) inside a qword, just return string content type (not default)
		// FIXME IF we're in qwords, we should inspect the contents because it may be a variable
		if (i == 32) {
			return new Token(fContentType);
		}
		switch (i) {
		case Tokens.tSTRING_CONTENT:
			return new Token(fContentType);
		case Tokens.tSTRING_BEG:
			fOpeningString = getOpeningString();
			if (fOpeningString.equals("'") || fOpeningString.startsWith("%q")) {
				inSingleQuote = true;
			} else if (fOpeningString.startsWith("<<")) { // here-doc
				fOpeningString += "\n";
			}
			fContentType = RUBY_STRING;			
			return new Token(RUBY_STRING);
		case Tokens.tXSTRING_BEG:
			fOpeningString = getOpeningString();
			fContentType = RUBY_COMMAND;
			return new Token(RUBY_COMMAND);
		case Tokens.tQWORDS_BEG:
			fOpeningString = getOpeningString();
			fContentType = RUBY_STRING;
			return new Token(RUBY_STRING);
		case Tokens.tSTRING_END:
			String oldContentType = fContentType;
			fContentType = RUBY_DEFAULT;
			inSingleQuote = false;
			return new Token(oldContentType);
		case Tokens.tREGEXP_BEG:
			fOpeningString = getOpeningString();
			fContentType = RUBY_REGULAR_EXPRESSION;
			return new Token(RUBY_REGULAR_EXPRESSION);
		case Tokens.tREGEXP_END:
			fContentType = RUBY_DEFAULT;
			return new Token(RUBY_REGULAR_EXPRESSION);
		default:
			return new Token(RUBY_DEFAULT);
		}
	}

	private String getOpeningString() {
		int start = fOffset - origOffset;
		List comments = result.getCommentNodes();
		if (comments != null && !comments.isEmpty()) {
		  Node comment = (Node) comments.get(comments.size() - 1);
		  int end = comment.getPosition().getEndOffset();
		  start = end;
		}			
		return fContents.substring(start, lexerSource.getOffset()).trim();
	}

	/**
	 * correct start offset, since when a line with nothing but spaces on it appears before comment, 
	 * we get messed up positions
	 */
	private int correctOffset(CommentNode comment) {
		return origOffset + comment.getPosition().getStartOffset();
	}

	private boolean isCommentMultiLine(CommentNode comment) {
		String src = ASTUtil.getSource(fContents, comment);
		if (src != null && src.startsWith(BEGIN)) return true;
		return false;
	}

	private String getContentType(CommentNode comment) {
		if (isCommentMultiLine(comment)) return RUBY_MULTI_LINE_COMMENT;
		return RUBY_SINGLE_LINE_COMMENT;
	}

	private void addQueuedToken(IToken returnValue, boolean isEOF) {
		// grab end of last comment (last thing in queue)
		QueuedToken token = peek();
		setOffset(token.getOffset() + token.getLength());
		int length = getOffset() - fOffset;
		if (length < 0 ) {
			length = 0;
		}
		push(new QueuedToken(returnValue, fOffset, length));
	}
	
	private QueuedToken peek() {
		return fQueue.get(fQueue.size() - 1);
	}

	private void push(QueuedToken token) {
		Assert.isTrue(token.getLength() >= 0);
		fQueue.add(token);
	}

	private int getOffset() {
		return lexerSource.getOffset() + origOffset;
	}

	public void setRange(IDocument document, int offset, int length) {
		setPartialRange(document, offset, length, null, -1);
	}

	public static class EndBraceFinder {
		private String input;
		private List<String> stack;
		
		public EndBraceFinder(String possible) {
			this.input = possible;
			stack = new ArrayList<String>();
		}
		
		public int find() {
			for (int i = 0; i < input.length(); i++) {
				char c = input.charAt(i);
				switch (c) {
				case '\\':
				case '$':
					// skip next character
					i++;
					break;
				case '"':
					if (topEquals("\"")) {
						pop();
					} else {
						push("\"");
					}
					break;
				case '\'':
					if (topEquals("'")) {
						pop();
					} else if (!topEquals("\"")){
						push("'");
					}
					break;
				case '{':
					// Only if we're not inside a string
					if (!topEquals("'") && !topEquals("\"")) {
						push("{");
					}
					break;
				case '#':
					// Only add if we're inside a double quote string
					if (topEquals("\"")) {
						c = input.charAt(i + 1);
						if (c == '{')
							push("#{");
					}					
					break;
				case '}':
					if (stack.isEmpty()) { // if not in open state
						return i;
					}
					if (topEquals("#{") || topEquals("{")) {
						pop();
					} 
					break;
				default:
					break;
				}
			}		
			return -1;
		}

		private boolean topEquals(String string) {
			String open = peek();
			return open != null && open.equals(string);
		}

		private boolean push(String string) {
			return stack.add(string);
		}

		private String pop() {
			return stack.remove(stack.size() - 1);
		}

		private String peek() {
			if (stack.isEmpty())
				return null;				
			return stack.get(stack.size() - 1);
		}
	}
}