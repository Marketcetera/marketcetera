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

import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jruby.lexer.yacc.ISourcePosition;

public class PositionBasedEditorOpener extends EditorOpener {

    private final ISourcePosition position;

    public PositionBasedEditorOpener(String filename, ISourcePosition position) {
        super(filename);
        this.position = position;
    }

    protected void setEditorPosition(ITextEditor editor) {
        IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
        int start = position.getStartOffset();
        int end   = position.getEndOffset();
        editor.selectAndReveal(start, end-start);
    }

}
