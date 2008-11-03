package org.marketcetera.photon.ui;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.IFeedComponent;
import org.marketcetera.photon.PhotonPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;


/**
 * Status image widget for the status line.  Supports status based on
 * {@link IFeedComponent} services.
 * 
 * @author gmiller
 * @author andrei@lissovski.org
 */
public class StatusImageTrimWidget extends StatusIndicatorContributionItem {

	private static final String EMPTY_WIDGET_NAME = " ";	 //$NON-NLS-1$
	
	private Label imageLabel;
	private String serviceName;
	private ServiceTracker serviceTracker;
	private String name;
	private String feedID;
	
	@Override
	protected Control createControl(Composite parent) {
		super.createControl(parent);
		serviceTracker = new StatusLineServiceTracker(PhotonPlugin.getDefault().getBundleContext(), serviceName, null);
		serviceTracker.open();
		
		Composite composite = new Composite(parent, SWT.NONE);
		
		FillLayout layout = new FillLayout();
		layout.marginWidth  = 2;
		composite.setLayout(layout);
		
		imageLabel = new Label(composite, SWT.NONE);
		FeedStatus feedStatus = getServiceStatus(serviceTracker.getService());
		update(feedStatus);
		
		return composite;
	}
	
	@Override
	protected void doDispose() {
		if (serviceTracker != null){
			serviceTracker.close();
		}
		super.doDispose();
	}
	
	private void serviceChanged(Object service) 
	{
		final FeedStatus theStatus = getServiceStatus(service);
		// make sure the UI is updated from the correct UI thread - this incantation guarantees that
		//  but it does not guarantee exactly *when* it will get updated
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
			    update(theStatus);
			}
		});
	}

	private void update(final FeedStatus aStatus) {
		// if the view containing the image is no longer alive, don't try to update anything
		if (imageLabel != null && !imageLabel.isDisposed()) {
		    // the view is alive
		    // update the image with the correct picture based on the status
			imageLabel.setImage(getStatusImage(aStatus));
			// the status has a string associated with it, use that to describe the status of the image in the UI
			String statusString = (aStatus == null ? FeedStatus.UNKNOWN.name() : aStatus.name());
			// use the feedID or a place holder to label the status
			String nameString = (feedID == null ? EMPTY_WIDGET_NAME : String.format(" \"%s\" ",  //$NON-NLS-1$
			                                                                        feedID));
			// put it all together
			imageLabel.setToolTipText(String.format("%s%s%s", //$NON-NLS-1$
			                                        name,
			                                        nameString,
			                                        statusString));
		}
	}

	private FeedStatus getServiceStatus(Object service) 
	{
		FeedStatus theStatus = FeedStatus.UNKNOWN;
		if (service != null && 
		    service instanceof IFeedComponent) {
			IFeedComponent feedComponent = (IFeedComponent)service;
			feedID = feedComponent.getID();
			theStatus = feedComponent.getFeedStatus();
		}
		return theStatus;
	}

	/**
	 * Returns the Image associated with the specified {@link FeedStatus} enum.
	 * 
	 * @param aStatus the status for which to get an image.
	 * @return the image associated with the given status
	 */
	private Image getStatusImage(FeedStatus aStatus) 
	{
		if (aStatus == null) {
			return getOffImage();
		}
		switch (aStatus) {
		case AVAILABLE:
			return getOnImage();
		case ERROR:
			return getErrorImage();
		default:
			return getOffImage();
		}
	}
	
	private final class StatusLineServiceTracker extends ServiceTracker {
		private StatusLineServiceTracker(BundleContext context, String clazz, ServiceTrackerCustomizer customizer) {
			super(context, clazz, customizer);
		}

		@Override
		public Object addingService(ServiceReference reference) {
			Object service = super.addingService(reference);
			serviceChanged(service);
			return service;
		}

		@Override
		public void modifiedService(ServiceReference reference, Object service) {
			super.modifiedService(reference, service);
			serviceChanged(service);
		}

		@Override
		public void removedService(ServiceReference reference, Object service) {
			super.removedService(reference, service);
			serviceChanged(service);
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		super.setInitializationData(config, propertyName, data);
		if ("class".equals(propertyName) && data != null && data instanceof Map){ //$NON-NLS-1$
			Map<String, String> dataMap = (Map<String, String>) data;
			for (String aKey : dataMap.keySet()) {
				if ("service".equals(aKey)){ //$NON-NLS-1$
					serviceName = dataMap.get(aKey);
				} else if ("name".equals(aKey)){ //$NON-NLS-1$
					name = dataMap.get(aKey);
				}
			}
		}
	}
}
