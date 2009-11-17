package org.marketcetera.photon.internal.strategy.engine.ui.workbench;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Adapts strategy engines to Eclipse platform interfaces.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id")
public class StrategyEngineAdapterFactory implements IAdapterFactory {

    @Override
    public Object getAdapter(Object adaptableObject,
            @SuppressWarnings("unchecked") Class adapterType) {
        if (adapterType.equals(IWorkbenchAdapter.class)
                && adaptableObject instanceof StrategyEngine) {
            return mAdapter;
        }
        return null;
    }

    @Override
    public Class<?>[] getAdapterList() {
        return new Class[] { IWorkbenchAdapter.class };
    }

    private final IWorkbenchAdapter mAdapter = new WorkbenchAdapter() {
        @Override
        public String getLabel(Object o) {
            return ((StrategyEngine) o).getName();
        }
    };
}
