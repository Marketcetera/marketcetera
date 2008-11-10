package org.rubypeople.rdt.internal.ui.text;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.rubypeople.rdt.core.IRubyProject;

public class RubyIndenter {

	private IDocument fDocument;
	private IRubyProject fProject;
	private RubyHeuristicScanner fScanner;

	public RubyIndenter(IDocument d, RubyHeuristicScanner scanner, IRubyProject project) {
		fDocument = d;
		fScanner = scanner;
		fProject = project;
	}

	public StringBuffer computeIndentation(int offset) {
		StringBuffer buf = getLeadingWhitespace(offset);
		// TODO Convert the whitespaces into units?
		return buf;
//		return CodeFormatterUtil.createIndentString(indentationUnits, fProject);
	}
	
	/**
	 * Returns the indentation of the line at <code>offset</code> as a
	 * <code>StringBuffer</code>. If the offset is not valid, the empty string
	 * is returned.
	 *
	 * @param offset the offset in the document
	 * @return the indentation (leading whitespace) of the line in which
	 * 		   <code>offset</code> is located
	 */
	private StringBuffer getLeadingWhitespace(int offset) {
		StringBuffer indent= new StringBuffer();
		try {
			IRegion line= fDocument.getLineInformationOfOffset(offset);
			int lineOffset= line.getOffset();
			int nonWS= fScanner.findNonWhitespaceForwardInAnyPartition(lineOffset, lineOffset + line.getLength());
			if (nonWS == -1) {
				indent.append(fDocument.get(lineOffset, line.getLength()));
				return indent;
			}
			indent.append(fDocument.get(lineOffset, nonWS - lineOffset));
			return indent;
		} catch (BadLocationException e) {
			return indent;
		}
	}

}
