package org.marketcetera.photon;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.marketcetera.photon.views.FiltersView;
import org.marketcetera.photon.views.StockOrderTicket;
import org.marketcetera.photon.views.WebBrowserView;

public class EquityPerspectiveFactory implements IPerspectiveFactory {

	private static final String LEFT_FOLDER = "leftFolder";

	private static final String BOTTOM_FOLDER = "bottomFolder";

	private static final String RIGHT_FOLDER = "rightFolder";

	public static final String ID = "org.marketcetera.photon.EquityPerspective";

	IFolderLayout leftFolder;

	IFolderLayout rightFolder;

	IFolderLayout bottomFolder;

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);

		layout.addPerspectiveShortcut("org.marketcetera.photon.DebugPerspective");
		layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
		
		bottomFolder = layout.createFolder(BOTTOM_FOLDER, IPageLayout.BOTTOM,
				0.7f, editorArea);
		bottomFolder.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW + ":*");
		bottomFolder.addView(IConsoleConstants.ID_CONSOLE_VIEW);

		leftFolder = layout.createFolder(LEFT_FOLDER, IPageLayout.LEFT, 0.35f,
				editorArea);
		leftFolder.addPlaceholder(FiltersView.ID + ":*");
		leftFolder.addView(FiltersView.ID);

		rightFolder = layout.createFolder(RIGHT_FOLDER, IPageLayout.RIGHT,
				0.85f, editorArea);
		rightFolder.addPlaceholder(StockOrderTicket.ID + ":*");
		rightFolder.addView(StockOrderTicket.ID);
		rightFolder.addPlaceholder(WebBrowserView.ID + ":*");
		rightFolder.addView(WebBrowserView.ID);

	}

}
