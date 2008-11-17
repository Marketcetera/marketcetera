/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.EditorUtility;

public class RubySearchEditorOpener {

	private static class ReusedEditorWatcher implements IPartListener {
		
		private IEditorPart fReusedEditor;
		private IPartService fPartService;
		
		public ReusedEditorWatcher() {
			fReusedEditor= null;
			fPartService= null;
		}
		
		public IEditorPart getReusedEditor() {
			return fReusedEditor;
		}
		
		public void initialize(IEditorPart editor) {
			if (fReusedEditor != null) {
				fPartService.removePartListener(this);
			}
			fReusedEditor= editor;
			if (editor != null) {
				fPartService= editor.getSite().getWorkbenchWindow().getPartService();
				fPartService.addPartListener(this);
			} else {
				fPartService= null;
			}
		}
		
		public void partOpened(IWorkbenchPart part) {
		}

		public void partDeactivated(IWorkbenchPart part) {
		}

		public void partClosed(IWorkbenchPart part) {
			if (part == fReusedEditor) {
				initialize(null);
			}
		}

		public void partActivated(IWorkbenchPart part) {
		}

		public void partBroughtToTop(IWorkbenchPart part) {
		}
	}
	
	private ReusedEditorWatcher fReusedEditorWatcher;


	public IEditorPart openElement(Object element) throws PartInitException, RubyModelException {
		IWorkbenchPage wbPage= RubyPlugin.getActivePage();
		if (NewSearchUI.reuseEditor())
			return showWithReuse(element, wbPage);
		else
			return showWithoutReuse(element, wbPage);
	}
		
	public IEditorPart openMatch(Match match) throws PartInitException, RubyModelException {
		Object element= getElementToOpen(match);
		return openElement(element);
	}

	protected Object getElementToOpen(Match match) {
		return match.getElement();
	}

	private IEditorPart showWithoutReuse(Object element, IWorkbenchPage wbPage) throws PartInitException, RubyModelException {
		return EditorUtility.openInEditor(element, false);
	}

	private IEditorPart showWithReuse(Object element, IWorkbenchPage wbPage) throws RubyModelException, PartInitException {
		IFile file= getFile(element);
		if (file != null) {
			String editorID= getEditorID(file);
			return showInEditor(wbPage, new FileEditorInput(file), editorID);
		}
		return showWithoutReuse(element, wbPage);
	}

	private IFile getFile(Object element) throws RubyModelException {
		if (element instanceof IFile)
			return (IFile) element;
		if (element instanceof IRubyElement) {
			IRubyElement jElement= (IRubyElement) element;
			IRubyScript cu= (IRubyScript) jElement.getAncestor(IRubyElement.SCRIPT);
			if (cu != null) {
				return (IFile) cu.getCorrespondingResource();
			}
		}
		return null;
	}

	private String getEditorID(IFile file) throws PartInitException {
		IEditorDescriptor desc= IDE.getEditorDescriptor(file);
		if (desc == null)
			return RubyPlugin.getDefault().getWorkbench().getEditorRegistry().findEditor(IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID).getId();
		else
			return desc.getId();
	}

	private boolean isPinned(IEditorPart editor) {
		if (editor == null)
			return false;

		IEditorReference[] editorRefs= editor.getEditorSite().getPage().getEditorReferences();
		int i= 0;
		while (i < editorRefs.length) {
			if (editor.equals(editorRefs[i].getEditor(false)))
				return editorRefs[i].isPinned();
			i++;
		}
		return false;
	}

	private IEditorPart showInEditor(IWorkbenchPage page, IEditorInput input, String editorId) {
		IEditorPart editor= page.findEditor(input);
		if (editor != null)
			page.bringToTop(editor);
		else {
			IEditorPart reusedEditor= getReusedEditor();
			boolean isOpen= false;
			if (reusedEditor != null) {
				IEditorReference[] parts= page.getEditorReferences();
				int i= 0;
				while (!isOpen && i < parts.length)
					isOpen= reusedEditor == parts[i++].getEditor(false);
			}

			boolean canBeReused= isOpen && !reusedEditor.isDirty() && !isPinned(reusedEditor);
			boolean showsSameInputType= reusedEditor != null && reusedEditor.getSite().getId().equals(editorId);
			if (canBeReused && !showsSameInputType) {
				page.closeEditor(reusedEditor, false);
				setReusedEditor(null);
			}

			if (canBeReused && showsSameInputType) {
				((IReusableEditor) reusedEditor).setInput(input);
				page.bringToTop(reusedEditor);
				editor= reusedEditor;
			} else {
				try {
					editor= page.openEditor(input, editorId, false);
					if (editor instanceof IReusableEditor)
						setReusedEditor(editor);
					else
						setReusedEditor(null);
				} catch (PartInitException ex) {
					MessageDialog.openError(RubyPlugin.getActiveWorkbenchShell(), SearchMessages.Search_Error_openEditor_title, SearchMessages.Search_Error_openEditor_message); 
					return null;
				}
			}
		}
		return editor;
	}

	private IEditorPart getReusedEditor() {
		if (fReusedEditorWatcher != null) 
			return fReusedEditorWatcher.getReusedEditor();
		return null;
	}
	
	private void setReusedEditor(IEditorPart editor) {
		if (fReusedEditorWatcher == null) {
			fReusedEditorWatcher= new ReusedEditorWatcher();
		}
		fReusedEditorWatcher.initialize(editor);
	}

}
