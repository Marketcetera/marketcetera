package org.marketcetera.photon;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.marketcetera.core.FeedComponent;
import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.core.FeedComponent.FeedStatus;

public class FeedStatusLineContribution extends ContributionItem implements
		IFeedComponentListener {

	private Map<String, Label> labelMap;

	private int numLabels = 1;

	private String[] statusNames;

	private static EnumMap<FeedStatus, Image> statusImageMap = new EnumMap<FeedStatus, Image>(
			FeedStatus.class);

	public FeedStatusLineContribution(String id, String[] statusNames) {
		super(id);
		this.numLabels = statusNames.length;
		this.statusNames = statusNames;
		labelMap = new HashMap<String, Label>();
	}

	public void fill(Composite parent) {
		for (int i = 0; i < numLabels; i++) {
			Label aLabel = new Label(parent, SWT.NONE);
			labelMap.put(statusNames[i], aLabel);
			aLabel.setImage(getStatusImage(FeedStatus.OFFLINE));
		}
	}

	protected Image getStatusImage(FeedStatus aStatus) {
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
		Display.getCurrent().asyncExec(new Runnable() {
			public void run() {
				theLabel.setImage(getStatusImage(aStatus));
				theLabel.setToolTipText(feedName + " " + aStatus.name());
			}
		});
	}

	public void feedComponentChanged(FeedComponent fc) {
		setStatus(fc.getID(), fc.getFeedStatus());
	}

}
