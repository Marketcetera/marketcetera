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
package org.rubypeople.rdt.internal.ui.compare;

import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.text.IRubyPartitions;
import org.rubypeople.rdt.ui.text.RubySourceViewerConfiguration;
import org.rubypeople.rdt.ui.text.RubyTextTools;


public class RubyTextViewer extends Viewer {
		
	private SourceViewer fSourceViewer;
	private Object fInput;
	
	
	RubyTextViewer(Composite parent) {
		fSourceViewer= new SourceViewer(parent, null, SWT.LEFT_TO_RIGHT | SWT.H_SCROLL | SWT.V_SCROLL);
		RubyTextTools tools= RubyCompareUtilities.getRubyTextTools();
		if (tools != null) {
			IPreferenceStore store= RubyPlugin.getDefault().getCombinedPreferenceStore();
			fSourceViewer.configure(new RubySourceViewerConfiguration(tools.getColorManager(), store, null, IRubyPartitions.RUBY_PARTITIONING));
		}

		fSourceViewer.setEditable(false);
		
		String symbolicFontName= RubyMergeViewer.class.getName();
		Font font= JFaceResources.getFont(symbolicFontName);
		if (font != null)
			fSourceViewer.getTextWidget().setFont(font);
		
	}
		
	public Control getControl() {
		return fSourceViewer.getControl();
	}
	
	public void setInput(Object input) {
		if (input instanceof IStreamContentAccessor) {
			Document document= new Document(getString(input));
			RubyCompareUtilities.setupDocument(document);
			fSourceViewer.setDocument(document);
		}
		fInput= input;
	}
	
	public Object getInput() {
		return fInput;
	}
	
	public ISelection getSelection() {
		return null;
	}
	
	public void setSelection(ISelection s, boolean reveal) {
	}
	
	public void refresh() {
	}
	
	/**
	 * A helper method to retrieve the contents of the given object
	 * if it implements the IStreamContentAccessor interface.
	 */
	private static String getString(Object input) {
		
		if (input instanceof IStreamContentAccessor) {
			IStreamContentAccessor sca= (IStreamContentAccessor) input;
			try {
				return RubyCompareUtilities.readString(sca);
			} catch (CoreException ex) {
				RubyPlugin.log(ex);
			}
		}
		return ""; //$NON-NLS-1$
	}
}

