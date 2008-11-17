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

package org.rubypeople.rdt.internal.ui.search;

import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.swt.graphics.Image;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;

class OccurrencesSearchLabelProvider extends TextSearchLabelProvider {
	
	public OccurrencesSearchLabelProvider(AbstractTextSearchViewPage page) {
		super(page);
	}

	protected String doGetText(Object element) {
		RubyElementLine jel= (RubyElementLine) element;
		return jel.getLineContents().replace('\t', ' ');
	}
	
	public Image getImage(Object element) {
		if (element instanceof OccurrencesGroupKey) {
			OccurrencesGroupKey group= (OccurrencesGroupKey) element;
			if (group.isVariable()) {
				if (group.isWriteAccess())
					return RubyPluginImages.get(RubyPluginImages.IMG_OBJS_SEARCH_WRITEACCESS);
				else
					return RubyPluginImages.get(RubyPluginImages.IMG_OBJS_SEARCH_READACCESS);
			}
			
		}		
		return RubyPluginImages.get(RubyPluginImages.IMG_OBJS_SEARCH_OCCURRENCE);
	}
}
