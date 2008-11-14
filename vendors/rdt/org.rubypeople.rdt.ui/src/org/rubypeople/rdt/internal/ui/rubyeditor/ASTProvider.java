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

package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.Assert;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jruby.ast.Node;
import org.jruby.ast.RootNode;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceReference;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.core.util.ASTUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.ui.RubyUI;


/**
 * Provides a shared AST for clients. The shared AST is
 * the AST of the active Ruby editor's input element.
 *
 * @since 1.0
 */
public final class ASTProvider {

	/**
	 * Wait flag.
	 *
	 * @since 3.1
	 */
	public static final class WAIT_FLAG {

		String fName;

		private WAIT_FLAG(String name) {
			fName= name;
		}

		/*
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return fName;
		}
	}

	/**
	 * Wait flag indicating that a client requesting an AST
	 * wants to wait until an AST is ready.
	 * <p>
	 * An AST will be created by this AST provider if the shared
	 * AST is not for the given java element.
	 * </p>
	 *
	 * @since 3.1
	 */
	public static final WAIT_FLAG WAIT_YES= new WAIT_FLAG("wait yes"); //$NON-NLS-1$

	/**
	 * Wait flag indicating that a client requesting an AST
	 * only wants to wait for the shared AST of the active editor.
	 * <p>
	 * No AST will be created by the AST provider.
	 * </p>
	 *
	 * @since 3.1
	 */
	public static final WAIT_FLAG WAIT_ACTIVE_ONLY= new WAIT_FLAG("wait active only"); //$NON-NLS-1$

	/**
	 * Wait flag indicating that a client requesting an AST
	 * only wants the already available shared AST.
	 * <p>
	 * No AST will be created by the AST provider.
	 * </p>
	 *
	 * @since 3.1
	 */
	public static final WAIT_FLAG WAIT_NO= new WAIT_FLAG("don't wait"); //$NON-NLS-1$


