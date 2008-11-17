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
package org.rubypeople.rdt.internal.ui.text.ruby.hover;

import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBufferManager;

import org.eclipse.jface.preference.IPreferenceStore;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;

import org.eclipse.ui.editors.text.EditorsUI;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;

import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyAnnotationIterator;
import org.rubypeople.rdt.internal.ui.text.HTMLPrinter;

/**
 * Abstract super class for annotation hovers.
 * 
 * @since 3.0
 */
public abstract class AbstractAnnotationHover extends AbstractRubyEditorTextHover {

	private IPreferenceStore fStore= RubyPlugin.getDefault().getCombinedPreferenceStore();
	private DefaultMarkerAnnotationAccess fAnnotationAccess= new DefaultMarkerAnnotationAccess();
	private boolean fAllAnnotations;
	
	
	public AbstractAnnotationHover(boolean allAnnotations) {
		fAllAnnotations= allAnnotations;
	}
	
	/*
	 * Formats a message as HTML text.
	 */
	private String formatMessage(String message) {
		StringBuffer buffer= new StringBuffer();
		HTMLPrinter.addPageProlog(buffer);
		HTMLPrinter.addParagraph(buffer, HTMLPrinter.convertToHTMLContent(message));
		HTMLPrinter.addPageEpilog(buffer);
		return buffer.toString();
	}
	
	/*
	 * @see ITextHover#getHoverInfo(ITextViewer, IRegion)
	 */
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		IPath path;
		IAnnotationModel model;
		if (textViewer instanceof ISourceViewer) {
			path= null;
			model= ((ISourceViewer)textViewer).getAnnotationModel();
		} else {
			// Get annotation model from file buffer manager
			path= getEditorInputPath();
			model= getAnnotationModel(path);
		}
		if (model == null)
			return null;

		try {
			Iterator e= new RubyAnnotationIterator(model, true, fAllAnnotations);
			int layer= -1;
			String message= null;
			while (e.hasNext()) {
				Annotation a= (Annotation) e.next();

				AnnotationPreference preference= getAnnotationPreference(a);
				if (preference == null || !(preference.getTextPreferenceKey() != null && fStore.getBoolean(preference.getTextPreferenceKey()) || (preference.getHighlightPreferenceKey() != null && fStore.getBoolean(preference.getHighlightPreferenceKey()))))
					continue;

				Position p= model.getPosition(a);
				
				int l= fAnnotationAccess.getLayer(a);
				
				if (l > layer && p != null && p.overlapsWith(hoverRegion.getOffset(), hoverRegion.getLength())) {
					String msg= a.getText();
					if (msg != null && msg.trim().length() > 0) {
						message= msg;
						layer= l;
					}
				}
			}
			if (layer > -1)
				return formatMessage(message);
			
		} finally {
			try {
				if (path != null) {
					ITextFileBufferManager manager= FileBuffers.getTextFileBufferManager();
					manager.disconnect(path, null);
				}
			} catch (CoreException ex) {
				RubyPlugin.log(ex.getStatus());
			}
		}
		
		return null;
	}
	
	private IPath getEditorInputPath() {
		if (getEditor() == null)
			return null;
		
		IEditorInput input= getEditor().getEditorInput();
		if (input instanceof IStorageEditorInput) {
			try {
				return ((IStorageEditorInput)input).getStorage().getFullPath();
			} catch (CoreException ex) {
				RubyPlugin.log(ex.getStatus());
			}
		}
		return null;
	}
	
	private IAnnotationModel getAnnotationModel(IPath path) {
		if (path == null)
			return null;
		
		ITextFileBufferManager manager= FileBuffers.getTextFileBufferManager();
		try {
			manager.connect(path, null);
		} catch (CoreException ex) {
			RubyPlugin.log(ex.getStatus());
			return null;
		}
		
		IAnnotationModel model= null;
		try {
			model= manager.getTextFileBuffer(path).getAnnotationModel();
			return model;
		} finally {
			if (model == null) {
				try {
					manager.disconnect(path, null);
				} catch (CoreException ex) {
					RubyPlugin.log(ex.getStatus());
				}
			}
		}
	}

	/**
	 * Returns the annotation preference for the given annotation.
	 *
	 * @param annotation the annotation
	 * @return the annotation preference or <code>null</code> if none
	 */	
	private AnnotationPreference getAnnotationPreference(Annotation annotation) {
		
		if (annotation.isMarkedDeleted())
			return null;
		return EditorsUI.getAnnotationPreferenceLookup().getAnnotationPreference(annotation);
	}
}
