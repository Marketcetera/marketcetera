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
 
package org.rubypeople.rdt.internal.ui.text.template.contentassist;

import java.util.Iterator;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateVariableResolver;
import org.rubypeople.rdt.internal.corext.template.ruby.RubyContextType;

public class RubyTemplateVariableTextHover implements ITextHover {

	public RubyTemplateVariableTextHover() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHover#getHoverInfo(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
	 */
	public String getHoverInfo(ITextViewer textViewer, IRegion subject) {
		try {
			IDocument doc= textViewer.getDocument();
			int offset= subject.getOffset();
			if (offset >= 2 && "${".equals(doc.get(offset-2, 2))) { //$NON-NLS-1$
				String varName= doc.get(offset, subject.getLength());
				TemplateContextType contextType= RubyTemplateAccess.getDefault().getContextTypeRegistry().getContextType(RubyContextType.NAME);
				if (contextType != null) {
					Iterator iter= contextType.resolvers();
					while (iter.hasNext()) {
						TemplateVariableResolver var= (TemplateVariableResolver) iter.next();
						if (varName.equals(var.getType())) {
							return var.getDescription();
						}
					}
				}
			}				
		} catch (BadLocationException e) {
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		if (textViewer != null) {
			// FIXME Rewrite this! I just stole it from Ant!
			IDocument document= textViewer.getDocument();
			
			int start= -1;
			int end= -1;
			
			try {	
				int pos= offset;
				char c;
				
				while (pos >= 0) {
					c= document.getChar(pos);
					if (c != '.' && c != '-' && c != '/' &&  c != '\\' && !Character.isJavaIdentifierPart(c))
						break;
					--pos;
				}
				
				start= pos;
				
				pos= offset;
				int length= document.getLength();
				
				while (pos < length) {
					c= document.getChar(pos);
					if (c != '.' && c != '-' && !Character.isJavaIdentifierPart(c))
						break;
					++pos;
				}
				
				end= pos;
				
			} catch (BadLocationException x) {
			}
			
			if (start > -1 && end > -1) {
				if (start == offset && end == offset)
					return new Region(offset, 0);
				else if (start == offset)
					return new Region(start, end - start);
				else
					return new Region(start + 1, end - start - 1);
			}
			
			return null;
		}
		return null;	
	}
} 