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

package org.rubypeople.rdt.internal.ui.preferences.formatter;

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.IContentFormatterExtension;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.swt.widgets.Composite;
import org.rubypeople.rdt.internal.ui.IRubyStatusConstants;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.text.comment.CommentFormattingContext;


public class RubyScriptPreview extends RubyPreview {

    protected String fPreviewText;

    /**
     * @param workingValues
     * @param parent
     */
    public RubyScriptPreview(Map workingValues, Composite parent) {

        super(workingValues, parent);
    }

    protected void doFormatPreview() {
        if (fPreviewText == null) {
            fPreviewDocument.set(""); //$NON-NLS-1$
            return;
        }
        fPreviewDocument.set(fPreviewText);
        
        fSourceViewer.setRedraw(false);
        final IFormattingContext context = new CommentFormattingContext();
        try {
            final IContentFormatter formatter = fViewerConfiguration.getContentFormatter(fSourceViewer);
            if (formatter instanceof IContentFormatterExtension) {
                final IContentFormatterExtension extension = (IContentFormatterExtension) formatter;
                context.setProperty(FormattingContextProperties.CONTEXT_PREFERENCES, fWorkingValues);
                context.setProperty(FormattingContextProperties.CONTEXT_DOCUMENT, Boolean.valueOf(true));
                extension.format(fPreviewDocument, context);
            } else
                formatter.format(fPreviewDocument, new Region(0, fPreviewDocument.getLength()));
        } catch (Exception e) {
            final IStatus status= new Status(IStatus.ERROR, RubyPlugin.getPluginId(), IRubyStatusConstants.INTERNAL_ERROR, 
                FormatterMessages.RubyPreview_formatter_exception, e); 
            RubyPlugin.log(status);
        } finally {
            context.dispose();
            fSourceViewer.setRedraw(true);
        }
    }
    
    public void setPreviewText(String previewText) {
//        if (previewText == null) throw new IllegalArgumentException();
        fPreviewText= previewText;
        update();
    }
}
