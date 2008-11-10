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
package org.rubypeople.rdt.internal.ui.text.ruby.hover;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;
import org.rubypeople.rdt.ui.text.ruby.hover.IRubyEditorTextHover;


public class RubyTypeHover implements IRubyEditorTextHover {

	private IRubyEditorTextHover fProblemHover;
	private IRubyEditorTextHover fRubydocHover;

	public RubyTypeHover() {
		fProblemHover= new ProblemHover();
		fRubydocHover= new CommentHoverProvider();
	}

	/*
	 * @see IRubyEditorTextHover#setEditor(IEditorPart)
	 */
	public void setEditor(IEditorPart editor) {
		fProblemHover.setEditor(editor);
		fRubydocHover.setEditor(editor);
	}

	/*
	 * @see ITextHover#getHoverRegion(ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return fRubydocHover.getHoverRegion(textViewer, offset);
	}

	/*
	 * @see ITextHover#getHoverInfo(ITextViewer, IRegion)
	 */
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		String hoverInfo= fProblemHover.getHoverInfo(textViewer, hoverRegion);
		if (hoverInfo != null)
			return hoverInfo;

		return fRubydocHover.getHoverInfo(textViewer, hoverRegion);
	}
}
