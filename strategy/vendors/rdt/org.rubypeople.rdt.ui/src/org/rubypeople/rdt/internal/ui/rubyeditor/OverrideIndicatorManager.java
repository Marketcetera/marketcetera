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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.ISynchronizable;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.ui.PartInitException;
import org.jruby.ast.Node;
import org.jruby.ast.RootNode;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.ITypeHierarchy;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.util.Messages;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.actions.OpenActionUtil;
import org.rubypeople.rdt.internal.ui.text.ruby.IRubyReconcilingListener;

/**
 * Manages the override and overwrite indicators for
 * the given Ruby element and annotation model.
 *
 * @since 3.0
 */
class OverrideIndicatorManager implements IRubyReconcilingListener {

	/**
	 * Overwrite and override indicator annotation.
	 *
	 * @since 3.0
	 */
	class OverrideIndicator extends Annotation {

		private boolean fIsOverwriteIndicator;
		private String fHandleIdentifier;

		/**
		 * Creates a new override annotation.
		 *
		 * @param isOverwriteIndicator <code>true</code> if this annotation is
		 *            an overwrite indicator, <code>false</code> otherwise
		 * @param text the text associated with this annotation
		 * @param key the method binding key
		 * @since 3.0
		 */
		OverrideIndicator(boolean isOverwriteIndicator, String text, String handleIdentifier) {
			super(ANNOTATION_TYPE, false, text);
			fIsOverwriteIndicator= isOverwriteIndicator;
			fHandleIdentifier= handleIdentifier;
		}

		/**
		 * Tells whether this is an overwrite or an override indicator.
		 *
		 * @return <code>true</code> if this is an overwrite indicator
		 */
		public boolean isOverwriteIndicator() {
			return fIsOverwriteIndicator;
		}

		/**
		 * Opens and reveals the defining method.
		 */
		public void open() {
			try {
				OpenActionUtil.open(RubyCore.create(fHandleIdentifier), true);
			} catch (PartInitException e) {
				RubyPlugin.log(e);
			} catch (RubyModelException e) {
				RubyPlugin.log(e);
			}
		}
	}

	static final String ANNOTATION_TYPE= "org.rubypeople.rdt.ui.overrideIndicator"; //$NON-NLS-1$

	private IAnnotationModel fAnnotationModel;
	private Object fAnnotationModelLockObject;
	private Annotation[] fOverrideAnnotations;

	public OverrideIndicatorManager(IAnnotationModel annotationModel, IRubyElement rubyElement, Node ast) {
		Assert.isNotNull(annotationModel);
		Assert.isNotNull(rubyElement);

		fAnnotationModel=annotationModel;
		fAnnotationModelLockObject= getLockObject(fAnnotationModel);
		
		if (ast != null)
			updateAnnotations((IRubyScript) rubyElement, new NullProgressMonitor());
	}

	/**
	 * Returns the lock object for the given annotation model.
	 *
	 * @param annotationModel the annotation model
	 * @return the annotation model's lock object
	 * @since 3.0
	 */
	private Object getLockObject(IAnnotationModel annotationModel) {
		if (annotationModel instanceof ISynchronizable) {
			Object lock= ((ISynchronizable)annotationModel).getLockObject();
			if (lock != null)
				return lock;
		}
		return annotationModel;
	}

	/**
	 * Updates the override and implements annotations based
	 * on the given AST.
	 *
	 * @param ast the compilation unit AST
	 * @param progressMonitor the progress monitor
	 * @since 3.0
	 */
	protected void updateAnnotations(IRubyScript ast, IProgressMonitor progressMonitor) {

		if (ast == null || progressMonitor.isCanceled())
			return;
		
		final Map annotationMap= new HashMap(50);
		try {
			IType[] types = ast.getAllTypes();
			for (int i = 0; i < types.length; i++) {
				IMethod[] methods = types[i].getMethods();			
				List<IMethod> filtered = filterToPublic(methods);
				if (filtered.isEmpty()) continue;
				ITypeHierarchy hierarchy = types[i].newSupertypeHierarchy(new NullProgressMonitor());
				if (hierarchy != null) {
					IType[] supers = hierarchy.getAllTypes();
					for (IMethod method : filtered) { // FIXME Clean this up! God it makes me want to vomit!
						for (int k = 0; k < supers.length; k++) {
							if (supers[k].equals(types[i])) continue; // skip original type
							IMethod[] superMethods = supers[k].getMethods();
							for (int l = 0; l < superMethods.length; l++) {
								IMethod overridenMethod = superMethods[l];					
								if (!overridenMethod.getElementName().equals(method.getElementName())) continue;
								Position position= new Position(method.getSourceRange().getOffset(), method.getSourceRange().getLength());
								String qualifiedMethodName= overridenMethod.getDeclaringType().getFullyQualifiedName() + "." + overridenMethod.getElementName(); //$NON-NLS-1$
								String text = Messages.format(RubyEditorMessages.OverrideIndicatorManager_overrides, qualifiedMethodName);
								annotationMap.put(
										new OverrideIndicator(false, text, overridenMethod.getHandleIdentifier()), 
										position);
							}				
						}
					}
				}
			}
		} catch (RubyModelException e) {
			RubyPlugin.log(e);
		}

		if (progressMonitor.isCanceled())
			return;

		synchronized (fAnnotationModelLockObject) {
			if (fAnnotationModel instanceof IAnnotationModelExtension) {
				((IAnnotationModelExtension)fAnnotationModel).replaceAnnotations(fOverrideAnnotations, annotationMap);
			} else {
				removeAnnotations();
				Iterator iter= annotationMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry mapEntry= (Map.Entry)iter.next();
					fAnnotationModel.addAnnotation((Annotation)mapEntry.getKey(), (Position)mapEntry.getValue());
				}
			}
			fOverrideAnnotations= (Annotation[])annotationMap.keySet().toArray(new Annotation[annotationMap.keySet().size()]);
		}
	}

	private List<IMethod> filterToPublic(IMethod[] methods) {
		List<IMethod> filtered = new ArrayList<IMethod>();
		if (methods == null || methods.length == 0) return filtered;
		for (int i = 0; i < methods.length; i++) {
			try {
				if (!methods[i].isPublic()) continue;
				filtered.add(methods[i]);
			} catch (RubyModelException e) {
				RubyPlugin.log(e);
			}
		}
		return filtered;
	}

	/**
	 * Removes all override indicators from this manager's annotation model.
	 */
	void removeAnnotations() {
		if (fOverrideAnnotations == null)
			return;

		synchronized (fAnnotationModelLockObject) {
			if (fAnnotationModel instanceof IAnnotationModelExtension) {
				((IAnnotationModelExtension)fAnnotationModel).replaceAnnotations(fOverrideAnnotations, null);
			} else {
				for (int i= 0, length= fOverrideAnnotations.length; i < length; i++)
					fAnnotationModel.removeAnnotation(fOverrideAnnotations[i]);
			}
			fOverrideAnnotations= null;
		}
	}

	/*
	 * @see org.rubypeople.rdt.internal.ui.text.ruby.IRubyReconcilingListener#aboutToBeReconciled()
	 */
	public void aboutToBeReconciled() {
	}

	/*
	 * @see org.rubypeople.rdt.internal.ui.text.ruby.IRubyReconcilingListener#reconciled(RootNode, boolean, IProgressMonitor)
	 */
	public void reconciled(IRubyScript script, RootNode ast, boolean forced, IProgressMonitor progressMonitor) {
		updateAnnotations(script, progressMonitor);
	}
}

