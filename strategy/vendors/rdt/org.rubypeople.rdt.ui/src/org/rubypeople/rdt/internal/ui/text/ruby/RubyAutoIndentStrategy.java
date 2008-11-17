package org.rubypeople.rdt.internal.ui.text.ruby;

import java.util.regex.Pattern;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.formatter.DefaultCodeFormatterConstants;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.corext.util.CodeFormatterUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.text.RubyHeuristicScanner;
import org.rubypeople.rdt.internal.ui.text.RubyIndenter;
import org.rubypeople.rdt.ui.PreferenceConstants;

public class RubyAutoIndentStrategy extends DefaultIndentLineAutoEditStrategy implements IPropertyChangeListener {

	/** Preference key for automatically 'end'ing statements */
	private final static String END_STATEMENTS= PreferenceConstants.EDITOR_END_STATEMENTS;
	
	private final Pattern openBlockPattern = Pattern.compile(".*[\\S].*do[\\w|\\s]*");
	
	private static final String BLOCK_CLOSER = "end";
	private String fPartitioning;
	private final IRubyProject fProject;
	private boolean endStatements;
	private IPreferenceStore fPreferenceStore;

	/**
	 * Creates a new Ruby auto indent strategy for the given document partitioning.
	 *
	 * @param partitioning the document partitioning
	 * @param project the project to get formatting preferences from, or null to use default preferences
	 */
	public RubyAutoIndentStrategy(String partitioning, IRubyProject project) {
		fPartitioning= partitioning;
		fProject= project;
		fPreferenceStore = RubyPlugin.getDefault().getPreferenceStore();
		endStatements= fPreferenceStore.getBoolean(END_STATEMENTS);
		fPreferenceStore.addPropertyChangeListener(this);
 	}
	
	/*
	 * @see org.eclipse.jface.text.IAutoIndentStrategy#customizeDocumentCommand(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.DocumentCommand)
	 */
	public void customizeDocumentCommand(IDocument d, DocumentCommand c) {
		if (c.doit == false)
			return;
		if (c.length == 0 && c.text != null && isLineDelimiter(d, c.text))
			smartIndentAfterNewLine(d, c);
	}
	
    private boolean isLineDelimiter(IDocument document, String text) {
		String[] delimiters= document.getLegalLineDelimiters();
		if (delimiters != null)
			return TextUtilities.equals(delimiters, text) > -1;
		return false;
	}
    
	private void smartIndentAfterNewLine(IDocument d, DocumentCommand c) {
		RubyHeuristicScanner scanner= new RubyHeuristicScanner(d);
		RubyIndenter indenter= new RubyIndenter(d, scanner, fProject);
		StringBuffer indent= indenter.computeIndentation(c.offset);
		if (indent == null)
			indent= new StringBuffer(); 

		int docLength= d.getLength();
		if (c.offset == -1 || docLength == 0)
			return;

		try {
			int p= (c.offset == docLength ? c.offset - 1 : c.offset);
			int line= d.getLineOfOffset(p);

			StringBuffer buf= new StringBuffer(c.text + indent);


			IRegion currentLineRegion= d.getLineInformation(line);
			int lineEnd= currentLineRegion.getOffset() + currentLineRegion.getLength();

			int contentStart= findEndOfWhiteSpace(d, c.offset, lineEnd);
			c.length=  Math.max(contentStart - c.offset, 0);

			int startOfCurrentLine= currentLineRegion.getOffset();
			
			String trimmed = getTrimmedLine(d, startOfCurrentLine, c.offset);
			if (mightHaveToShiftCurrentLine(trimmed)) {// check to see if we need to fix the indentation of this line
				IRegion previousLineRegion = d.getLineInformation(line - 1);
				String previousLine = getTrimmedLine(d, previousLineRegion.getOffset(), previousLineRegion.getOffset() + previousLineRegion.getLength());				
				String previousIndent= indenter.computeIndentation(previousLineRegion.getOffset()).toString();
				
				// FIXME This all assumes spaces!				
				String unindented = "";
				if (middleOfBlockRightAfterBeginning(trimmed, previousLine) ) {
					unindented = previousIndent;
					if (whenAfterCase(trimmed, previousLine)) {
						// add extra indent, dpending upon code formatting option
						if (RubyCore.getPlugin().getPluginPreferences().getBoolean(DefaultCodeFormatterConstants.FORMATTER_INDENT_CASE_BODY)) {
							unindented += CodeFormatterUtil.createIndentString(1, fProject);
						}
					}
				} else {
					int length = previousIndent.length() - CodeFormatterUtil.createIndentString(1, fProject).length();
					int nextCalculated = nextMeaningfulIndentLength(d, indenter, line);
					int unit = CodeFormatterUtil.createIndentString(1, fProject).length();
					if (nextCalculated != -1 && (length > (nextCalculated + unit))) {
						unindented = previousIndent.substring(0, length - unit);
					} else if (length <= 0) {
						unindented = "";
					} else {
						unindented = previousIndent.substring(0, length);
					}
				}				// FIXME Deindenting 'end' of case that has indented 'when's comes out incorrectly
				if (unindented.length() < indent.length()) { // if calculated indent length is less than indent we currently have queued up...
					d.replace(startOfCurrentLine, c.offset - startOfCurrentLine, unindented + trimmed); // fix indent of this line
					int shift = indent.length() - unindented.length();
					c.offset = c.offset - shift; // change where we're adding the newline
					buf.delete(buf.length() - shift, buf.length()); // remove additional indent from being inserted
				}
			}
//			 If we're hitting return at the end of the line of a new block, add indent
			if (atIndentPoint(trimmed)) {
				buf.append(CodeFormatterUtil.createIndentString(1, fProject));
				c.caretOffset= c.offset + buf.length();
				c.shiftsCaret= false;
			}
			if (trimmed.equals("=begin")) { // TODO If doesn't start at beginnig of line, move to first column
				buf.append(CodeFormatterUtil.createIndentString(1, fProject));
				c.caretOffset= c.offset + buf.length();
				c.shiftsCaret= false;
				buf.append(TextUtilities.getDefaultLineDelimiter(d));
				buf.append("=end");
			}
			// insert closing "end" on new line after an unclosed block
			if (closeBlock() && unclosedBlock(d, trimmed, c.offset)) {			
				// copy old content of line behind insertion point to new line
//				if (c.offset == 0) {
					if (lineEnd - contentStart > 0) {
						c.length=  lineEnd - c.offset;
						buf.append(d.get(contentStart, lineEnd - contentStart).toCharArray());
					}
//				}
				
				buf.append(TextUtilities.getDefaultLineDelimiter(d));
				buf.append(indent);
				buf.append(BLOCK_CLOSER);
			}
			c.text= buf.toString();

		} catch (BadLocationException e) {
			RubyPlugin.log(e);
		}
	}

