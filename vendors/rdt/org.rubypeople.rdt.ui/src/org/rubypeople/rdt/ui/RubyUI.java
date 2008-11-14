/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyUIMessages;
import org.rubypeople.rdt.internal.ui.SharedImages;
import org.rubypeople.rdt.internal.ui.dialogs.TypeSelectionDialog2;
import org.rubypeople.rdt.internal.ui.rubyeditor.EditorUtility;
import org.rubypeople.rdt.ui.dialogs.TypeSelectionExtension;

/**
 * Central access point for the Ruby UI plug-in (id
 * <code>"org.rubypeople.rdt.ui"</code>). This class provides static methods
 * for:
 * <ul>
 * <li> creating various kinds of selection dialogs to present a collection of
 * Ruby elements to the user and let them make a selection.</li>
 * <li> opening a Ruby editor on a compilation unit.</li>
 * </ul>
 * <p>
 * This class provides static methods and fields only; it is not intended to be
 * instantiated or subclassed by clients.
 * </p>
 */
public final class RubyUI {

	private RubyUI() {
		// prevent instantiation of RubyUI.
	}
	
	private static ISharedImages fgSharedImages= null;
	
	/**
	 * The id of the Ruby plugin (value <code>"org.rubypeople.rdt.ui"</code>).
	 */
	public static final String ID_PLUGIN = "org.rubypeople.rdt.ui"; //$NON-NLS-1$

	/**
	 * The id of the Ruby action set
	 * (value <code>"org.rubypeople.rdt.ui.rubyActionSet"</code>).
	 */
	public static final String ID_ACTION_SET = "org.rubypeople.rdt.ui.rubyActionSet"; //$NON-NLS-1$

	/**
	 * The editor part id of the editor that presents Ruby compilation units
	 * (value <code>"org.rubypeople.rdt.ui.EditorRubyFile"</code>).
	 */	
	public static final String ID_RUBY_EDITOR=			"org.rubypeople.rdt.ui.EditorRubyFile"; //$NON-NLS-1$
	
	/**
	 * The editor part id of the editor that presents Ruby binary class files
	 * (value <code>"org.rubypeople.rdt.ui.ExternalRubyEditor"</code>).
	 */
	public static final String ID_EXTERNAL_EDITOR=			"org.rubypeople.rdt.ui.ExternalRubyEditor"; //$NON-NLS-1$
	
	/**
	 * The view part id of the Ruby Browsing Projects view (value
	 * <code>"org.rubypeople.rdt.ui.ProjectsView"</code>).
	 * 
	 * @since 0.8.0
	 */
	public static String ID_PROJECTS_VIEW = "org.rubypeople.rdt.ui.ProjectsView"; //$NON-NLS-1$

	/**
	 * The view part id of the Ruby Browsing Types view (value
	 * <code>"org.rubypeople.rdt.ui.TypesView"</code>).
	 * 
	 * @since 0.8.0
	 */
	public static String ID_TYPES_VIEW = "org.rubypeople.rdt.ui.TypesView"; //$NON-NLS-1$

	/**
	 * The view part id of the Ruby Browsing Memberss view (value
	 * <code>"org.rubypeople.rdt.ui.MembersView"</code>).
	 * 
	 * @since 0.8.0
	 */
	public static String ID_MEMBERS_VIEW = "org.rubypeople.rdt.ui.MembersView"; //$NON-NLS-1$

	/**
	 * The id of the Ruby Element Creation action set (value
	 * <code>"org.rubypeople.rdt.ui.RubyElementCreationActionSet"</code>).
	 * 
	 * @since 0.8.0
	 */
	public static final String ID_ELEMENT_CREATION_ACTION_SET = "org.rubypeople.rdt.ui.RubyElementCreationActionSet"; //$NON-NLS-1$

	/**
	 * The id of the Ruby perspective
	 * (value <code>"org.rubypeople.rdt.ui.PerspectiveRuby"</code>).
	 */	
	public static final String ID_PERSPECTIVE= 		"org.rubypeople.rdt.ui.PerspectiveRuby"; //$NON-NLS-1$

	/** 
	 * The view part id of the type hierarchy part
	 * (value <code>"org.rubypeople.rdt.ui.TypeHierarchy"</code>).
	 * <p>
	 * When this id is used to access
	 * a view part with <code>IWorkbenchPage.findView</code> or 
	 * <code>showView</code>, the returned <code>IViewPart</code>
	 * can be safely cast to an <code>ITypeHierarchyViewPart</code>.
	 * </p>
	 *
	 * @see ITypeHierarchyViewPart
	 * @see org.eclipse.ui.IWorkbenchPage#findView(java.lang.String)
	 * @see org.eclipse.ui.IWorkbenchPage#showView(java.lang.String)
	 */ 
	public static final String ID_TYPE_HIERARCHY= 		"org.rubypeople.rdt.ui.TypeHierarchy"; //$NON-NLS-1$

