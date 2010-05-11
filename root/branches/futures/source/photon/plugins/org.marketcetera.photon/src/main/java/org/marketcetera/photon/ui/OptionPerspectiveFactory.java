package org.marketcetera.photon.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.marketcetera.photon.views.*;
import org.marketcetera.photon.views.fixmessagedetail.FIXMessageDetailView;

/**
 * Factory responsible for creating and laying out the option perspective.
 * 
 * @author andrei.lissovski@softwaregoodness.com
 */
public class OptionPerspectiveFactory implements IPerspectiveFactory {

	private static final String LEFT_FOLDER = "leftFolder"; //$NON-NLS-1$

	private static final String BOTTOM_FOLDER = "bottomFolder"; //$NON-NLS-1$

	private static final String RIGHT_FOLDER = "rightFolder"; //$NON-NLS-1$

	public static final String ID = "org.marketcetera.photon.OptionPerspective"; //$NON-NLS-1$

	private IFolderLayout rightFolder;

	private IFolderLayout bottomFolder;

	private IFolderLayout leftFolder;

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

		layout.addPerspectiveShortcut(EquityPerspectiveFactory.ID);
        layout.addPerspectiveShortcut(FuturePerspectiveFactory.ID);
		layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
		layout.addShowViewShortcut(StockOrderTicketView.ID);
		layout.addShowViewShortcut(OptionOrderTicketView.ID);
        layout.addShowViewShortcut(FutureOrderTicketView.ID);
		
		bottomFolder = layout.createFolder(BOTTOM_FOLDER, IPageLayout.BOTTOM,
				0.7f, editorArea);
		bottomFolder.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW + ":*"); //$NON-NLS-1$
		bottomFolder.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		bottomFolder.addView(AveragePriceView.ID);
		bottomFolder.addView(FillsView.ID);
		bottomFolder.addView(FIXMessagesView.ID);

		leftFolder = layout.createFolder(LEFT_FOLDER, IPageLayout.LEFT, 0.5f,
				editorArea);
		leftFolder.addPlaceholder(FIXMessageDetailView.ID);
		leftFolder.addPlaceholder(OptionOrderTicketView.ID + ":*"); //$NON-NLS-1$
		leftFolder.addView(OptionOrderTicketView.ID);

		rightFolder = layout.createFolder(RIGHT_FOLDER, IPageLayout.RIGHT,
				0.85f, editorArea);
		rightFolder.addPlaceholder(OpenOrdersView.ID+":*"); //$NON-NLS-1$
		rightFolder.addView(OpenOrdersView.ID);
	}

}
