package org.marketcetera.photon.product.handlers;

import java.util.EnumMap;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.marketcetera.core.position.Grouping;
import org.marketcetera.core.position.PositionRow;
import org.marketcetera.photon.internal.product.Messages;
import org.marketcetera.photon.views.FillsView;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Shows a Fills view filtered by the currently selected position.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class ShowPositionFills extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getCurrentSelectionChecked(event);
		PositionRow row = (PositionRow) selection.getFirstElement();
		final EnumMap<Grouping, String> filters = new EnumMap<Grouping, String>(Grouping.class);
		Grouping[] keys = row.getGrouping();
		if (keys == null) {
			// if no grouping, then this is a unique position
			keys = Grouping.values();
		}
		for (Grouping key : keys) {
			filters.put(key, key.get(row));
		}
		final IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage();
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
		
			@Override
			public void run() {
				try {
					FillsView view = (FillsView) page.showView(FillsView.ID, filters.toString(), IWorkbenchPage.VIEW_ACTIVATE);
					view.setFillsFilter(filters);
				} catch (PartInitException e) {
					Messages.SHOW_POSITION_FILLS_UNABLE_TO_OPEN_VIEW.error(ShowPositionFills.this, e);
				}
			}
		});
		return null;
	}

}
