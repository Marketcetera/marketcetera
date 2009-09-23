package org.marketcetera.photon.internal.strategy.engine.ui.workbench;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Adapts deployed strategies to Eclipse platform interfaces.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id")
public class DeployedStrategyAdapterFactory implements IAdapterFactory {

    @Override
    public Object getAdapter(Object adaptableObject,
            @SuppressWarnings("unchecked") Class adapterType) {
        if (adapterType.equals(IWorkbenchAdapter.class)
                && adaptableObject instanceof DeployedStrategy) {
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
            DeployedStrategy strategy = (DeployedStrategy) o;
            return Messages.STRATEGY_ENGINES_ADAPTER_FACTORY_DEPLOYED_STRATEGY_LABEL
                    .getText(strategy.getInstanceName(), strategy.getEngine()
                            .getName());
        }
    };

}
