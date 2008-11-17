package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.rubypeople.rdt.internal.ui.text.ruby.LegacyRubyCompletionProcessor;

public class RubyAutoEditStrategy implements ILinkedModeEditStrategy {

	private LegacyRubyCompletionProcessor fRubyCp;

	private ISourceViewer fViewer;
	
	/** The linked position list */
	protected final List fPositionList = new ArrayList();
	
	public RubyAutoEditStrategy(String partition, ISourceViewer viewer,
			LegacyRubyCompletionProcessor rhtmlCp) {
		fViewer = viewer;
		fRubyCp = rhtmlCp;
	}
	
	public void customizeDocumentCommand(IDocument document,
			DocumentCommand command) {
		fPositionList.clear();
		if (command.text.length() == 1) {
			if (command.text.charAt(0) == '\t') {
				try {
					int length = 0;
					while ((command.offset - length > 0)
							&& Pattern.matches("\\w", document.get(
									command.offset - length - 1, 1))) {
						length++;
					}
					String prefix = document.get(command.offset - length,
							length);
					Region region = new Region(
							command.offset - prefix.length(), prefix.length());
					if (prefix.length() > 0) {
						Template[] templates = fRubyCp
								.getTemplates("ruby");
						for (int i = 0; i < templates.length; i++) {
							Template template = templates[i];
							if (template.getName().equals(prefix)) {
								final int offset = command.offset
										- prefix.length();
								TemplateContextType contextType = fRubyCp
										.getContextType(fViewer, region);
								TemplateContext context = new DocumentTemplateContext(
										contextType, document, region
												.getOffset(), region
												.getLength());

								context.setReadOnly(false);
								TemplateBuffer templateBuffer;
								try {
									templateBuffer = context.evaluate(template);
								} catch (TemplateException e1) {
									return;
								}

								int start = getReplaceOffset(context, region);
								int end = Math.max(getReplaceEndOffset(context,
										region), offset);

								// insert template string
								String templateString = templateBuffer
										.getString();

								command.text = templateString;
								command.offset = start;
								command.length = end - start;

								fPositionList.add(new LinkedPosition(document,
										offset + templateString.length(), 0));
								
								// translate positions
								TemplateVariable[] variables = templateBuffer
										.getVariables();
								for (int z = 0; z != variables.length; z++) {
									TemplateVariable variable = variables[z];

									if (variable.isUnambiguous())
										continue;

									int[] offsets = variable.getOffsets();
									int variablelength = variable.getLength();

									for (int j = 0; j != offsets.length; j++)
										fPositionList.add(new LinkedPosition(
												document, offsets[j] + start,
												variablelength));
								}

								break;
							}

						}
					}
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public LinkedPosition[] getLinkedPositions() {
		final int size = fPositionList.size();
		if (size > 0) {

			final LinkedPosition[] positions = new LinkedPosition[size];
			fPositionList.toArray(positions);

			return positions;
		}
		return null;
	}

	/**
	 * Returns the offset of the range in the document that will be replaced by
	 * applying this template.
	 * 
	 * @return the offset of the range in the document that will be replaced by
	 *         applying this template
	 * @since 3.1
	 */
	protected final int getReplaceOffset(TemplateContext context, Region region) {
		int start;
		if (context instanceof DocumentTemplateContext) {
			DocumentTemplateContext docContext = (DocumentTemplateContext) context;
			start = docContext.getStart();
		} else {
			start = region.getOffset();
		}
		return start;
	}

	/**
	 * Returns the end offset of the range in the document that will be replaced
	 * by applying this template.
	 * 
	 * @return the end offset of the range in the document that will be replaced
	 *         by applying this template
	 * @since 3.1
	 */
	protected final int getReplaceEndOffset(TemplateContext context,
			Region region) {
		int end;
		if (context instanceof DocumentTemplateContext) {
			DocumentTemplateContext docContext = (DocumentTemplateContext) context;
			end = docContext.getEnd();
		} else {
			end = region.getOffset() + region.getLength();
		}
		return end;
	}

}
