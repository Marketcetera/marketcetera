/*
?* Author: David Corbin
?*
?* Copyright (c) 2005 RubyPeople.
?*
?* This file is part of the Ruby Development Tools (RDT) plugin for eclipse. 
 * RDT is subject to the "Common Public License (CPL) v 1.0". You may not use
 * RDT except in compliance with the License. For further information see 
 * org.rubypeople.rdt/rdt.license.
?*/

package org.rubypeople.rdt.internal.ui.util;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.texteditor.ITextEditor;

public class LineBasedEditorOpener extends EditorOpener {

    private final int lineNumber;

    public LineBasedEditorOpener(String filename, int lineNumber) {
        super(filename);
        this.lineNumber = lineNumber;
    }

    protected void setEditorPosition(ITextEditor editor) {
        try {
            if (lineNumber > 0) {
                IDocument document= editor.getDocumentProvider().getDocument(editor.getEditorInput());
                int offset = document.getLineOffset(lineNumber-1);
                int length = document.getLineLength(lineNumber-1);
                editor.selectAndReveal(offset, length);
            }
        } catch (BadLocationException doNothing) {
        }
    }

}