	/**
	 * The id of the Ruby hierarchy perspective
	 * (value <code>"org.rubypeople.rdt.ui.RubyHierarchyPerspective"</code>).
	 */	
	public static final String ID_HIERARCHYPERSPECTIVE= "org.rubypeople.rdt.ui.RubyHierarchyPerspective"; //$NON-NLS-1$

	/**
	 * The view part id of the Packages view
	 * (value <code>"org.rubypeople.rdt.ui.RubyExplorer"</code>).
	 * <p>
	 * When this id is used to access
	 * a view part with <code>IWorkbenchPage.findView</code> or 
	 * <code>showView</code>, the returned <code>IViewPart</code>
	 * can be safely cast to an <code>IPackagesViewPart</code>.
	 * </p>
	 *
	 * @see IPackagesViewPart
	 * @see org.eclipse.ui.IWorkbenchPage#findView(java.lang.String)
	 * @see org.eclipse.ui.IWorkbenchPage#showView(java.lang.String)
	 */ 
	public static final String ID_RUBY_EXPLORER= 			"org.rubypeople.rdt.ui.RubyExplorer"; //$NON-NLS-1$
		
	/**
	 * @deprecated View is going to be removed
	 */
	public static final String ID_RUBY_RESOURCE_VIEW = "org.rubypeople.rdt.ui.ViewRubyResources"; //$NON-NLS-1$
	
	public static final String ID_RULER_CONTEXT_MENU = "org.rubypeople.rdt.ui.rubyeditor.rulerContextMenu"; //$NON-NLS-1$
	public static final String ID_EDITOR_CONTEXT_MENU = "org.rubypeople.rdt.ui.rubyeditor.contextMenu"; //$NON-NLS-1$
	
	/**
	 * Returns the Ruby element wrapped by the given editor input.
	 * 
	 * @param editorInput
	 *            the editor input
	 * @return the Ruby element wrapped by <code>editorInput</code> or
	 *         <code>null</code> if none
	 * @since 0.9.0
	 */
	public static IRubyElement getEditorInputRubyElement(
			IEditorInput editorInput) {
		Assert.isNotNull(editorInput);
		IRubyElement re = getWorkingCopyManager().getWorkingCopy(editorInput);
		if (re != null)
			return re;
		
		/*
		 * This needs works, see
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=120340
		 */
		re = (IRubyElement) editorInput.getAdapter(IRubyElement.class);
		if (re != null)
			return re;
		
		// Case of RHTML Editor
		if (editorInput instanceof IFileEditorInput) {
			IFileEditorInput fileInput = (IFileEditorInput) editorInput;
			IFile file = fileInput.getFile();
			re = RubyCore.create(file);
		}
		return re;
	}

	/**
	 * Returns the shared images for the Ruby UI.
	 *
	 * @return the shared images manager
	 */
	public static ISharedImages getSharedImages() {
		if (fgSharedImages == null)
			fgSharedImages= new SharedImages();
			
		return fgSharedImages;
	}
	
	/**
	 * Returns the working copy manager for the Ruby UI plug-in.
	 * 
	 * @return the working copy manager for the Ruby UI plug-in
	 */
	public static IWorkingCopyManager getWorkingCopyManager() {
		return RubyPlugin.getDefault().getWorkingCopyManager();
	}
	/**
	 * Returns the DocumentProvider used for Ruby compilation units.
	 *
	 * @return the DocumentProvider for Ruby compilation units.
	 * 
	 * @see IDocumentProvider
	 * @since 2.0
	 */
	public static IDocumentProvider getDocumentProvider() {
		return RubyPlugin.getDefault().getRubyDocumentProvider();
	}

	/** 
	 * Reveals the given java element  in the given editor. If the element is not an instance
	 * of <code>ISourceReference</code> this method result in a NOP. If it is a source
	 * reference no checking is done if the editor displays a compilation unit or class file that 
	 * contains the source reference element. The editor simply reveals the source range 
	 * denoted by the given element.
	 * 
	 * @param part the editor displaying a compilation unit or class file
	 * @param element the element to be revealed
	 * 
	 * @since 2.0
	 */
	public static void revealInEditor(IEditorPart part, IRubyElement element) {
		EditorUtility.revealInEditor(part, element);
	}
	
