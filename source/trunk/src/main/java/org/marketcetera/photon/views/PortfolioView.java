package org.marketcetera.photon.views;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.photon.Application;
import org.marketcetera.photon.PhotonAdapterFactory;
import org.marketcetera.photon.editors.OrderHistorySelectionProvider;
import org.marketcetera.photon.model.IPortfolioListener;
import org.marketcetera.photon.model.Portfolio;
import org.marketcetera.photon.model.PositionProgress;

public class PortfolioView extends ViewPart implements IPortfolioListener {

	public static final String ID = "org.marketcetera.photon.views.PortfolioView";
	
	private TreeViewer treeViewer;
	private IAdapterFactory adapterFactory = new PhotonAdapterFactory();
	
	public PortfolioView() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		Platform.getAdapterManager().registerAdapters(adapterFactory, PositionProgress.class);
		treeViewer = new TreeViewer(parent,
				SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		
		getSite().setSelectionProvider(treeViewer);
		treeViewer.setLabelProvider(new WorkbenchLabelProvider());
		treeViewer.setContentProvider(new BaseWorkbenchContentProvider());
		treeViewer.refresh();
		setInput(Application.getRootPortfolio());
		makeActions();
	}
	
	private void makeActions() {
		MenuManager menuMgr = new MenuManager("portfolioViewPopup");
		Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
		menuMgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		treeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, treeViewer);
	}


	public void setInput(Portfolio input){
		Portfolio oldPortfolio = (Portfolio)treeViewer.getInput();
		if (oldPortfolio != null){
			oldPortfolio.removePortfolioListener(this);
		}
		treeViewer.setInput(input);
		input.addPortfolioListener(this);
	}

	public void positionsChanged(Portfolio portfolio, PositionProgress entry) {
		asyncRefresh();
	}

	public void asyncExec(Runnable runnable) {
		Display display = this.getViewSite().getWorkbenchWindow().getShell().getDisplay();

		// If the display is disposed, you can't do anything with it!!!
		if (display == null || display.isDisposed())
			return;

		display.asyncExec(runnable);
	}
	
	protected void asyncRefresh()
	{
		asyncExec(new Runnable() {
			public void run() {
				treeViewer.refresh();
			}
		});
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
