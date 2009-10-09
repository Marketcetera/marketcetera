package org.marketcetera.photon.commons.ui.databinding;

import org.eclipse.core.databinding.ObservablesManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Convenience base class for Composites that are using data binding to enable
 * automatic observable disposal.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public abstract class ObservingComposite extends Composite {

    private final ObservablesManager mObservablesManager;

    /**
     * Constructor.
     * 
     * @param parent
     *            parent composite in which to create the widgets
     */
    public ObservingComposite(Composite parent) {
        super(parent, SWT.NONE);
        mObservablesManager = new ObservablesManager();
        addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                mObservablesManager.dispose();
            }
        });
    }

    /**
     * Returns the {@link ObservablesManager} that will be disposed with this
     * widget.
     * 
     * @return the observablesManager
     */
    protected ObservablesManager getObservablesManager() {
        return mObservablesManager;
    }

}
