package org.marketcetera.photon.ui;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.FeedComponent;
import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.core.FeedComponent.FeedStatus;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.PhotonPlugin;

/**
 * The FeedStatusLineContribution manifests as an array of LED-like images at the
 * bottom of the application window that indicate the status of one or more of
 * the "feed" connections.  This could be anything from a market data feed to
 * a JMS connection to a database connection.  The images reflect status by
 * changing color in response to a call to {@link #setStatus(String, FeedStatus)}
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
public class FeedStatusLineContribution extends ContributionItem implements
		IFeedComponentListener {

	private Map<String, Label> labelMap;

	private int numLabels = 1;

	private String[] feedNames;

	private static EnumMap<FeedStatus, Image> statusImageMap = new EnumMap<FeedStatus, Image>(
			FeedStatus.class);

	/**
	 * Create a new FeedStatusLineContribution with the given id.  The number of status
	 * indicators is determined by the size of the pFeedNames array parameter.
	 * 
	 * @param id the id of this status line contribution (used by the RCP)
	 * @param pFeedNames the names of the feeds for which to keep status
	 */
	public FeedStatusLineContribution(String id, String[] pFeedNames) {
		super(id);
		this.numLabels = pFeedNames.length;
		this.feedNames = pFeedNames;
		labelMap = new HashMap<String, Label>();
	}

	
	/**
	 * Puts the correct number of status indicator images into the status line.
	 * 
	 * @see org.eclipse.jface.action.ContributionItem#fill(org.eclipse.swt.widgets.Composite)
	 */
	public void fill(Composite parent) {
		for (int i = 0; i < numLabels; i++) {
			Label aLabel = new Label(parent, SWT.NONE);
			labelMap.put(feedNames[i], aLabel);
			aLabel.setImage(getStatusImage(FeedStatus.OFFLINE));
		}
	}

	/**
	 * Returns the Image associated with the specified {@link FeedStatus} enum.
	 * This caches the images for later retrieval.
	 * 
	 * @param aStatus the status for which to get an image.
	 * @return the image associated with the given status
	 */
	protected Image getStatusImage(FeedStatus aStatus) {
		ILabelDecorator labelDecorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator("asdf");
		Image theImage = statusImageMap.get(aStatus);
		if (theImage == null) {
			ImageDescriptor descriptor;
			switch (aStatus) {
			case AVAILABLE:
				descriptor = PhotonPlugin.getImageDescriptor(IImageKeys.STATUS_AVAILABLE);
				break;
			case ERROR:
				descriptor = PhotonPlugin.getImageDescriptor(IImageKeys.STATUS_ERROR);
				break;

			case OFFLINE:
			case UNKNOWN:
			default:
				descriptor = PhotonPlugin.getImageDescriptor(IImageKeys.STATUS_OFFLINE);
				break;
			}

			theImage = descriptor.createImage();
			statusImageMap.put(aStatus, theImage);
		}
		return theImage;
	}

	public void setStatus(final String feedName, final FeedStatus aStatus) {
		final Label theLabel = labelMap.get(feedName);
		if (theLabel == null)
			return;
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (!theLabel.isDisposed()){
						theLabel.setImage(getStatusImage(aStatus));
						theLabel.setToolTipText(feedName + " " + aStatus.name());
					}
				}
			});
	}

	/**
	 * Updates the status of the appropriate status indicator based on 
	 * the values found in a changed FeedComponent.
	 * 
	 * @param fc the feed component that has changed
	 * @see org.marketcetera.core.IFeedComponentListener#feedComponentChanged(org.marketcetera.core.FeedComponent)
	 */
	public void feedComponentChanged(FeedComponent fc) {
		setStatus(fc.getID(), fc.getFeedStatus());
	}


	/**
	 * Disposes of the cached Image objects.
	 * @see org.eclipse.jface.action.ContributionItem#dispose()
	 */
	@Override
	public void dispose() {
		for (Image image : statusImageMap.values()) {
			image.dispose();
		}
		super.dispose();
	}

}
