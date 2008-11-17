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
package org.rubypeople.rdt.internal.ui.util;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.util.Assert;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;
import org.rubypeople.rdt.core.IImportDeclaration;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyUIMessages;
import org.rubypeople.rdt.internal.ui.actions.OpenActionUtil;
import org.rubypeople.rdt.internal.ui.rubyeditor.EditorUtility;
import org.rubypeople.rdt.internal.ui.typehierarchy.TypeHierarchyViewPart;
import org.rubypeople.rdt.ui.RubyUI;

public class OpenTypeHierarchyUtil {
	
	private OpenTypeHierarchyUtil() {
	}

	public static TypeHierarchyViewPart open(IRubyElement element, IWorkbenchWindow window) {
		IRubyElement[] candidates= getCandidates(element);
		if (candidates != null) {
			return open(candidates, window);
		}
		return null;
	}	
	
	public static TypeHierarchyViewPart open(IRubyElement[] candidates, IWorkbenchWindow window) {
		Assert.isTrue(candidates != null && candidates.length != 0);
			
		IRubyElement input= null;
		if (candidates.length > 1) {
			String title= RubyUIMessages.OpenTypeHierarchyUtil_selectionDialog_title;  
			String message= RubyUIMessages.OpenTypeHierarchyUtil_selectionDialog_message; 
			input= OpenActionUtil.selectRubyElement(candidates, window.getShell(), title, message);			
		} else {
			input= candidates[0];
		}
		if (input == null)
			return null;
			
//		try {
//			if (PreferenceConstants.OPEN_TYPE_HIERARCHY_IN_PERSPECTIVE.equals(PreferenceConstants.getPreferenceStore().getString(PreferenceConstants.OPEN_TYPE_HIERARCHY))) {
//				return openInPerspective(window, input);
//			} else {
				return openInViewPart(window, input);
//			}
				
//		} catch (WorkbenchException e) {
//			ExceptionHandler.handle(e, window.getShell(),
//				RubyUIMessages.OpenTypeHierarchyUtil_error_open_perspective, 
//				e.getMessage());
//		} catch (RubyModelException e) {
//			ExceptionHandler.handle(e, window.getShell(),
//				RubyUIMessages.OpenTypeHierarchyUtil_error_open_editor, 
//				e.getMessage());
//		}
//		return null;
	}

	private static TypeHierarchyViewPart openInViewPart(IWorkbenchWindow window, IRubyElement input) {
		IWorkbenchPage page= window.getActivePage();
		try {
			TypeHierarchyViewPart result= (TypeHierarchyViewPart) page.findView(RubyUI.ID_TYPE_HIERARCHY);
			if (result != null) {
				result.clearNeededRefresh(); // avoid refresh of old hierarchy on 'becomes visible'
			}
			result= (TypeHierarchyViewPart) page.showView(RubyUI.ID_TYPE_HIERARCHY);
			result.setInputElement(input);
			return result;
		} catch (CoreException e) {
			ExceptionHandler.handle(e, window.getShell(), 
				RubyUIMessages.OpenTypeHierarchyUtil_error_open_view, e.getMessage()); 
		}
		return null;		
	}
	
	private static TypeHierarchyViewPart openInPerspective(IWorkbenchWindow window, IRubyElement input) throws WorkbenchException, RubyModelException {
		IWorkbench workbench= RubyPlugin.getDefault().getWorkbench();
		// The problem is that the input element can be a working copy. So we first convert it to the original element if
		// it exists.
		IRubyElement perspectiveInput= input;
		
		if (input instanceof IMember) {
			if (input.getElementType() != IRubyElement.TYPE) {
				perspectiveInput= ((IMember)input).getDeclaringType();
			} else {
				perspectiveInput= input;
			}
		}
		IWorkbenchPage page= workbench.showPerspective(RubyUI.ID_HIERARCHYPERSPECTIVE, window, perspectiveInput);
		
		TypeHierarchyViewPart part= (TypeHierarchyViewPart) page.findView(RubyUI.ID_TYPE_HIERARCHY);
		if (part != null) {
			part.clearNeededRefresh(); // avoid refresh of old hierarchy on 'becomes visible'
		}		
		part= (TypeHierarchyViewPart) page.showView(RubyUI.ID_TYPE_HIERARCHY);
		part.setInputElement(input);
		if (input instanceof IMember) {
			if (page.getEditorReferences().length == 0) {
				openEditor(input, false); // only open when the perspecive has been created
			}
		}
		return part;
	}

	private static void openEditor(Object input, boolean activate) throws PartInitException, RubyModelException {
		IEditorPart part= EditorUtility.openInEditor(input, activate);
		if (input instanceof IRubyElement)
			EditorUtility.revealInEditor(part, (IRubyElement) input);
	}
	
	/**
	 * Converts the input to a possible input candidates
	 */	
	public static IRubyElement[] getCandidates(Object input) {
		if (!(input instanceof IRubyElement)) {
			return null;
		}
		try {
			IRubyElement elem= (IRubyElement) input;
			switch (elem.getElementType()) {
				case IRubyElement.METHOD:
				case IRubyElement.FIELD:
				case IRubyElement.TYPE:
				case IRubyElement.SOURCE_FOLDER_ROOT:
				case IRubyElement.RUBY_PROJECT:
					return new IRubyElement[] { elem };
				case IRubyElement.SOURCE_FOLDER:
					if (((ISourceFolder)elem).containsRubyResources())
						return new IRubyElement[] {elem};
					break;
				case IRubyElement.IMPORT_DECLARATION:	
					IImportDeclaration decl= (IImportDeclaration) elem;
				
					elem= elem.getRubyProject().findType(elem.getElementName());
					
					if (elem == null)
						return null;
					return new IRubyElement[] {elem};
			
				case IRubyElement.SCRIPT: {
					IRubyScript cu= (IRubyScript) elem.getAncestor(IRubyElement.SCRIPT);
					if (cu != null) {
						IType[] types= cu.getTypes();
						if (types.length > 0) {
							return types;
						}
					}
					break;
				}					
				default:
			}
		} catch (RubyModelException e) {
			RubyPlugin.log(e);
		}
		return null;	
	}
}
