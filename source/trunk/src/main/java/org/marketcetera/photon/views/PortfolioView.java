package org.marketcetera.photon.views;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.core.InternalID;
import org.marketcetera.photon.Application;
import org.marketcetera.photon.PhotonAdapterFactory;
import org.marketcetera.photon.model.IPortfolioListener;
import org.marketcetera.photon.model.Portfolio;
import org.marketcetera.photon.model.PositionEntry;
import org.marketcetera.photon.model.PositionProgress;

public class PortfolioView extends ViewPart {

	public static final String ID = "org.marketcetera.photon.views.PortfolioView";
	
	private TreeViewer treeViewer;
	private Portfolio root;
	private IAdapterFactory adapterFactory = new PhotonAdapterFactory();
	
	public PortfolioView() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		Platform.getAdapterManager().registerAdapters(adapterFactory, PositionProgress.class);
		initializePortfolio();
		treeViewer = new TreeViewer(parent,
				SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		
		getSite().setSelectionProvider(treeViewer);
		treeViewer.setLabelProvider(new WorkbenchLabelProvider());
		treeViewer.setContentProvider(new BaseWorkbenchContentProvider());
		treeViewer.setInput(root);
		root.addPositionListener(new IPortfolioListener() {
			public void positionsChanged(Portfolio portfolio, PositionProgress entry) {
				treeViewer.refresh();
			}
		});
		treeViewer.refresh();
	}

	private void initializePortfolio() {
		root = Application.getOrderManager().getRootPortfolio();
		Portfolio subPortfolio1 = new Portfolio(root, "My Portfolio 1");
		root.addEntry(subPortfolio1);
		subPortfolio1.addEntry(new PositionEntry(subPortfolio1, "IBM", new InternalID("1234")));
		
		Portfolio subPortfolio2 = new Portfolio(root, "My Portfolio 2");
		root.addEntry(subPortfolio2);
		subPortfolio2.addEntry(new PositionEntry(subPortfolio2, "MSFT", new InternalID("2345")));
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
