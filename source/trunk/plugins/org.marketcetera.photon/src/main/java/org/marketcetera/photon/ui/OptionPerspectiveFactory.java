package org.marketcetera.photon.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.marketcetera.photon.views.AveragePriceView;
import org.marketcetera.photon.views.FIXMessagesView;
import org.marketcetera.photon.views.FillsView;
import org.marketcetera.photon.views.OpenOrdersView;
import org.marketcetera.photon.views.OptionMarketDataView;
import org.marketcetera.photon.views.OptionOrderTicket;
import org.marketcetera.photon.views.StockOrderTicket;

/**
 * Factory responsible for creating and laying out the option perspective.
 * 
 * @author andrei.lissovski@softwaregoodness.com
 */
public class OptionPerspectiveFactory implements IPerspectiveFactory {

	private static final String LEFT_FOLDER = "leftFolder";

	private static final String BOTTOM_FOLDER = "bottomFolder";

	private static final String RIGHT_FOLDER = "rightFolder";

	private static final String TOP_FOLDER = "topFolder";

	public static final String ID = "org.marketcetera.photon.OptionPerspective";

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

		layout.addPerspectiveShortcut("org.marketcetera.photon.DebugPerspective");
		layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
		layout.addShowViewShortcut(StockOrderTicket.ID);
		layout.addShowViewShortcut(OptionOrderTicket.ID);
		
		bottomFolder = layout.createFolder(BOTTOM_FOLDER, IPageLayout.BOTTOM,
				0.7f, editorArea);
		bottomFolder.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW + ":*");
		bottomFolder.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		bottomFolder.addView(AveragePriceView.ID);
		bottomFolder.addView(FillsView.ID);
		bottomFolder.addView(FIXMessagesView.ID);

		leftFolder = layout.createFolder(LEFT_FOLDER, IPageLayout.LEFT, 0.5f,
				editorArea);
		leftFolder.addView(OptionMarketDataView.ID);

		rightFolder = layout.createFolder(RIGHT_FOLDER, IPageLayout.RIGHT,
				0.85f, editorArea);
		rightFolder.addView(OpenOrdersView.ID);

		topFolder = layout.createFolder(TOP_FOLDER, IPageLayout.TOP,
				0.4f, RIGHT_FOLDER);
		topFolder.addPlaceholder(OptionOrderTicket.ID + ":*");
		topFolder.addView(OptionOrderTicket.ID);
	}

}