	private int nextMeaningfulIndentLength(IDocument d, RubyIndenter indenter, int line) throws BadLocationException {
		for (int i = line + 1; i < d.getNumberOfLines(); i++) {
			IRegion nextLineRegion = d.getLineInformation(i);
			String trimmed = getTrimmedLine(d, nextLineRegion.getOffset(), nextLineRegion.getOffset() + nextLineRegion.getLength());
			if (trimmed == null || trimmed.length() == 0) continue;
			String nextIndent= indenter.computeIndentation(nextLineRegion.getOffset()).toString();
			return nextIndent.length();
		}
		return -1;
	}

	private boolean middleOfBlockRightAfterBeginning(String trimmed, String previousLine) {
		return middleOfIfRightAfterBeginning(trimmed, previousLine) || middleOfBeginRightAfterBeginning(trimmed, previousLine) || elseRightAfterElsif(trimmed, previousLine)
		|| ensureRightAfterRescue(trimmed, previousLine) || whenAfterCase(trimmed, previousLine) || elsifRightAfterElsif(trimmed, previousLine);
	}

	private boolean middleOfBeginRightAfterBeginning(String trimmed, String previousLine) {
		return previousLine.equals("begin") && (trimmed.startsWith("rescue") || trimmed.equals("ensure") || trimmed.equals("rescue"));
	}

	private boolean middleOfIfRightAfterBeginning(String trimmed, String previousLine) {
		return previousLine.startsWith("if ") && (trimmed.startsWith("elsif") || trimmed.equals("else"));
	}
	
	private boolean ensureRightAfterRescue(String trimmed, String previousLine) {
		return (previousLine.startsWith("rescue ") || previousLine.equals("rescue")) && trimmed.equals("ensure");
	}
	
	private boolean elseRightAfterElsif(String trimmed, String previousLine) {
		return previousLine.startsWith("elsif ") && trimmed.equals("else");
	}
	
	private boolean elsifRightAfterElsif(String trimmed, String previousLine) {
		return previousLine.startsWith("elsif ") && trimmed.startsWith("elsif ");
	}
	
	private boolean whenAfterCase(String trimmed, String previousLine) {
		return previousLine.startsWith("case ") && trimmed.startsWith("when ");
	}

	private boolean atIndentPoint(String trimmed) {
		if (trimmed == null || trimmed.length() == 0) return false;
		return atStartOfBlock(trimmed) || isMiddleOfBlockKeyword(trimmed);
	}

	private boolean isMiddleOfBlockKeyword(String trimmed) {
		if (trimmed == null || trimmed.length() == 0) return false;
		return trimmed.equals("rescue") || trimmed.equals("else") || trimmed.equals("ensure") 
			|| trimmed.startsWith("elsif ") || trimmed.startsWith("rescue ") || trimmed.startsWith("when ");
	}
	
	private boolean mightHaveToShiftCurrentLine(String trimmed) {
		if (trimmed == null || trimmed.length() == 0) return false;
		return isMiddleOfBlockKeyword(trimmed) || trimmed.equals(BLOCK_CLOSER);
	}

	private boolean unclosedBlock(IDocument d, String trimmed, int offset) {
		// FIXME wow is this ugly! There has to be an easier way to tell if there's an unclosed block besides parsing and catching a syntaxError!
		if (!atStartOfBlock(trimmed)) {
			return false;
		}
		
		RubyParser parser = new RubyParser();
		try {
			parser.parse(d.get());
		} catch (SyntaxException e) {
			String msg = e.getMessage();
			if (msg.contains("expecting") && (msg.contains("kEND") || msg.contains("kTHEN")))
				return true;
			try {
				StringBuffer buffer = new StringBuffer(d.get());
				buffer.insert(offset, TextUtilities.getDefaultLineDelimiter(d) + BLOCK_CLOSER);
				parser.parse(buffer.toString());
			} catch (SyntaxException syntaxException) {
				return false;
			}
			return true;
		}
		return false;
	}

	private String getTrimmedLine(IDocument d, int start, int offset) throws BadLocationException {
		String line = d.get(start, offset - start);
		return line.trim();
	}

	private boolean atStartOfBlock(String line) {
		return line.startsWith("class ") || line.startsWith("if ") || line.startsWith("module ") 
		  || line.startsWith("unless ") || line.startsWith("def ") || line.equals("begin") || line.startsWith("case ") 
		  || line.startsWith("for ") || openBlockPattern.matcher(line).matches();
	}

	private boolean closeBlock() {
		return endStatements;
	}

	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();
		if (END_STATEMENTS.equals(property)) {
			endStatements = fPreferenceStore.getBoolean(property);
			return;
		}         
	}
	
}
