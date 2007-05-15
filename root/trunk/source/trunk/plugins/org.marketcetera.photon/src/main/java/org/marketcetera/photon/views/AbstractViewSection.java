package org.marketcetera.photon.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IViewSite;

/**
 * Represent a section of a View, contains refactored parts of a ViewPart.
 */
public abstract class AbstractViewSection 
{
    private IViewSite viewSite;

    
    public AbstractViewSection( IViewSite viewSite )
    {
        this.viewSite = viewSite;
    }

    protected IViewSite getViewSite()
    {
        return viewSite;
    }

    public abstract void dispose();

    protected abstract void createSectionControl(Composite parent);

    /**
     * Return the outermost containing control for this section
     */
    public abstract Control getControl();
}
