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
package org.rubypeople.rdt.internal.ui.text.correction;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationAccessExtension;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationPresentation;
import org.eclipse.jface.text.source.ImageUtilities;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jruby.ast.RootNode;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.rubyeditor.ASTProvider;
import org.rubypeople.rdt.internal.ui.viewsupport.ISelectionListenerWithAST;
import org.rubypeople.rdt.internal.ui.viewsupport.SelectionListenerWithASTManager;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.RubyUI;
import org.rubypeople.rdt.ui.text.ruby.IInvocationContext;

/**
 *
 */
public class QuickAssistLightBulbUpdater {

	public static class AssistAnnotation extends Annotation implements IAnnotationPresentation {

		//XXX: To be fully correct this should be a non-static fields in QuickAssistLightBulbUpdater
		private static final int LAYER;

		static {
			Annotation annotation= new Annotation("org.rubypeople.rdt.ui.warning", false, null); //$NON-NLS-1$
			AnnotationPreference preference= EditorsUI.getAnnotationPreferenceLookup().getAnnotationPreference(annotation);
			if (preference != null)
				LAYER= preference.getPresentationLayer() - 1;
			else
				LAYER= IAnnotationAccessExtension.DEFAULT_LAYER;

		}

		private Image fImage;

		public AssistAnnotation() {
		}

		/*
		 * @see org.eclipse.jface.text.source.IAnnotationPresentation#getLayer()
		 */
		public int getLayer() {
			return LAYER;
		}

		private Image getImage() {
			if (fImage == null) {
				fImage= RubyPluginImages.get(RubyPluginImages.IMG_OBJS_QUICK_ASSIST);
			}
			return fImage;
		}

		/* (non-Rubydoc)
		 * @see org.eclipse.jface.text.source.Annotation#paint(org.eclipse.swt.graphics.GC, org.eclipse.swt.widgets.Canvas, org.eclipse.swt.graphics.Rectangle)
		 */
		public void paint(GC gc, Canvas canvas, Rectangle r) {
			ImageUtilities.drawImage(getImage(), gc, canvas, r, SWT.CENTER, SWT.TOP);
		}

	}

	private final Annotation fAnnotation;
	private boolean fIsAnnotationShown;
	private ITextEditor fEditor;
	private ITextViewer fViewer;

	private ISelectionListenerWithAST fListener;
	private IPropertyChangeListener fPropertyChangeListener;

	public QuickAssistLightBulbUpdater(ITextEditor part, ITextViewer viewer) {
		fEditor= part;
		fViewer= viewer;
		fAnnotation= new AssistAnnotation();
		fIsAnnotationShown= false;
		fPropertyChangeListener= null;
	}

