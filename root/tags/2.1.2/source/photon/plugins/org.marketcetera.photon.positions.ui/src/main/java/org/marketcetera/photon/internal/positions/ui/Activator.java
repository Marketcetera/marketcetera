package org.marketcetera.photon.internal.positions.ui;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.marketcetera.core.position.PositionEngine;
import org.marketcetera.photon.positions.ui.IPositionLabelProvider;
import org.marketcetera.util.misc.ClassVersion;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/* $License$ */

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.marketcetera.photon.positions.ui"; //$NON-NLS-1$

	// The shared instance
	private static Activator sPlugin;

	private IObservableValue mPositionEngine = new WritableValue(new SyncRealm(), null, PositionEngine.class);

	private ServiceTracker positionLabelTracker;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		ServiceListener sl = new ServiceListener() {
			public void serviceChanged(ServiceEvent ev) {
				ServiceReference sr = ev.getServiceReference();
				PositionEngine engine = (PositionEngine) context.getService(sr);
				switch (ev.getType()) {
				case ServiceEvent.REGISTERED: {
					mPositionEngine.setValue(engine);
				}
					break;
				case ServiceEvent.UNREGISTERING: {
					mPositionEngine.setValue(null);
				}
					break;
				}
			}
		};

		String filter = "(objectclass=" + PositionEngine.class.getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		try {
			context.addServiceListener(sl, filter);
			ServiceReference[] srl = context.getServiceReferences(null, filter);
			for (int i = 0; srl != null && i < srl.length; i++) {
				sl.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, srl[i]));
			}
		} catch (InvalidSyntaxException e) {
			// the filter is hardcoded above, syntax should be valid
			throw new AssertionError(e);
		}
		positionLabelTracker = new ServiceTracker(context, IPositionLabelProvider.class.getName(),
				null);
		positionLabelTracker.open();
		sPlugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		sPlugin = null;
		positionLabelTracker.close();
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return sPlugin;
	}

	/**
	 * Returns an observable reference to the position engine service.
	 * 
	 * @return the position engine observable, will not be null
	 */
	public IObservableValue getPositionEngine() {
		return mPositionEngine;
	}

	/**
	 * Returns the position label provider service.
	 * 
	 * @return the position label provider, or null if none exists.
	 */
	public IPositionLabelProvider getPositionLabelProvider() {
		return (IPositionLabelProvider) positionLabelTracker.getService();
	}
	
	/**
	 * Simple realm that synchronizes access to the observable.
	 */
	@ClassVersion("$Id$")
	private class SyncRealm extends Realm {

		@Override
		public boolean isCurrent() {
			return true;
		}
		
		@Override
		protected void syncExec(Runnable runnable) {
			synchronized (mPositionEngine) {
				super.syncExec(runnable);
			}
		}
		
	}

}
