package org.rubypeople.rdt.internal.corext.util;

import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Position;
import org.eclipse.text.edits.TextEdit;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.ToolFactory;
import org.rubypeople.rdt.core.formatter.DefaultCodeFormatterConstants;
import org.rubypeople.rdt.internal.corext.Assert;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

public class CodeFormatterUtil {

	/**
	 * Creates edits that describe how to format the given string. Returns
	 * <code>null</code> if the code could not be formatted for the given
	 * kind.
	 * 
	 * @throws IllegalArgumentException
	 *             If the offset and length are not inside the string, a
	 *             IllegalArgumentException is thrown.
	 */
	public static TextEdit format2(int kind, String string, int offset,
			int length, int indentationLevel, String lineSeparator, Map options) {
		if (offset < 0 || length < 0 || offset + length > string.length()) {
			throw new IllegalArgumentException(
					"offset or length outside of string. offset: " + offset + ", length: " + length + ", string size: " + string.length()); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		}
		return ToolFactory.createCodeFormatter(options).format(kind, string,
				offset, length, indentationLevel, lineSeparator);
	}

	/**
	 * Returns the current indent width.
	 * 
	 * @param project
	 *            the project where the source is used or <code>null</code> if
	 *            the project is unknown and the workspace default should be
	 *            used
	 * @return the indent width
	 * @since 0.8.0
	 */
	public static int getIndentWidth(IRubyProject project) {
		String key;
		if (DefaultCodeFormatterConstants.MIXED.equals(getCoreOption(project,
				DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR)))
			key = DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE;
		else
			key = DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE;

		return getCoreOption(project, key, 4);
	}

	/**
	 * Gets the current tab width.
	 * 
	 * @param project
	 *            The project where the source is used, used for project
	 *            specific options or <code>null</code> if the project is
	 *            unknown and the workspace default should be used
	 * @return The tab width
	 */
	public static int getTabWidth(IRubyProject project) {
		/*
		 * If the tab-char is SPACE, FORMATTER_INDENTATION_SIZE is not used by
		 * the core formatter. We piggy back the visual tab length setting in
		 * that preference in that case.
		 */
		String key;
		if (RubyCore.SPACE.equals(getCoreOption(project,
				DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR)))
			key = DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE;
		else
			key = DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE;

		return getCoreOption(project, key, 4);
	}

	/**
	 * Returns the possibly <code>project</code>-specific core preference
	 * defined under <code>key</code>.
	 * 
	 * @param project
	 *            the project to get the preference from, or <code>null</code>
	 *            to get the global preference
	 * @param key
	 *            the key of the preference
	 * @return the value of the preference
	 * @since 0.8.0
	 */
	private static String getCoreOption(IRubyProject project, String key) {
		if (project == null)
			return RubyCore.getOption(key);
		return project.getOption(key, true);
	}

	/**
	 * Returns the possibly <code>project</code>-specific core preference
	 * defined under <code>key</code>, or <code>def</code> if the value is
	 * not a integer.
	 * 
	 * @param project
	 *            the project to get the preference from, or <code>null</code>
	 *            to get the global preference
	 * @param key
	 *            the key of the preference
	 * @param def
	 *            the default value
	 * @return the value of the preference
	 * @since 0.8.0
	 */
	private static int getCoreOption(IRubyProject project, String key, int def) {
		try {
			return Integer.parseInt(getCoreOption(project, key));
		} catch (NumberFormatException e) {
			return def;
		}
	}

	/**
	 * Creates a string that represents the given number of indentation units.
	 * The returned string can contain tabs and/or spaces depending on the core
	 * formatter preferences.
	 * 
	 * @param indentationUnits
	 *            the number of indentation units to generate
	 * @param project
	 *            the project from which to get the formatter settings,
	 *            <code>null</code> if the workspace default should be used
	 * @return the indent string
	 */
	public static String createIndentString(int indentationUnits,
			IRubyProject project) {
		final String tabChar = getCoreOption(project,
				DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR);
		final int tabs, spaces;
		if (RubyCore.SPACE.equals(tabChar)) {
			tabs = 0;
			spaces = indentationUnits * getIndentWidth(project);
		} else if (RubyCore.TAB.equals(tabChar)) {
			// indentWidth == tabWidth
			tabs = indentationUnits;
			spaces = 0;
		} else if (DefaultCodeFormatterConstants.MIXED.equals(tabChar)) {
			int tabWidth = getTabWidth(project);
			int spaceEquivalents = indentationUnits * getIndentWidth(project);
			if (tabWidth > 0) {
				tabs = spaceEquivalents / tabWidth;
				spaces = spaceEquivalents % tabWidth;
			} else {
				tabs = 0;
				spaces = spaceEquivalents;
			}
		} else {
			// new indent type not yet handled
			Assert.isTrue(false);
			return null;
		}

		StringBuffer buffer = new StringBuffer(tabs + spaces);
		for (int i = 0; i < tabs; i++)
			buffer.append('\t');
		for (int i = 0; i < spaces; i++)
			buffer.append(' ');
		return buffer.toString();
	}

