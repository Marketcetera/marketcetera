package org.rubypeople.rdt.internal.ui.text.comment;

import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.corext.util.CodeFormatterUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.WorkingCopyManager;
import org.rubypeople.rdt.internal.ui.text.IRubyPartitions;

public class RubyCommentAutoIndentStrategy extends
		DefaultIndentLineAutoEditStrategy {

	private String fPartitioning;
	private ITextEditor fEditor;
	private WorkingCopyManager fManager;
	private IRubyProject fProject;

	public RubyCommentAutoIndentStrategy(ITextEditor textEditor, String partitioning, IRubyProject project) {
		fPartitioning = partitioning;
		fEditor = textEditor;
		fManager = RubyPlugin.getDefault().getWorkingCopyManager();
		fProject = project;
	}

	public void customizeDocumentCommand(IDocument document,
			DocumentCommand command) {
		if (command.text != null) {
			if (command.length == 0) {
				String[] lineDelimiters = document.getLegalLineDelimiters();
				int index = TextUtilities
						.endsWith(lineDelimiters, command.text);
				if (index > -1) {
					// ends with line delimiter
					if (lineDelimiters[index].equals(command.text))
						// just the line delimiter
						indentAfterNewLine(document, command);
					return;
				}
			}
		}
	}

	/**
	 * Copies the indentation of the previous line and adds a #.
	 * 
	 * @param d
	 *            the document to work on
	 * @param c
	 *            the command to deal with
	 */
	private void indentAfterNewLine(IDocument d, DocumentCommand c) {
		if (fPartitioning.equals(IRubyPartitions.RUBY_SINGLE_LINE_COMMENT)) {
			doSingleLineComment(d, c);
		} else {
			doMultiLineComment(d, c);
		}
	}

	private void doMultiLineComment(IDocument d, DocumentCommand c) {
		
		int offset = c.offset;
		if (offset == -1 || d.getLength() == 0)
			return;

		try {
			int p = (offset == d.getLength() ? offset - 1 : offset);
			try {
				new RubyParser().parse(d.get());
				return;
			} catch(SyntaxException se) {
				if (!se.getMessage().equals("embedded document meets end of file")) {
					return;
				}
			}
				
			int lineNumber = d.getLineOfOffset(p);
			IRegion line = d.getLineInformation(lineNumber);
			String aLine = getLine(d, lineNumber - 1);
			StringBuffer buf = new StringBuffer(c.text);
			if (aLine.trim().equals("=begin")) {
				// add =end
				buf.append(CodeFormatterUtil.createIndentString(1, fProject));
				c.caretOffset= c.offset + buf.length();
				c.shiftsCaret= false;
				buf.append(TextUtilities.getDefaultLineDelimiter(d));
				buf.append("=end");
			}			
					
			c.text = buf.toString();

		} catch (BadLocationException excp) {
			// stop work
		}
	}

	private void doSingleLineComment(IDocument d, DocumentCommand c) {
		int offset = c.offset;
		if (offset == -1 || d.getLength() == 0)
			return;

		try {
			int p = (offset == d.getLength() ? offset - 1 : offset);
				
			int lineNumber = d.getLineOfOffset(p);
			IRegion line = d.getLineInformation(lineNumber);
			if (lineNumber != 0) { // If first line, extend the comment
				String nextLine = getLine(d, lineNumber + 1); // otherwise check next line
				if (!(isComment(nextLine) || isClassDefinition(nextLine)
						|| isMethodDeclaration(nextLine)
						|| isAttributeCall(nextLine) || isAliasCall(nextLine)
						|| isModuleDeclaration(nextLine) || isConstantAssignment(nextLine))) { // if next line is commonly documented element, continue comment
					String previousLine = getLine(d, lineNumber - 1);
					if (!isComment(previousLine)) // last, if the previous line was a comment (so two lines in a row now), extend comments
						return;
				}
			}
			int lineOffset = line.getOffset();
			int firstNonWS = findEndOfWhiteSpace(d, lineOffset, offset);
			Assert.isTrue(firstNonWS >= lineOffset,
					"indentation must not be negative"); //$NON-NLS-1$

			StringBuffer buf = new StringBuffer(c.text);
			IRegion prefix = findPrefixRange(d, line);
			String indentation = d.get(prefix.getOffset(), prefix.getLength());
			int lengthToAdd = Math.min(offset - prefix.getOffset(), prefix
					.getLength());

			buf.append(indentation.substring(0, lengthToAdd));
			
			String src = getRDoc(d, c.text, lineNumber + 1);
			if (src != null) buf.append(src);
			// move the caret behind the prefix, even if we do not have to
			// insert it.
			if (lengthToAdd < prefix.getLength())
				c.caretOffset = offset + prefix.getLength() - lengthToAdd;
			c.text = buf.toString();

		} catch (BadLocationException excp) {
			// stop work
		}
	}

	private String getRDoc(IDocument d, String newLine, int line) {
		IRubyScript script = fManager.getWorkingCopy(fEditor.getEditorInput());
		int pos;
		try {
			IRegion region = d.getLineInformation(line);
			pos = findEndOfWhiteSpace(d, region.getOffset(), region.getOffset() + region.getLength());
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		IRubyElement element = null;
		try {
			element = script.getElementAt(pos);

			if (element == null)
				return null;
			StringBuffer buffer = new StringBuffer();
			if (element instanceof IMethod) {
				IMethod method = (IMethod) element;
				String[] names = method.getParameterNames();
				for (int i = 0; i < names.length; i++) {
					String name = names[i];
					int end = name.indexOf(' ');
					if (end != -1) {
						name = name.substring(0, end);
					}
					buffer.append("+");
					buffer.append(name);
					buffer.append("+");
					buffer.append(newLine);
				}
			}
			return buffer.toString();
		} catch (RubyModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}		
	}

	private boolean isComment(String nextLineText) {
		return nextLineText.matches("^\\s*#.*");
	}

	private boolean isClassDefinition(String nextLineText) {
		return nextLineText.matches("^\\s*class\\s+.+\\s*");
	}

	private boolean isAliasCall(String nextLineText) {
		return nextLineText.matches("^\\s*alias\\s+.+\\s*");
	}

	private boolean isModuleDeclaration(String nextLineText) {
		return nextLineText.matches("^\\s*module\\s+.+\\s*");
	}

	private boolean isMethodDeclaration(String nextLineText) {
		return nextLineText.matches("^\\s*def\\s+.+\\s*");
	}

	private boolean isAttributeCall(String nextLineText) {
		return nextLineText.matches("^\\s*attr.+\\s*");
	}

	private boolean isConstantAssignment(String nextLineText) {
		return nextLineText.matches("^\\s*[A-Z_]+\\s?=\\s+.+\\s*");
	}

	private String getLine(IDocument d, int lineNum) throws BadLocationException {
		IRegion nextLineRegion = d.getLineInformation(lineNum + 1);
		return d.get(nextLineRegion.getOffset(), nextLineRegion.getLength());
	}

	/**
	 * Returns the range of the comment prefix on the given line in
	 * <code>document</code>. The prefix greedily matches the following regex
	 * pattern: <code>\w*#\w*</code>, that is, any number of whitespace
	 * characters, followed by an pound symbol ('#'), followed by any number of
	 * whitespace characters.
	 * 
	 * @param document
	 *            the document to which <code>line</code> refers
	 * @param line
	 *            the line from which to extract the prefix range
	 * @return an <code>IRegion</code> describing the range of the prefix on
	 *         the given line
	 * @throws BadLocationException
	 *             if accessing the document fails
	 */
	private IRegion findPrefixRange(IDocument document, IRegion line)
			throws BadLocationException {
		int lineOffset = line.getOffset();
		int lineEnd = lineOffset + line.getLength();
		int indentEnd = findEndOfWhiteSpace(document, lineOffset, lineEnd);
		if (indentEnd < lineEnd && document.getChar(indentEnd) == '#') {
			indentEnd++;
			while (indentEnd < lineEnd && document.getChar(indentEnd) == ' ')
				indentEnd++;
		}
		return new Region(lineOffset, indentEnd - lineOffset);
	}

}
