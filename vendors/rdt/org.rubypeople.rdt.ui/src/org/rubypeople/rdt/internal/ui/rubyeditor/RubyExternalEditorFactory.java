/*
 * Author: Markus Barchfeld
 * 
 * Copyright (c) 2004 RubyPeople.
 * 
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. RDT is
 * subject to the "Common Public License (CPL) v 1.0". You may not use RDT except in 
 * compliance with the License. For further information see org.rubypeople.rdt/rdt.license.
 */

package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.io.File;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

public class RubyExternalEditorFactory implements IElementFactory {

    public final static String MEMENTO_ABSOLUTE_PATH_KEY = "path" ;
	public static final String FACTORY_ID = RubyPlugin.PLUGIN_ID + ".externalRubyFileEditorInputFactory"; //$NON-NLS-1$

    /* (non-Javadoc)
     * @see org.eclipse.ui.IElementFactory#createElement(org.eclipse.ui.IMemento)
     */
    public IAdaptable createElement(IMemento memento) {
        String absolutePath = memento.getString(MEMENTO_ABSOLUTE_PATH_KEY) ; //$NON-NLS-1$

		if (absolutePath == null) {
			return null;
		}
		
		return new ExternalRubyFileEditorInput(new File(absolutePath)) ;
    }

}
