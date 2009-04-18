package org.marketcetera.photon.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.marketdata.ui.MarketDataUI;
import org.marketcetera.photon.views.AveragePriceView;
import org.marketcetera.photon.views.FIXMessagesView;
import org.marketcetera.photon.views.FillsView;
import org.marketcetera.photon.views.MarketDataView;
import org.marketcetera.photon.views.OpenOrdersView;
import org.marketcetera.photon.views.StockOrderTicketView;
import org.marketcetera.photon.views.WebBrowserView;
import org.marketcetera.photon.views.fixmessagedetail.FIXMessageDetailView;

/**
 * Factory responsible for creating and laying out the equity perspective.
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
public class EquityPerspectiveFactory implements IPerspectiveFactory {

	private static final String SECONDARY_ID_WILDCARD = ":*"; //$NON-NLS-1$

	private static final String LEFT_FOLDER = "leftFolder"; //$NON-NLS-1$

	private static final String BOTTOM_FOLDER = "bottomFolder"; //$NON-NLS-1$

	private static final String RIGHT_FOLDER = "rightFolder"; //$NON-NLS-1$

	private static final String TOP_FOLDER = "topFolder"; //$NON-NLS-1$

	public static final String ID = "org.marketcetera.photon.EquityPerspective"; //$NON-NLS-1$

	private IFolderLayout rightFolder;

	private IFolderLayout bottomFolder;

	private IFolderLayout leftFolder;

	private IFolderLayout topFolder;

	/**
	 * Creates the initial layout of the equity perspective, laying out the
	 * console view, filters view, stock order ticket view, and browser view.
	 * Additionally it sets the editor area to be visible.
	 * 
	 * @param layout the current layout object
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);

		layout.addPerspectiveShortcut(OptionPerspectiveFactory.ID);
		layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
		
		bottomFolder = layout.createFolder(BOTTOM_FOLDER, IPageLayout.BOTTOM,
				0.7f, editorArea);
		bottomFolder.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW + SECONDARY_ID_WILDCARD);
		bottomFolder.addPlaceholder(AveragePriceView.ID + SECONDARY_ID_WILDCARD);
		bottomFolder.addPlaceholder(FillsView.ID + SECONDARY_ID_WILDCARD);
		bottomFolder.addPlaceholder(FIXMessagesView.ID + SECONDARY_ID_WILDCARD);
		bottomFolder.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		bottomFolder.addView(AveragePriceView.ID);
		bottomFolder.addView(FillsView.ID);
		bottomFolder.addView(FIXMessagesView.ID);

		leftFolder = layout.createFolder(LEFT_FOLDER, IPageLayout.LEFT, 0.45f,
				editorArea);
		leftFolder.addPlaceholder(WebBrowserView.ID + SECONDARY_ID_WILDCARD);	
		leftFolder.addView(WebBrowserView.ID);
		leftFolder.addView(MarketDataView.ID);
		leftFolder.addPlaceholder(FIXMessageDetailView.ID);
		leftFolder.addPlaceholder(MarketDataUI.MARKET_DEPTH_VIEW_ID + SECONDARY_ID_WILDCARD);

		rightFolder = layout.createFolder(RIGHT_FOLDER, IPageLayout.RIGHT,
				0f, editorArea);
		rightFolder.addPlaceholder(OpenOrdersView.ID+SECONDARY_ID_WILDCARD);
		rightFolder.addView(OpenOrdersView.ID);

		topFolder = layout.createFolder(TOP_FOLDER, IPageLayout.TOP,
				0.55f, RIGHT_FOLDER);
		topFolder.addPlaceholder(StockOrderTicketView.ID + SECONDARY_ID_WILDCARD);
		topFolder.addView(StockOrderTicketView.ID);

		

	}

}
