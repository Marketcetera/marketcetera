package org.marketcetera.photon.actions;

import static org.marketcetera.photon.Messages.CANNOT_OPEN_VIEW;
import static org.marketcetera.photon.Messages.OPEN_NEW_LABEL;
import static org.marketcetera.photon.Messages.OPEN_NEW_TOOLTIPS;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.PhotonPlugin;

/* $License$ */

/**
 * Opens a new view of the given type.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.6.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class OpenAdditionalViewAction
        extends Action
{
    /**
     * unique identifier for this action
     */
    private static final String ID = "org.marketcetera.photon.actions.OpenAdditionalViewAction"; //$NON-NLS-1$
    /**
     * the ID of the new view
     */
    private final String mViewID;
    /**
     * common counter used to create unique identifiers for the views opened
     */
    private static final AtomicInteger sCounter = new AtomicInteger(0);
    /**
     * the name of the new view
     */
    private final String mHumanReadableViewName;
    /**
     * the actual view object
     */
    private final IWorkbenchWindow mWindow;
    /**
     * Create a new OpenAdditionalViewAction instance.
     *
     * @param inWindow an <code>IWorkbenchWindow</code> value indicating the type of view to open
     * @param inHumanReadableViewName a <code>String</code> value containing the name of the view
     * @param inViewID a <code>String</code> value containing the ID of the view
     */
    public OpenAdditionalViewAction(IWorkbenchWindow inWindow,
                                    String inHumanReadableViewName,
                                    String inViewID)
    {
        mWindow = inWindow;
        mHumanReadableViewName = inHumanReadableViewName;
        mViewID = inViewID;
        setId(ID);
        setText(OPEN_NEW_LABEL.getText(inHumanReadableViewName));
        setToolTipText(OPEN_NEW_TOOLTIPS.getText(inHumanReadableViewName));
        setImageDescriptor(PhotonPlugin.getImageDescriptor(IImageKeys.OPEN_ADDITIONAL_VIEW));
    }
    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run()
    {
        IWorkbenchPage page = mWindow.getActivePage();
        try {
            page.showView(mViewID,
                          Integer.toString(sCounter.getAndIncrement()),
                          IWorkbenchPage.VIEW_ACTIVATE);
        } catch (PartInitException e) {
            PhotonPlugin.getMainConsoleLogger().error(CANNOT_OPEN_VIEW.getText(mHumanReadableViewName),
                                                      e);
        }
    }
}
