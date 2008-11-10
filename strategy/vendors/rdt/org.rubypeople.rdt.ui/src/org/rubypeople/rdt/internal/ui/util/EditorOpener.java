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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.ExternalRubyFileEditorInput;

public abstract class EditorOpener {
    private final String filename;

    public EditorOpener(String filename) {
        this.filename = filename;
    }
    
    public void open() {
        try {
            IEditorInput fileEditorInput = createEditorInput(filename);
            IWorkbench workbench = PlatformUI.getWorkbench();
            IEditorRegistry editorRegistry = workbench.getEditorRegistry();
            IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
            IEditorDescriptor descriptor = editorRegistry.getDefaultEditor(filename);
            if (descriptor == null)
                return;
            ITextEditor editor = (ITextEditor) page.openEditor(fileEditorInput, editorId(descriptor));
            setEditorPosition(editor);
        } catch (PartInitException e) {
            RubyPlugin.log(e);
        }
    }
    
    protected abstract void setEditorPosition(ITextEditor editor);

    private String editorId(IEditorDescriptor descriptor)
    {
        String editorId;
        if (descriptor == null)
        {
            editorId = "org.eclipse.ui.DefaultTextEditor";                         //$NON-NLS-1$
        }
        else
        {
            editorId = descriptor.getId();
        }
        return editorId;
    }
    
    private IEditorInput createEditorInput(String filename) {
        IFile file = getWorkspaceFile(filename);
        if (file == null) 
            return new ExternalRubyFileEditorInput(new java.io.File(filename));
        return new FileEditorInput(file);
    }

    private IFile getWorkspaceFile(String filename) {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        IPath filepath = new Path(filename);
        IFile file = root.getFileForLocation(filepath);
        
        return file;
    }

}