	public boolean isSetInPreferences() {
		return PreferenceConstants.getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_QUICKASSIST_LIGHTBULB);
	}

	private void installSelectionListener() {
		fListener= new ISelectionListenerWithAST() {
			public void selectionChanged(IEditorPart part, ITextSelection selection, RootNode astRoot) {
				doSelectionChanged(selection.getOffset(), selection.getLength(), astRoot);
			}
		};
		SelectionListenerWithASTManager.getDefault().addListener(fEditor, fListener);
	}

	private void uninstallSelectionListener() {
		if (fListener != null) {
			SelectionListenerWithASTManager.getDefault().removeListener(fEditor, fListener);
			fListener= null;
		}
		IAnnotationModel model= getAnnotationModel();
		if (model != null) {
			removeLightBulb(model);
		}
	}

	public void install() {
		if (isSetInPreferences()) {
			installSelectionListener();
		}
		if (fPropertyChangeListener == null) {
			fPropertyChangeListener= new IPropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent event) {
					doPropertyChanged(event.getProperty());
				}
			};
			PreferenceConstants.getPreferenceStore().addPropertyChangeListener(fPropertyChangeListener);
		}
	}

	public void uninstall() {
		uninstallSelectionListener();
		if (fPropertyChangeListener != null) {
			PreferenceConstants.getPreferenceStore().removePropertyChangeListener(fPropertyChangeListener);
			fPropertyChangeListener= null;
		}
	}

	protected void doPropertyChanged(String property) {
		if (property.equals(PreferenceConstants.EDITOR_QUICKASSIST_LIGHTBULB)) {
			if (isSetInPreferences()) {
				IRubyScript cu= getRubyScript();
				if (cu != null) {
					installSelectionListener();
					Point point= fViewer.getSelectedRange();
					RootNode astRoot= (RootNode) ASTProvider.getASTProvider().getAST(cu, ASTProvider.WAIT_ACTIVE_ONLY, null);
					if (astRoot != null) {
						doSelectionChanged(point.x, point.y, astRoot);
					}
				}
			} else {
				uninstallSelectionListener();
			}
		}
	}

	private IRubyScript getRubyScript() {
		IRubyElement elem= RubyUI.getEditorInputRubyElement(fEditor.getEditorInput());
		if (elem instanceof IRubyScript) {
			return (IRubyScript) elem;
		}
		return null;
	}

	private IAnnotationModel getAnnotationModel() {
		return RubyUI.getDocumentProvider().getAnnotationModel(fEditor.getEditorInput());
	}

	private IDocument getDocument() {
		return RubyUI.getDocumentProvider().getDocument(fEditor.getEditorInput());
	}


	private void doSelectionChanged(int offset, int length, RootNode astRoot) {

		final IAnnotationModel model= getAnnotationModel();
		final IRubyScript cu= getRubyScript();
		if (model == null || cu == null) {
			return;
		}

		final AssistContext context= new AssistContext(cu, offset, length);
		context.setASTRoot(astRoot);

		boolean hasQuickFix= hasQuickFixLightBulb(model, context.getSelectionOffset());
		if (hasQuickFix) {
			removeLightBulb(model);
			return; // there is already a quick fix light bulb at the new location
		}

		calculateLightBulb(model, context);
	}

	/*
	 * Needs to be called synchronized
	 */
	private void calculateLightBulb(IAnnotationModel model, IInvocationContext context) {
		boolean needsAnnotation= RubyCorrectionProcessor.hasAssists(context);
		if (fIsAnnotationShown) {
			model.removeAnnotation(fAnnotation);
		}
		if (needsAnnotation) {
			model.addAnnotation(fAnnotation, new Position(context.getSelectionOffset(), context.getSelectionLength()));
		}
		fIsAnnotationShown= needsAnnotation;
	}

	private void removeLightBulb(IAnnotationModel model) {
		synchronized (this) {
			if (fIsAnnotationShown) {
				model.removeAnnotation(fAnnotation);
				fIsAnnotationShown= false;
			}
		}
	}

	/*
	 * Tests if there is already a quick fix light bulb on the current line
	 */
	private boolean hasQuickFixLightBulb(IAnnotationModel model, int offset) {
		try {
			IDocument document= getDocument();
			if (document == null) {
				return false;
			}

			// we access a document and annotation model from within a job
			// since these are only read accesses, we won't hurt anyone else if
			// this goes boink

			// may throw an IndexOutOfBoundsException upon concurrent document modification
			int currLine= document.getLineOfOffset(offset);

			// this iterator is not protected, it may throw ConcurrentModificationExceptions
			Iterator iter= model.getAnnotationIterator();
			while (iter.hasNext()) {
				Annotation annot= (Annotation) iter.next();
				if (RubyCorrectionProcessor.isQuickFixableType(annot)) {
					// may throw an IndexOutOfBoundsException upon concurrent annotation model changes
					Position pos= model.getPosition(annot);
					if (pos != null) {
						// may throw an IndexOutOfBoundsException upon concurrent document modification
						int startLine= document.getLineOfOffset(pos.getOffset());
						if (startLine == currLine && RubyCorrectionProcessor.hasCorrections(annot)) {
							return true;
						}
					}
				}
			}
		} catch (BadLocationException e) {
			// ignore
		} catch (IndexOutOfBoundsException e) {
			// concurrent modification - too bad, ignore
		} catch (ConcurrentModificationException e) {
			// concurrent modification - too bad, ignore
		}
		return false;
	}


}
