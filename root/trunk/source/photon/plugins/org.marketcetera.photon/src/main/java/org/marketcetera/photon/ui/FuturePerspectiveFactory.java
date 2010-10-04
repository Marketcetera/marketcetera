package org.marketcetera.photon.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.marketcetera.photon.marketdata.ui.MarketDataUI;
import org.marketcetera.photon.views.*;
import org.marketcetera.photon.views.fixmessagedetail.FIXMessageDetailView;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.0
 */
public class FuturePerspectiveFactory
        implements IPerspectiveFactory
{
    /* (non-Javadoc)
     * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
     */
    @Override
    public void createInitialLayout(IPageLayout inLayout)
    {
        String editorArea = inLayout.getEditorArea();
        inLayout.setEditorAreaVisible(false);

        inLayout.addPerspectiveShortcut(EquityPerspectiveFactory.ID);
        inLayout.addPerspectiveShortcut(OptionPerspectiveFactory.ID);
        inLayout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
        inLayout.addShowViewShortcut(StockOrderTicketView.ID);
        inLayout.addShowViewShortcut(OptionOrderTicketView.ID);
        inLayout.addShowViewShortcut(FutureOrderTicketView.ID);
        
        bottomFolder = inLayout.createFolder(BOTTOM_FOLDER, IPageLayout.BOTTOM,
                0.7f, editorArea);
        bottomFolder.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW + ":*"); //$NON-NLS-1$
        bottomFolder.addView(IConsoleConstants.ID_CONSOLE_VIEW);
        bottomFolder.addView(AveragePriceView.ID);
        bottomFolder.addView(FillsView.ID);
        bottomFolder.addView(FIXMessagesView.ID);

        leftFolder = inLayout.createFolder(LEFT_FOLDER,
                                           IPageLayout.LEFT,
                                           0.45f,
                                           editorArea);
        leftFolder.addPlaceholder(WebBrowserView.ID + SECONDARY_ID_WILDCARD);   
        leftFolder.addView(WebBrowserView.ID);
        leftFolder.addView(MarketDataView.ID);
        leftFolder.addPlaceholder(FIXMessageDetailView.ID);
        leftFolder.addPlaceholder(MarketDataUI.MARKET_DEPTH_VIEW_ID + SECONDARY_ID_WILDCARD);

        rightFolder = inLayout.createFolder(RIGHT_FOLDER,
                                            IPageLayout.RIGHT,
                                            0f,
                                            editorArea);
        rightFolder.addPlaceholder(OpenOrdersView.ID+SECONDARY_ID_WILDCARD);
        rightFolder.addView(OpenOrdersView.ID);
        topFolder = inLayout.createFolder(TOP_FOLDER,
                                          IPageLayout.TOP,
                                          0.55f,
                                          RIGHT_FOLDER);
        topFolder.addPlaceholder(FutureOrderTicketView.ID + SECONDARY_ID_WILDCARD);
        topFolder.addView(FutureOrderTicketView.ID);
    }
    private static final String LEFT_FOLDER = "leftFolder"; //$NON-NLS-1$

    private static final String BOTTOM_FOLDER = "bottomFolder"; //$NON-NLS-1$

    private static final String RIGHT_FOLDER = "rightFolder"; //$NON-NLS-1$
    private static final String TOP_FOLDER = "topFolder"; //$NON-NLS-1$

    public static final String ID = "org.marketcetera.photon.FuturePerspective"; //$NON-NLS-1$

    private IFolderLayout rightFolder;

    private IFolderLayout bottomFolder;

    private IFolderLayout leftFolder;
    private static final String SECONDARY_ID_WILDCARD = ":*"; //$NON-NLS-1$
    private IFolderLayout topFolder;
}
