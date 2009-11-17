package org.marketcetera.photon.commons.ui.workbench;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.marketcetera.photon.commons.ui.databinding.DataBindingPreferencePage;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Extends {@link DataBindingPreferencePage} to support
 * {@link IWorkbenchPropertyPage}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public abstract class DataBindingPropertyPage extends DataBindingPreferencePage
        implements IWorkbenchPropertyPage {

    private IAdaptable element;

    @Override
    public IAdaptable getElement() {
        return element;
    }

    @Override
    public void setElement(IAdaptable element) {
        this.element = element;
    }
}