	/**
	 * Creates a selection dialog that lists all types in the given scope.
	 * The caller is responsible for opening the dialog with <code>Window.open</code>,
	 * and subsequently extracting the selected type(s) (of type
	 * <code>IType</code>) via <code>SelectionDialog.getResult</code>.
	 * 
	 * @param parent the parent shell of the dialog to be created
	 * @param context the runnable context used to show progress when the dialog
	 *   is being populated
	 * @param scope the scope that limits which types are included
	 * @param multipleSelection <code>true</code> if multiple selection is allowed
	 * 
	 * @return a new selection dialog
	 * 
	 * @exception RubyModelException if the selection dialog could not be opened
	 */
	public static SelectionDialog createTypeDialog(Shell parent, IRunnableContext context, IRubySearchScope scope, int elementKinds, boolean multipleSelection) throws RubyModelException {
		return createTypeDialog(parent, context, scope, elementKinds, multipleSelection, "");//$NON-NLS-1$
	}
	
	/**
	 * Creates a selection dialog that lists all types in the given scope.
	 * The caller is responsible for opening the dialog with <code>Window.open</code>,
	 * and subsequently extracting the selected type(s) (of type
	 * <code>IType</code>) via <code>SelectionDialog.getResult</code>.
	 * 
	 * @param parent the parent shell of the dialog to be created
	 * @param context the runnable context used to show progress when the dialog
	 *   is being populated
	 * @param scope the scope that limits which types are included
	 * @param multipleSelection <code>true</code> if multiple selection is allowed
	 * @param filter the initial pattern to filter the set of types. For example "Abstract" shows 
	 *  all types starting with "abstract". The meta character '?' representing any character and 
	 *  '*' representing any string are supported. Clients can pass an empty string if no filtering 
	 *  is required.
	 *  
	 * @return a new selection dialog
	 * 
	 * @exception RubyModelException if the selection dialog could not be opened
	 * 
	 * @since 2.0
	 */
	public static SelectionDialog createTypeDialog(Shell parent, IRunnableContext context, IRubySearchScope scope, int elementKinds, boolean multipleSelection, String filter) throws RubyModelException {
		return createTypeDialog(parent, context, scope, elementKinds, multipleSelection, filter, null);
	}
	
	/**
	 * Creates a selection dialog that lists all types in the given scope.
	 * The caller is responsible for opening the dialog with <code>Window.open</code>,
	 * and subsequently extracting the selected type(s) (of type
	 * <code>IType</code>) via <code>SelectionDialog.getResult</code>.
	 * 
	 * @param parent the parent shell of the dialog to be created
	 * @param context the runnable context used to show progress when the dialog
	 *   is being populated
	 * @param scope the scope that limits which types are included
	 * @param multipleSelection <code>true</code> if multiple selection is allowed
	 * @param filter the initial pattern to filter the set of types. For example "Abstract" shows 
	 *  all types starting with "abstract". The meta character '?' representing any character and 
	 *  '*' representing any string are supported. Clients can pass an empty string if no filtering
	 *  is required.
	 * @param extension a user interface extension to the type selection dialog or <code>null</code>
	 *  if no extension is desired
	 *  
	 * @return a new selection dialog
	 * 
	 * @exception RubyModelException if the selection dialog could not be opened
	 * 
	 * @since 3.2
	 */
	public static SelectionDialog createTypeDialog(Shell parent, IRunnableContext context, IRubySearchScope scope, int elementKinds, 
			boolean multipleSelection, String filter, TypeSelectionExtension extension) throws RubyModelException {
		TypeSelectionDialog2 dialog= new TypeSelectionDialog2(parent, multipleSelection, 
			context, scope, elementKinds, extension);
		dialog.setMessage(RubyUIMessages.RubyUI_defaultDialogMessage); 
		dialog.setFilter(filter);
		return dialog;
	}
	
	/**
	 * Opens a Ruby editor on the given Ruby element. The element can be a ruby script. 
	 * If there already is an open Ruby editor for the given element, it is returned.
	 *
	 * @param element the input element; a ruby script 
	 *   (<code>IRubyScript</code>)
	 * @return the editor, or </code>null</code> if wrong element type or opening failed
	 * @exception PartInitException if the editor could not be initialized
	 * @exception RubyModelException if this element does not exist or if an
	 *		exception occurs while accessing its underlying resource
	 */
	public static IEditorPart openInEditor(IRubyElement element) throws RubyModelException, PartInitException {
		return EditorUtility.openInEditor(element);
	}
}