	/**
	 * Tells whether this class is in debug mode.
	 * @since 3.0
	 */
	private static final boolean DEBUG= "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jdt.ui/debug/ASTProvider"));  //$NON-NLS-1$//$NON-NLS-2$


	/**
	 * Internal activation listener.
	 *
	 * @since 3.0
	 */
	private class ActivationListener implements IPartListener2, IWindowListener {


		/*
		 * @see org.eclipse.ui.IPartListener2#partActivated(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partActivated(IWorkbenchPartReference ref) {
			if (isRubyEditor(ref) && !isActiveEditor(ref))
				activeRubyEditorChanged(ref.getPart(true));
		}

		/*
		 * @see org.eclipse.ui.IPartListener2#partBroughtToTop(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partBroughtToTop(IWorkbenchPartReference ref) {
			if (isRubyEditor(ref) && !isActiveEditor(ref))
				activeRubyEditorChanged(ref.getPart(true));
		}

		/*
		 * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partClosed(IWorkbenchPartReference ref) {
			if (isActiveEditor(ref)) {
				if (DEBUG)
					System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "closed active editor: " + ref.getTitle()); //$NON-NLS-1$ //$NON-NLS-2$

				activeRubyEditorChanged(null);
			}
		}

		/*
		 * @see org.eclipse.ui.IPartListener2#partDeactivated(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partDeactivated(IWorkbenchPartReference ref) {
		}

		/*
		 * @see org.eclipse.ui.IPartListener2#partOpened(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partOpened(IWorkbenchPartReference ref) {
			if (isRubyEditor(ref) && !isActiveEditor(ref))
				activeRubyEditorChanged(ref.getPart(true));
		}

		/*
		 * @see org.eclipse.ui.IPartListener2#partHidden(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partHidden(IWorkbenchPartReference ref) {
		}

		/*
		 * @see org.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partVisible(IWorkbenchPartReference ref) {
			if (isRubyEditor(ref) && !isActiveEditor(ref))
				activeRubyEditorChanged(ref.getPart(true));
		}

		/*
		 * @see org.eclipse.ui.IPartListener2#partInputChanged(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partInputChanged(IWorkbenchPartReference ref) {
			if (isRubyEditor(ref) && isActiveEditor(ref))
				activeRubyEditorChanged(ref.getPart(true));
		}

		/*
		 * @see org.eclipse.ui.IWindowListener#windowActivated(org.eclipse.ui.IWorkbenchWindow)
		 */
		public void windowActivated(IWorkbenchWindow window) {
			IWorkbenchPartReference ref= window.getPartService().getActivePartReference();
			if (isRubyEditor(ref) && !isActiveEditor(ref))
				activeRubyEditorChanged(ref.getPart(true));
		}

		/*
		 * @see org.eclipse.ui.IWindowListener#windowDeactivated(org.eclipse.ui.IWorkbenchWindow)
		 */
		public void windowDeactivated(IWorkbenchWindow window) {
		}

		/*
		 * @see org.eclipse.ui.IWindowListener#windowClosed(org.eclipse.ui.IWorkbenchWindow)
		 */
		public void windowClosed(IWorkbenchWindow window) {
			if (fActiveEditor != null && fActiveEditor.getSite() != null && window == fActiveEditor.getSite().getWorkbenchWindow()) {
				if (DEBUG)
					System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "closed active editor: " + fActiveEditor.getTitle()); //$NON-NLS-1$ //$NON-NLS-2$

				activeRubyEditorChanged(null);
			}
			window.getPartService().removePartListener(this);
		}

		/*
		 * @see org.eclipse.ui.IWindowListener#windowOpened(org.eclipse.ui.IWorkbenchWindow)
		 */
		public void windowOpened(IWorkbenchWindow window) {
			window.getPartService().addPartListener(this);
		}

		private boolean isActiveEditor(IWorkbenchPartReference ref) {
			return ref != null && isActiveEditor(ref.getPart(false));
		}

		private boolean isActiveEditor(IWorkbenchPart part) {
			return part != null && (part == fActiveEditor);
		}

		private boolean isRubyEditor(IWorkbenchPartReference ref) {
			if (ref == null)
				return false;

			String id= ref.getId();

			// The instanceof check is not need but helps clients, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=84862
			return RubyUI.ID_RUBY_EDITOR.equals(id) || RubyUI.ID_EXTERNAL_EDITOR.equals(id) || ref.getPart(false) instanceof RubyEditor;
		}
	}

	public static final boolean SHARED_AST_STATEMENT_RECOVERY= true;

	private static final String DEBUG_PREFIX= "ASTProvider > "; //$NON-NLS-1$


	private IRubyElement fReconcilingRubyElement;
	private IRubyElement fActiveRubyElement;
	private RootNode fAST;
	private ActivationListener fActivationListener;
	private Object fReconcileLock= new Object();
	private Object fWaitLock= new Object();
	private boolean fIsReconciling;
	private IWorkbenchPart fActiveEditor;

	
	/**
	 * Returns the Ruby plug-in's AST provider.
	 * 
	 * @return the AST provider
	 * @since 3.2
	 */
	public static ASTProvider getASTProvider() {
		return RubyPlugin.getDefault().getASTProvider();
	}
	
	/**
	 * Creates a new AST provider.
	 */
	public ASTProvider() {
		install();
	}

	/**
	 * Installs this AST provider.
	 */
	void install() {
		// Create and register activation listener
		fActivationListener= new ActivationListener();
		PlatformUI.getWorkbench().addWindowListener(fActivationListener);

		// Ensure existing windows get connected
		IWorkbenchWindow[] windows= PlatformUI.getWorkbench().getWorkbenchWindows();
		for (int i= 0, length= windows.length; i < length; i++)
			windows[i].getPartService().addPartListener(fActivationListener);
	}

	private void activeRubyEditorChanged(IWorkbenchPart editor) {

		IRubyElement rubyElement= null;
		if (editor instanceof RubyEditor)
			rubyElement= ((RubyEditor)editor).getInputRubyElement();

		synchronized (this) {
			fActiveEditor= editor;
			fActiveRubyElement= rubyElement;
			cache(null, rubyElement);
		}

		if (DEBUG)
			System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "active editor is: " + toString(rubyElement)); //$NON-NLS-1$ //$NON-NLS-2$

		synchronized (fReconcileLock) {
			if (fIsReconciling && (fReconcilingRubyElement == null || !fReconcilingRubyElement.equals(rubyElement))) {
				fIsReconciling= false;
				fReconcilingRubyElement= null;
			} else if (rubyElement == null) {
				fIsReconciling= false;
				fReconcilingRubyElement= null;
			}
		}
	}

	/**
	 * Returns whether the given compilation unit AST is
	 * cached by this AST provided.
	 *
	 * @param ast the compilation unit AST
	 * @return <code>true</code> if the given AST is the cached one
	 */
	public boolean isCached(Node ast) {
		return ast != null && fAST == ast;
	}

	/**
	 * Returns whether this AST provider is active on the given
	 * compilation unit.
	 *
	 * @param cu the compilation unit
	 * @return <code>true</code> if the given compilation unit is the active one
	 * @since 3.1
	 */
	public boolean isActive(IRubyScript cu) {
		return cu != null && cu.equals(fActiveRubyElement);
	}

	/**
	 * Informs that reconciling for the given element is about to be started.
	 *
	 * @param rubyElement the Ruby element
	 * @see org.rubypeople.rdt.internal.ui.text.ruby.IRubyReconcilingListener#aboutToBeReconciled()
	 */
	void aboutToBeReconciled(IRubyElement rubyElement) {

		if (rubyElement == null)
			return;

		if (DEBUG)
			System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "about to reconcile: " + toString(rubyElement)); //$NON-NLS-1$ //$NON-NLS-2$

		synchronized (fReconcileLock) {
			fIsReconciling= true;
			fReconcilingRubyElement= rubyElement;
		}
		cache(null, rubyElement);
	}

	/**
	 * Disposes the cached AST.
	 */
	private synchronized void disposeAST() {

		if (fAST == null)
			return;

		if (DEBUG)
			System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "disposing AST: " + toString(fAST) + " for: " + toString(fActiveRubyElement)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		fAST= null;

		cache(null, null);
	}

	/**
	 * Returns a string for the given Ruby element used for debugging.
	 *
	 * @param javaElement the compilation unit AST
	 * @return a string used for debugging
	 */
	private String toString(IRubyElement javaElement) {
		if (javaElement == null)
			return "null"; //$NON-NLS-1$
		else
			return javaElement.getElementName();

	}

	/**
	 * Returns a string for the given AST used for debugging.
	 *
	 * @param ast the compilation unit AST
	 * @return a string used for debugging
	 */
	private String toString(Node ast) {
		if (ast == null)
			return "null"; //$NON-NLS-1$

		return ASTUtil.stringRepresentation(ast);
	}

	/**
	 * Caches the given compilation unit AST for the given Ruby element.
	 *
	 * @param ast
	 * @param javaElement
	 */
	private synchronized void cache(RootNode ast, IRubyElement javaElement) {

		if (fActiveRubyElement != null && !fActiveRubyElement.equals(javaElement)) {
			if (DEBUG && javaElement != null) // don't report call from disposeAST()
				System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "don't cache AST for inactive: " + toString(javaElement)); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}

		if (DEBUG && (javaElement != null || ast != null)) // don't report call from disposeAST()
			System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "caching AST: " + toString(ast) + " for: " + toString(javaElement)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		if (fAST != null)
			disposeAST();

		fAST= ast;

		// Signal AST change
		synchronized (fWaitLock) {
			fWaitLock.notifyAll();
		}
	}

	/**
	 * Returns a shared compilation unit AST for the given
	 * Ruby element.
	 * <p>
	 * Clients are not allowed to modify the AST and must
	 * synchronize all access to its nodes.
	 * </p>
	 *
	 * @param re				the Ruby element
	 * @param waitFlag			{@link #WAIT_YES}, {@link #WAIT_NO} or {@link #WAIT_ACTIVE_ONLY}
	 * @param progressMonitor	the progress monitor or <code>null</code>
	 * @return					the AST or <code>null</code> if the AST is not available
	 */
	public RootNode getAST(IRubyElement re, WAIT_FLAG waitFlag, IProgressMonitor progressMonitor) {
		if (re == null)
			return null;
		
		Assert.isTrue(re.getElementType() == IRubyElement.SCRIPT);

		if (progressMonitor != null && progressMonitor.isCanceled())
			return null;

		boolean isActiveElement;
		synchronized (this) {
			isActiveElement= re.equals(fActiveRubyElement);
			if (isActiveElement) {
				if (fAST != null) {
					if (DEBUG)
						System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "returning cached AST:" + toString(fAST) + " for: " + re.getElementName()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

					return fAST;
				}
				if (waitFlag == WAIT_NO) {
					if (DEBUG)
						System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "returning null (WAIT_NO) for: " + re.getElementName()); //$NON-NLS-1$ //$NON-NLS-2$

					return null;

				}
			}
		}
		if (isActiveElement && isReconciling(re)) {
			try {
				final IRubyElement activeElement= fReconcilingRubyElement;

				// Wait for AST
				synchronized (fWaitLock) {
					if (DEBUG)
						System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "waiting for AST for: " + re.getElementName()); //$NON-NLS-1$ //$NON-NLS-2$

					fWaitLock.wait();
				}

				// Check whether active element is still valid
				synchronized (this) {
					if (activeElement == fActiveRubyElement && fAST != null) {
						if (DEBUG)
							System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "...got AST for: " + re.getElementName()); //$NON-NLS-1$ //$NON-NLS-2$

						return fAST;
					}
				}
				return getAST(re, waitFlag, progressMonitor);
			} catch (InterruptedException e) {
				return null; // thread has been interrupted don't compute AST
			}
		} else if (waitFlag == WAIT_NO || (waitFlag == WAIT_ACTIVE_ONLY && !(isActiveElement && fAST == null)))
			return null;

		if (isActiveElement)
			aboutToBeReconciled(re);

		RootNode ast= null;
		try {
			ast= createAST(re, progressMonitor);
			if (progressMonitor != null && progressMonitor.isCanceled())
				ast= null;
			else if (DEBUG && ast != null)
				System.err.println(getThreadName() + " - " + DEBUG_PREFIX + "created AST for: " + re.getElementName()); //$NON-NLS-1$ //$NON-NLS-2$
		} finally {
			if (isActiveElement) {
				if (fAST != null) {
					if (DEBUG)
						System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "Ignore created AST for " + re.getElementName() + "- AST from reconciler is newer"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					reconciled(fAST, re, null);
				} else
					reconciled(ast, re, null);
			}
		}

		return ast;
	}

	/**
	 * Tells whether the given Ruby element is the one
	 * reported as currently being reconciled.
	 *
	 * @param javaElement the Ruby element
	 * @return <code>true</code> if reported as currently being reconciled
	 */
	private boolean isReconciling(IRubyElement javaElement) {
		synchronized (fReconcileLock) {
			return javaElement != null && javaElement.equals(fReconcilingRubyElement) && fIsReconciling;
		}
	}

	/**
	 * Creates a new compilation unit AST.
	 *
	 * @param je the Ruby element for which to create the AST
	 * @param progressMonitor the progress monitor
	 * @return AST
	 */
	private RootNode createAST(IRubyElement je, final IProgressMonitor progressMonitor) {
		if (!hasSource(je))
			return null;
		
		if (progressMonitor != null && progressMonitor.isCanceled())
			return null;
		
		final RubyParser parser = new RubyParser();

		if (progressMonitor != null && progressMonitor.isCanceled())
			return null;
		
		if (je.getElementType() != IRubyElement.SCRIPT)
			return null;
		
		IRubyScript script = (IRubyScript) je;
		String source = null;
		try {
			source = script.getSource();
		} catch (RubyModelException e) {
			return null;
		}
		final String goodSource = source;

		if (progressMonitor != null && progressMonitor.isCanceled())
			return null;

		final RootNode root[]= new RootNode[1]; 
		
		SafeRunner.run(new ISafeRunnable() {
			public void run() {
				try {
					if (progressMonitor != null && progressMonitor.isCanceled())
						root[0]= null;
					root[0]= (RootNode) parser.parse(goodSource).getAST();
				} catch (OperationCanceledException ex) {
					root[0]= null;
				} catch (SyntaxException ex) {
					root[0]= null;
				}
			}
			public void handleException(Throwable ex) {
				IStatus status= new Status(IStatus.ERROR, RubyUI.ID_PLUGIN, IStatus.OK, "Error in RDT Core during AST creation", ex);  //$NON-NLS-1$
				RubyPlugin.getDefault().getLog().log(status);
			}
		});
		
		// mark as unmodifiable
//		if (root[0] != null)
//			ASTNodes.setFlagsToAST(root[0], ASTNode.PROTECT);
		
		return root[0];
	}
	
	/**
	 * Checks whether the given Ruby element has accessible source.
	 * 
	 * @param re the Ruby element to test
	 * @return <code>true</code> if the element has source
	 * @since 3.2
	 */
	private boolean hasSource(IRubyElement re) {
		if (re == null || !re.exists())
			return false;
		
		try {
			return re instanceof ISourceReference && ((ISourceReference)re).getSource() != null;
		} catch (RubyModelException ex) {
			IStatus status= new Status(IStatus.ERROR, RubyUI.ID_PLUGIN, IStatus.OK, "Error in RDT Core during AST creation", ex);  //$NON-NLS-1$
			RubyPlugin.getDefault().getLog().log(status);
		}
		return false;
	}
	
	/**
	 * Disposes this AST provider.
	 */
	public void dispose() {

		// Dispose activation listener
		PlatformUI.getWorkbench().removeWindowListener(fActivationListener);
		fActivationListener= null;

		disposeAST();

		synchronized (fWaitLock) {
			fWaitLock.notifyAll();
		}
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.text.java.IRubyReconcilingListener#reconciled(org.eclipse.jdt.core.dom.CompilationUnit)
	 */
	void reconciled(RootNode ast, IRubyElement javaElement, IProgressMonitor progressMonitor) {

		if (DEBUG)
			System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "reconciled: " + toString(javaElement) + ", AST: " + toString(ast)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		synchronized (fReconcileLock) {

			fIsReconciling= progressMonitor != null && progressMonitor.isCanceled();
			if (javaElement == null || !javaElement.equals(fReconcilingRubyElement)) {

				if (DEBUG)
					System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "  ignoring AST of out-dated editor"); //$NON-NLS-1$ //$NON-NLS-2$

				// Signal - threads might wait for wrong element
				synchronized (fWaitLock) {
					fWaitLock.notifyAll();
				}

				return;
			}

			cache(ast, javaElement);
		}
	}

	private String getThreadName() {
		String name= Thread.currentThread().getName();
		if (name != null)
			return name;
		else
			return Thread.currentThread().toString();
	}
	
}

