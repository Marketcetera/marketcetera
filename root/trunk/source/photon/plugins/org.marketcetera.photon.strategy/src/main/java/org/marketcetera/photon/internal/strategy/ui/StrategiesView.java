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
import org.eclipse.jface.viewers.ViewerComparator;
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
import org.marketcetera.photon.internal.strategy.AbstractStrategyConnection;
import org.marketcetera.photon.internal.strategy.RemoteStrategyAgent;
import org.marketcetera.photon.internal.strategy.Strategy;
import org.marketcetera.photon.internal.strategy.StrategyManager;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * View that lists the registered strategies and allows the user to manage them.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public final class StrategiesView extends ViewPart {

	private TableViewer mViewer;
	private Action mDoubleClickAction;

	/**
	 * Label provider for an observable list of {@link Strategy} objects.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since 1.0.0
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
			if (element instanceof AbstractStrategyConnection) {
				switch (((AbstractStrategyConnection) element).getState()) {
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
			// the display name
			return super.getColumnText(element, 1);
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return getImage(element);
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			// we only want one label at this point
			return getText(element);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		mViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		ObservableListContentProvider contentProvider = new ObservableListContentProvider();
		mViewer.setContentProvider(contentProvider);
		mViewer.setLabelProvider(new StrategyLabelProvider(contentProvider
				.getKnownElements()));
		mViewer.setComparator(new ViewerComparator() {
			@Override
			public int category(Object element) {
				return element instanceof RemoteStrategyAgent ? 0 : 1;
			}
		});
		mViewer.setInput(StrategyManager.getCurrent().getStrategies());
		getViewSite().setSelectionProvider(mViewer);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		Menu menu = menuMgr.createContextMenu(mViewer.getControl());
		mViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, mViewer);
	}

	private void makeActions() {
		mDoubleClickAction = new PropertyDialogAction(getViewSite(),
				getViewSite().getSelectionProvider());
	}

	private void hookDoubleClickAction() {
		mViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				mDoubleClickAction.run();
			}
		});
	}

	@Override
	public void setFocus() {
		mViewer.getControl().setFocus();
	}
}