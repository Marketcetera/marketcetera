package org.marketcetera.photon.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.model.MessageHolder;
import org.marketcetera.photon.model.PositionEntry;
import org.marketcetera.photon.views.GoogleFinanceView;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.Symbol;

public class ViewSecurityAction extends Action implements ISelectionListener,
		IWorkbenchAction {
	private final IWorkbenchWindow window;

	public final static String ID = "org.marketcetera.photon.ViewSecurity";

	private IStructuredSelection selection;

	public ViewSecurityAction(IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setText("Open &Finance Page");
		setToolTipText("Google Finance page for selected security");
		setImageDescriptor(PhotonPlugin.getImageDescriptor(IImageKeys.VIEW_SECURITY));
		window.getSelectionService().addSelectionListener(this);
	}

	public void dispose() {
		window.getSelectionService().removeSelectionListener(this);
	}

	public void selectionChanged(IWorkbenchPart part, ISelection incoming) {
		if (incoming instanceof IStructuredSelection) {
			selection = (IStructuredSelection) incoming;
			Object firstElement = selection.getFirstElement();
			setEnabled(selection.size() == 1
					&& (firstElement instanceof PositionEntry||
					firstElement instanceof Message ||
					firstElement instanceof MessageHolder));
		} else {
			// Other selections, for example containing text or of other kinds.
			setEnabled(false);
		}
	}

	public void run() {
		if (selection == null){
			return;
		}
		try {
			Object firstElement = selection.getFirstElement();
			String symbol = null;
			
			if (firstElement instanceof PositionEntry) {
				PositionEntry pos = (PositionEntry) firstElement;
				symbol = pos.getSymbol();
			} else if (firstElement instanceof Message) {
				Message message = (Message) firstElement;
				try {
					symbol = message.getString(Symbol.FIELD);
				} catch (FieldNotFound e) {
					// TODO Auto-generated catch block
				}
			} else if (firstElement instanceof MessageHolder) {
				MessageHolder holder = (MessageHolder) firstElement;
				try {
					symbol = holder.getMessage().getString(Symbol.FIELD);
				} catch (FieldNotFound e) {
					// TODO Auto-generated catch block
				}
			}
			if (symbol != null){
				IWorkbenchPage page = window.getActivePage();
				page.showView(GoogleFinanceView.ID);
				IViewReference[] viewReferences = page.getViewReferences();
				for (IViewReference reference : viewReferences) {
					if (GoogleFinanceView.ID.equals(reference.getId())) {
						IViewPart view = reference.getView(true);
						((GoogleFinanceView) view).browseTo(symbol);
					}
				}
			}
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
