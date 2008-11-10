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
package org.rubypeople.rdt.internal.ui.text.ruby;

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
import org.rubypeople.rdt.core.formatter.CodeFormatter;
import org.rubypeople.rdt.internal.corext.util.CodeFormatterUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

/**
 * Formatting strategy for ruby source code.
 *
 * @since 0.8.0
 */
public class RubyFormattingStrategy extends ContextBasedFormattingStrategy {

    /** Documents to be formatted by this strategy */
    private final LinkedList fDocuments= new LinkedList();
    /** Partitions to be formatted by this strategy */
    private final LinkedList fPartitions= new LinkedList();

    /**
     * Creates a new ruby formatting strategy.
     */
    public RubyFormattingStrategy() {
        super();
    }

    /*
     * @see org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy#format()
     */
    public void format() {
        super.format();

        final IDocument document= (IDocument)fDocuments.removeFirst();
        final TypedPosition partition= (TypedPosition)fPartitions.removeFirst();

        if (document != null && partition != null) {
            try {

                final TextEdit edit= CodeFormatterUtil.format2(CodeFormatter.K_RUBY_SCRIPT, document.get(), partition.getOffset(), partition.getLength(), 0, TextUtilities.getDefaultLineDelimiter(document), getPreferences());
                if (edit != null) {
                    Map partitioners= null;
                    if (edit.getChildrenSize() > 20)
                        partitioners= TextUtilities.removeDocumentPartitioners(document);

                    edit.apply(document);

                    if (partitioners != null)
                        TextUtilities.addDocumentPartitioners(document, partitioners);
                }

            } catch (MalformedTreeException exception) {
                RubyPlugin.log(exception);
            } catch (BadLocationException exception) {
                // Can only happen on concurrent document modification - log and bail out
                RubyPlugin.log(exception);
            }
        }
    }

    /*
     * @see org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy#formatterStarts(org.eclipse.jface.text.formatter.IFormattingContext)
     */
    public void formatterStarts(final IFormattingContext context) {
        super.formatterStarts(context);

        fPartitions.addLast(context.getProperty(FormattingContextProperties.CONTEXT_PARTITION));
        fDocuments.addLast(context.getProperty(FormattingContextProperties.CONTEXT_MEDIUM));
    }

    /*
     * @see org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy#formatterStops()
     */
    public void formatterStops() {
        super.formatterStops();

        fPartitions.clear();
        fDocuments.clear();
    }
}
