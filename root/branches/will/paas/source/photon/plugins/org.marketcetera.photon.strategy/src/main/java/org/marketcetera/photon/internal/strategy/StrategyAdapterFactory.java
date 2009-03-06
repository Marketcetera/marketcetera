package org.marketcetera.photon.internal.strategy;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Adapts {@link Strategy} to Eclipse platform interfaces.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class StrategyAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof AbstractStrategyConnection
				&& adapterType.equals(IWorkbenchAdapter.class)) {
			return mWorkbenchAdapter;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class[] getAdapterList() {
		return new Class[] { IWorkbenchAdapter.class };
	}

	final IWorkbenchAdapter mWorkbenchAdapter = new WorkbenchAdapter() {

		@Override
		public String getLabel(Object o) {
			return ((AbstractStrategyConnection) o).getDisplayName();
		}
	};

}
