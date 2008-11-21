package org.marketcetera.photon.internal.strategy.ui;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.photon.PhotonImages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.internal.strategy.Strategy;
import org.marketcetera.photon.internal.strategy.StrategyManager;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * View that lists the registered strategies and allows the user to manage them.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class StrategiesView extends ViewPart {

	private TableViewer viewer;
	private Action doubleClickAction;

	/**
	 * Label provider for an observable list of {@link Strategy} objects.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since $Release$
	 */
	@ClassVersion("$Id$")
	private final class StrategyLabelProvider extends
			ObservableMapLabelProvider {

		public StrategyLabelProvider(IObservableSet domain) {
			super(BeansObservables.observeMaps(domain, Strategy.class,
					new String[] { "state", "displayName" })); //$NON-NLS-1$ //$NON-NLS-2$
		}

		@Override
		public Image getImage(Object element) {
			if (element instanceof Strategy) {
				switch (((Strategy) element).getState()) {
				case RUNNING:
					return PhotonPlugin.getDefault().getImageRegistry().get(
							PhotonImages.GREEN_LED);
				default:
					return PhotonPlugin.getDefault().getImageRegistry().get(
							PhotonImages.GRAY_LED);
				}
			} else {
				return PlatformUI.getWorkbench().getSharedImages().getImage(
						ISharedImages.IMG_OBJ_ELEMENT);
			}
		}

		@Override
		public String getText(Object element) {
			return element instanceof Strategy ? ((Strategy) element)
					.getDisplayName() : super.getText(element);
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return getImage(element);
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			return getText(element);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		ObservableListContentProvider contentProvider = new ObservableListContentProvider();
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new StrategyLabelProvider(contentProvider
				.getKnownElements()));
		viewer.setSorter(new ViewerSorter());
		viewer.setInput(StrategyManager.getCurrent().getStrategies());
		getViewSite().setSelectionProvider(viewer);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void makeActions() {
		doubleClickAction = new PropertyDialogAction(getViewSite(),
				getViewSite().getSelectionProvider());
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}