	public static TextEdit format2(int kind, String string,
			int indentationLevel, String lineSeparator, Map options) {
		return format2(kind, string, 0, string.length(), indentationLevel,
				lineSeparator, options);
	}

	/**
	 * Old API. Consider to use format2 (TextEdit)
	 */
	public static String format(int kind, String string, int offset,
			int length, int indentationLevel, int[] positions,
			String lineSeparator, Map options) {
		TextEdit edit = format2(kind, string, offset, length, indentationLevel,
				lineSeparator, options);
		if (edit == null) {
			// JavaPlugin.logErrorMessage("formatter failed to format (no edit
			// returned). Will use unformatted text instead. kind: " + kind + ",
			// string: " + string); //$NON-NLS-1$ //$NON-NLS-2$
			return string.substring(offset, offset + length);
		}
		String formatted = getOldAPICompatibleResult(string, edit,
				indentationLevel, positions, lineSeparator, options);
		return formatted.substring(offset, formatted.length()
				- (string.length() - (offset + length)));
	}

	private static String getOldAPICompatibleResult(String string, TextEdit edit, int indentationLevel, int[] positions, String lineSeparator, Map options) {
		Position[] p= null;
		
		if (positions != null) {
			p= new Position[positions.length];
			for (int i= 0; i < positions.length; i++) {
				p[i]= new Position(positions[i], 0);
			}
		}
		String res= evaluateFormatterEdit(string, edit, p);
		
		if (positions != null) {
			for (int i= 0; i < positions.length; i++) {
				Position curr= p[i];
				positions[i]= curr.getOffset();
			}
		}			
		return res;
	}
	/**
	 * Evaluates the edit on the given string.
	 * @throws IllegalArgumentException If the positions are not inside the string, a
	 *  IllegalArgumentException is thrown.
	 */
	public static String evaluateFormatterEdit(String string, TextEdit edit, Position[] positions) {
		try {
			Document doc= createDocument(string, positions);
			edit.apply(doc, 0);
			if (positions != null) {
				for (int i= 0; i < positions.length; i++) {
					Assert.isTrue(!positions[i].isDeleted, "Position got deleted"); //$NON-NLS-1$
				}
			}
			return doc.get();
		} catch (BadLocationException e) {
			RubyPlugin.log(e); // bug in the formatter
			Assert.isTrue(false, "Formatter created edits with wrong positions: " + e.getMessage()); //$NON-NLS-1$
		}
		return null;
	}
	
	private static Document createDocument(String string, Position[] positions) throws IllegalArgumentException {
		Document doc= new Document(string);
		try {
			if (positions != null) {
				final String POS_CATEGORY= "myCategory"; //$NON-NLS-1$
				
				doc.addPositionCategory(POS_CATEGORY);
				doc.addPositionUpdater(new DefaultPositionUpdater(POS_CATEGORY) {
					protected boolean notDeleted() {
						if (fOffset < fPosition.offset && (fPosition.offset + fPosition.length < fOffset + fLength)) {
							fPosition.offset= fOffset + fLength; // deleted positions: set to end of remove
							return false;
						}
						return true;
					}
				});
				for (int i= 0; i < positions.length; i++) {
					try {
						doc.addPosition(POS_CATEGORY, positions[i]);
					} catch (BadLocationException e) {
						throw new IllegalArgumentException("Position outside of string. offset: " + positions[i].offset + ", length: " + positions[i].length + ", string size: " + string.length());   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
					}
				}
			}
		} catch (BadPositionCategoryException cannotHappen) {
			// can not happen: category is correctly set up
		}
		return doc;
	}

	public static String format(int kind, String string, int indentationLevel, int[] positions, String lineSeparator, IRubyProject project) {
		Map options= project != null ? project.getOptions(true) : null;
		return format(kind, string, 0, string.length(), indentationLevel, positions, lineSeparator, options);
	}
}
