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
package org.rubypeople.rdt.internal.ui.text.template.contentassist;

import java.util.ArrayList;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Point;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.internal.corext.Assert;
import org.rubypeople.rdt.internal.corext.template.ruby.RubyScriptContextType;
import org.rubypeople.rdt.internal.corext.template.ruby.RubyScriptContext;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;

public class TemplateEngine {

    private static final String $_LINE_SELECTION= "${" + GlobalTemplateVariables.LineSelection.NAME + "}"; //$NON-NLS-1$ //$NON-NLS-2$
    private static final String $_WORD_SELECTION= "${" + GlobalTemplateVariables.WordSelection.NAME + "}"; //$NON-NLS-1$ //$NON-NLS-2$

    /** The context type. */
    private TemplateContextType fContextType;
    /** The result proposals. */
    private ArrayList fProposals= new ArrayList();

    /**
     * Creates the template engine for a particular context type.
     * See <code>TemplateContext</code> for supported context types.
     */
    public TemplateEngine(TemplateContextType contextType) {
        Assert.isNotNull(contextType);
        fContextType= contextType;
    }

    /**
     * Empties the collector.
     */
    public void reset() {
        fProposals.clear();
    }

    /**
     * Returns the array of matching templates.
     */
    public TemplateProposal[] getResults() {
        return (TemplateProposal[]) fProposals.toArray(new TemplateProposal[fProposals.size()]);
    }

    /**
     * Inspects the context of the compilation unit around <code>completionPosition</code>
     * and feeds the collector with proposals.
     * @param viewer the text viewer
     * @param completionPosition the context position in the document of the text viewer
     * @param compilationUnit the compilation unit (may be <code>null</code>)
     */
    public void complete(ITextViewer viewer, int completionPosition, IRubyScript compilationUnit) {
        IDocument document= viewer.getDocument();

        if (!(fContextType instanceof RubyScriptContextType))
            return;

        Point selection= viewer.getSelectedRange();

        // remember selected text
        String selectedText= null;
        if (selection.y != 0) {
            try {
                selectedText= document.get(selection.x, selection.y);
            } catch (BadLocationException e) {}
        }


        RubyScriptContext context= ((RubyScriptContextType) fContextType).createContext(document, completionPosition, selection.y, compilationUnit);
        context.setVariable("selection", selectedText); //$NON-NLS-1$
        int start= context.getStart();
        int end= context.getEnd();
        IRegion region= new Region(start, end - start);

        Template[] templates= RubyPlugin.getDefault().getTemplateStore().getTemplates();

        if (selection.y == 0) {
            for (int i= 0; i != templates.length; i++)
                if (context.canEvaluate(templates[i]))
                    fProposals.add(new TemplateProposal(templates[i], context, region, RubyPluginImages.get(RubyPluginImages.IMG_OBJS_TEMPLATE)));

        } else {

            if (context.getKey().length() == 0)
                context.setForceEvaluation(true);

            boolean multipleLinesSelected= areMultipleLinesSelected(viewer);

            for (int i= 0; i != templates.length; i++) {
                Template template= templates[i];
                if (context.canEvaluate(template) &&
                    template.getContextTypeId().equals(context.getContextType().getId()) &&
                    (!multipleLinesSelected && template.getPattern().indexOf($_WORD_SELECTION) != -1 || (multipleLinesSelected && template.getPattern().indexOf($_LINE_SELECTION) != -1)))
                {
                    fProposals.add(new TemplateProposal(templates[i], context, region, RubyPluginImages.get(RubyPluginImages.IMG_OBJS_TEMPLATE)));
                }
            }
        }
    }

    /**
     * Returns <code>true</code> if one line is completely selected or if multiple lines are selected.
     * Being completely selected means that all characters except the new line characters are
     * selected.
     *
     * @return <code>true</code> if one or multiple lines are selected
     * @since 2.1
     */
    private boolean areMultipleLinesSelected(ITextViewer viewer) {
        if (viewer == null)
            return false;

        Point s= viewer.getSelectedRange();
        if (s.y == 0)
            return false;

        try {

            IDocument document= viewer.getDocument();
            int startLine= document.getLineOfOffset(s.x);
            int endLine= document.getLineOfOffset(s.x + s.y);
            IRegion line= document.getLineInformation(startLine);
            return startLine != endLine || (s.x == line.getOffset() && s.y == line.getLength());

        } catch (BadLocationException x) {
            return false;
        }
    }
}
