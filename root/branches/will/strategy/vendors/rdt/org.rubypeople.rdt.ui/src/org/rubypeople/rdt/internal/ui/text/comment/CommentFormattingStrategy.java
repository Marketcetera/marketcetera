/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.rubypeople.rdt.internal.ui.text.comment;

import java.util.LinkedList;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.rubypeople.rdt.core.ToolFactory;
import org.rubypeople.rdt.core.formatter.DefaultCodeFormatterConstants;
import org.rubypeople.rdt.core.formatter.CodeFormatter;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.text.IRubyPartitions;


/**
 * Formatting strategy for general source code comments.
 *
 * @since 3.0
 */
public class CommentFormattingStrategy extends ContextBasedFormattingStrategy {

    /** Documents to be formatted by this strategy */
    private final LinkedList fDocuments= new LinkedList();

    /** Partitions to be formatted by this strategy */
    private final LinkedList fPartitions= new LinkedList();

    /** Last formatted document's hash-code. */
    private int fLastDocumentHash;

    /** Last formatted document header's hash-code. */
    private int fLastHeaderHash;

    /** End of the first class or interface token in the last document. */
    private int fLastMainTokenEnd= -1;

    /** End of the header in the last document. */
    private int fLastDocumentsHeaderEnd;


    /*
     * @see org.eclipse.jface.text.formatter.IFormattingStrategyExtension#format()
     */
    public void format() {
        super.format();

        final IDocument document= (IDocument) fDocuments.removeFirst();
        final TypedPosition position= (TypedPosition)fPartitions.removeFirst();
        if (document == null || position == null)
            return;

        Map preferences= getPreferences();
        final boolean isFormattingHeader= Boolean.toString(true).equals(preferences.get(DefaultCodeFormatterConstants.FORMATTER_COMMENT_FORMAT_HEADER));
        int documentsHeaderEnd= computeHeaderEnd(document);

        if (isFormattingHeader || position.offset >= documentsHeaderEnd) {
            TextEdit edit= null;
            try {
                // compute offset in document of region passed to the formatter
                int sourceOffset= document.getLineOffset(document.getLineOfOffset(position.getOffset()));

                // format region
                int partitionOffset= position.getOffset() - sourceOffset;
                int sourceLength= partitionOffset + position.getLength();
                String source= document.get(sourceOffset, sourceLength);
                CodeFormatter commentFormatter= ToolFactory.createCodeFormatter(preferences);
                int indentationLevel= inferIndentationLevel(source.substring(0, partitionOffset), getTabSize(preferences));
                edit= commentFormatter.format(getKindForPartitionType(position.getType()), source, partitionOffset, position.getLength(), indentationLevel, TextUtilities.getDefaultLineDelimiter(document));

                // move edit offset to match document
                if (edit != null)
                    edit.moveTree(sourceOffset);
            } catch (BadLocationException x) {
                RubyPlugin.log(x);
            }

            try {
                if (edit != null)
                    edit.apply(document);
            } catch (MalformedTreeException x) {
                RubyPlugin.log(x);
            } catch (BadLocationException x) {
                RubyPlugin.log(x);
            }
        }
    }

    /*
     * @see org.eclipse.jface.text.formatter.IFormattingStrategyExtension#formatterStarts(org.eclipse.jface.text.formatter.IFormattingContext)
     */
    public void formatterStarts(IFormattingContext context) {
        super.formatterStarts(context);

        fPartitions.addLast(context.getProperty(FormattingContextProperties.CONTEXT_PARTITION));
        fDocuments.addLast(context.getProperty(FormattingContextProperties.CONTEXT_MEDIUM));
    }

    /*
     * @see org.eclipse.jface.text.formatter.IFormattingStrategyExtension#formatterStops()
     */
    public void formatterStops() {
        fPartitions.clear();
        fDocuments.clear();

        super.formatterStops();
    }

    /**
     * Map from {@link IRubyPartitions}comment partition types to
     * {@link CodeFormatter}code snippet kinds.
     *
     * @param type the partition type
     * @return the code snippet kind
     * @since 3.1
     */
    private static int getKindForPartitionType(String type) {
        if (IRubyPartitions.RUBY_SINGLE_LINE_COMMENT.equals(type))
                return CodeFormatter.K_SINGLE_LINE_COMMENT;
        if (IRubyPartitions.RUBY_MULTI_LINE_COMMENT.equals(type))
                return CodeFormatter.K_MULTI_LINE_COMMENT;
        return CodeFormatter.K_UNKNOWN;
    }

    /**
     * Infer the indentation level based on the given reference indentation
     * and tab size.
     *
     * @param reference the reference indentation
     * @param tabSize the tab size
     * @return the inferred indentation level
     * @since 3.1
     */
    private int inferIndentationLevel(String reference, int tabSize) {
        StringBuffer expanded= expandTabs(reference, tabSize);

        int referenceWidth= expanded.length();
        if (tabSize == 0)
            return referenceWidth;

        int spaceWidth= 1;
        int level= referenceWidth / (tabSize * spaceWidth);
        if (referenceWidth % (tabSize * spaceWidth) > 0)
            level++;
        return level;
    }

    /**
     * Expands the given string's tabs according to the given tab size.
     *
     * @param string the string
     * @param tabSize the tab size
     * @return the expanded string
     * @since 3.1
     */
    private static StringBuffer expandTabs(String string, int tabSize) {
        StringBuffer expanded= new StringBuffer();
        for (int i= 0, n= string.length(), chars= 0; i < n; i++) {
            char ch= string.charAt(i);
            if (ch == '\t') {
                for (; chars < tabSize; chars++)
                    expanded.append(' ');
                chars= 0;
            } else {
                expanded.append(ch);
                chars++;
                if (chars >= tabSize)
                    chars= 0;
            }

        }
        return expanded;
    }

    /**
     * Returns the value of {@link DefaultCodeFormatterConstants#FORMATTER_TAB_SIZE}
     * from the given preferences.
     *
     * @param preferences the preferences
     * @return the value of {@link DefaultCodeFormatterConstants#FORMATTER_TAB_SIZE}
     *         from the given preferences
     * @since 3.1
     */
    private static int getTabSize(Map preferences) {
        if (preferences.containsKey(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE))
            try {
                return Integer.parseInt((String) preferences.get(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE));
            } catch (NumberFormatException e) {
                // use default
            }
        return 4;
    }

    /**
     * Returns the end offset for the document's header.
     *
     * @param document the document
     * @return the header's end offset
     */
    private int computeHeaderEnd(IDocument document) {
        if (document == null)
            return -1;

        try {
            if (fLastMainTokenEnd >= 0 && document.hashCode() == fLastDocumentHash && fLastMainTokenEnd < document.getLength() && document.get(0, fLastMainTokenEnd).hashCode() == fLastHeaderHash)
                return fLastDocumentsHeaderEnd;
        } catch (BadLocationException e) {
            // should not happen -> recompute
        }

        // FIXME Search for end of initial comment. If we hit code, then give up
        
        return -1;
    }
}
