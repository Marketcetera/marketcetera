package org.marketcetera.photon.marketdata;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataFeed.FeedStatusEvent;
import org.marketcetera.photon.marketdata.MarketDataFeed.IFeedStatusChangedListener;
import org.marketcetera.photon.ui.StatusIndicatorContributionItem;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Contribution item that displays the status of the active market data feed.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public class MarketDataStatusIndicator extends StatusIndicatorContributionItem {

	private Label imageLabel;
	private final MarketDataManager mMarketDataManager = PhotonPlugin
			.getDefault().getMarketDataManager();
	private IFeedStatusChangedListener mListener;

	@Override
	protected Control createControl(Composite parent) {
		super.createControl(parent);
		Composite composite = new Composite(parent, SWT.NONE);
		FillLayout layout = new FillLayout();
		layout.marginWidth = 2;
		composite.setLayout(layout);
		imageLabel = new Label(composite, SWT.NONE);
		mListener = new IFeedStatusChangedListener() {

			@Override
			public void feedStatusChanged(FeedStatusEvent event) {
				imageLabel.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						updateLabel();
					}
				});
			}
		};
		mMarketDataManager.addActiveFeedStatusChangedListener(mListener);
		updateLabel();
		return composite;
	}
	
	@Override
	protected void doDispose() {
		if (mListener != null) {
			mMarketDataManager
					.removeActiveFeedStatusChangedListener(mListener);
			mListener = null;
		}
		super.doDispose();
	}

	private void updateLabel() {
		FeedStatus newStatus = mMarketDataManager.getActiveFeedStatus();
		String name = mMarketDataManager.getActiveFeedName();
		Image image;
		I18NMessage1P tooltip;
		switch (newStatus) {
		case AVAILABLE:
			image = getOnImage();
			tooltip = Messages.MARKET_DATA_STATUS_ON_TOOLTIP;
			break;
		case ERROR:
			image = getErrorImage();
			tooltip = Messages.MARKET_DATA_STATUS_ERROR_TOOLTIP;
			break;
		default:
			image = getOffImage();
			tooltip = Messages.MARKET_DATA_STATUS_OFF_TOOLTIP;
			break;
		}
		imageLabel.setImage(image);
		if (name == null) {
			imageLabel.setToolTipText(Messages.MARKET_DATA_STATUS_NO_FEED_TOOLTIP.getText());
		} else {
			imageLabel.setToolTipText(tooltip.getText(name));
		}
	}

}
