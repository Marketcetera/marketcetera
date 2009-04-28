package org.marketcetera.photon.internal.positions.ui;

import java.util.Arrays;

import net.miginfocom.swt.MigLayout;

import org.apache.commons.lang.Validate;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.marketcetera.core.position.Grouping;
import org.marketcetera.photon.commons.ui.FilterBox;
import org.marketcetera.photon.commons.ui.FilterBox.FilterChangeEvent;
import org.marketcetera.photon.commons.ui.table.ChooseColumnsMenu.IColumnProvider;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Real-time Positions and P&L view. The page book view has three pages that can be shown:
 * <ul>
 * <li>{@link UnavailablePage} - default page shown when position data is not available.</li>
 * <li>{@link PositionsViewTablePage} - flat, table based P&amp;L data.</li>
 * <li>{@link PositionsViewTreePage} - tree based page allowing data to be grouped hierarchically.</li>
 * </ul>
 * 
 * This view does not care about the actual active workbench part. So fake parts are used to control
 * the switching of pages. For example, to switch to the flat page, you call
 * {@link #partActivated(IWorkbenchPart)}, passing PositionsPart.FLAT as the parameter.
 * <p>
 * This view serializes it's state, which includes the last viewed page (flat or hierarchical) and
 * last selected grouping on the hierarchical page.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class PositionsView extends PageBookView implements IColumnProvider {

	/**
	 * Dummy part that allows us to take advantage of {@link PageBookView} infrastructure to support
	 * switching between table and tree UI.
	 * 
	 * This technique was inspired by
	 * org.eclipse.pde.internal.ui.views.dependencies.DependenciesView.
	 */
	@ClassVersion("$Id$")
	private static enum PositionsPart implements IWorkbenchPart {

		/**
		 * Flat table UI.
		 */
		FLAT,

		/**
		 * Grouped tree UI.
		 */
		HIERARCHICAL;

		@Override
		public void addPropertyListener(IPropertyListener listener) {
		}

		@Override
		public void createPartControl(Composite parent) {
		}

		@Override
		public void dispose() {
		}

		@Override
		@SuppressWarnings("unchecked")
		public Object getAdapter(Class adapter) {
			return null;
		}

		@Override
		public IWorkbenchPartSite getSite() {
			return null;
		}

		@Override
		public String getTitle() {
			return null;
		}

		@Override
		public Image getTitleImage() {
			return null;
		}

		@Override
		public String getTitleToolTip() {
			return null;
		}

		@Override
		public void removePropertyListener(IPropertyListener listener) {
		}

		@Override
		public void setFocus() {
		}
	}

	/**
	 * Changes the current page.
	 */
	@ClassVersion("$Id$")
	public static final class ChangePageHandler extends AbstractHandler implements IHandler,
			IExecutableExtension {

		private PositionsPart mPart;

		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			PositionsView view = (PositionsView) HandlerUtil.getActivePart(event);
			if (view != null && view.mPositionEngine.getValue() != null) {
				view.showPart(mPart);
			}
			return null;
		}

		@Override
		public void setInitializationData(IConfigurationElement config, String propertyName,
				Object data) throws CoreException {
			mPart = PositionsPart.valueOf((String) data);
		}
	}

	/**
	 * Toolbar filter box.
	 */
	@ClassVersion("$Id$")
	public static final class ViewFilter extends WorkbenchWindowControlContribution {

		@Override
		protected Control createControl(Composite parent) {
			Composite composite = new Composite(parent, SWT.NONE);
			// insets keeps the box outline inside the toolbar area
			composite.setLayout(new MigLayout("ins 1")); //$NON-NLS-1$
			Label filterLabel = new Label(composite, SWT.NONE);
			filterLabel.setText(Messages.POSITIONS_VIEW_FILTER_LABEL.getText());
			FilterBox filter = new FilterBox(composite);
			filter.addListener(new FilterBox.FilterChangeListener() {

				@Override
				public void filterChanged(FilterChangeEvent event) {
					PositionsView view = getView();
					if (view != null) view.setFilterText(event.getFilterText());
				}
			});
			return composite;
		}
	}

	/*
	 * memento keys for serialization
	 */
	private static final String LAST_PART_KEY = "defaultPart"; //$NON-NLS-1$
	private static final String LAST_GROUPING_KEY_1 = "grouping1"; //$NON-NLS-1$
	private static final String LAST_GROUPING_KEY_2 = "grouping2"; //$NON-NLS-1$

	/*
	 * remembers user choices
	 */
	private PositionsPart mLastPart = PositionsPart.FLAT;
	private Grouping[] mLastGrouping = new Grouping[] { Grouping.Symbol, Grouping.Account };

	private final IObservableValue mPositionEngine = Activator.getDefault().getPositionEngine();
	private Grouping[] mGrouping = null;
	private String mFilterText = ""; //$NON-NLS-1$
	private IMemento mMemento;
	private MenuManager mMenuManager;
	private IValueChangeListener mEngineListener;

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		mMemento = memento;
		// restore saved state, if any
		if (memento != null) {
			try {
				String part = memento.getString(LAST_PART_KEY);
				if (part != null) {
					mLastPart = PositionsPart.valueOf(part);
				}
			} catch (IllegalArgumentException e) {
				Messages.POSITIONS_VIEW_STATE_RESTORE_FAILURE.error(this, e);
			}
			try {
				String one = memento.getString(LAST_GROUPING_KEY_1);
				String two = memento.getString(LAST_GROUPING_KEY_2);
				if (one != null && two != null) {
					mLastGrouping = new Grouping[] { Grouping.valueOf(one), Grouping.valueOf(two) };
				}
			} catch (IllegalArgumentException e) {
				Messages.POSITIONS_VIEW_STATE_RESTORE_FAILURE.error(this, e);
			}
		}
		// both pages share a context menu
		mMenuManager = new MenuManager();
		site.registerContextMenu(mMenuManager, getSelectionProvider());

		// a listener that activates/deactivates the page when the position engine
		// appears/disappears
		mEngineListener = new IValueChangeListener() {
			@Override
			public void handleValueChange(ValueChangeEvent event) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (mPositionEngine.getValue() == null) {
							mGrouping = null;
							partClosed(getCurrentContributingPart());
						} else {
							showPart(mLastPart);
						}
					}
				});
				
			}
		};
		mPositionEngine.addValueChangeListener(mEngineListener);
	}

	@Override
	public void saveState(IMemento memento) {
		memento.putString(LAST_PART_KEY, mLastPart.name());
		memento.putString(LAST_GROUPING_KEY_1, mLastGrouping[0].name());
		memento.putString(LAST_GROUPING_KEY_2, mLastGrouping[1].name());
		// allow individual pages to save state too
		PageRec page = getPageRec(PositionsPart.FLAT);
		if (page != null) ((PositionsViewPage) page.page).saveState(memento);
		page = getPageRec(PositionsPart.HIERARCHICAL);
		if (page != null) ((PositionsViewPage) page.page).saveState(memento);
	}

	@Override
	public void dispose() {
		// cleanup listener
		mPositionEngine.removeValueChangeListener(mEngineListener);
		super.dispose();
	}

	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		// We don't care about the active workbench part, just dummy parts "activated" by this view
		return part instanceof PositionsPart;
	}

	@Override
	public Control getColumnWidget() {
		IPage currentPage = getCurrentPage();
		if (currentPage instanceof PositionsViewPage) {
			return ((PositionsViewPage) currentPage).getColumnWidget();
		} else {
			return null;
		}
	}

	@Override
	protected IWorkbenchPart getBootstrapPart() {
		// only can bootstrap if the engine is available
		return mPositionEngine.getValue() == null ? null : mLastPart;
	}

	@Override
	protected IPage createDefaultPage(PageBook book) {
		UnavailablePage unavailablePage = new UnavailablePage();
		unavailablePage.createControl(book);
		return unavailablePage;
	}

	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		final IPageBookViewPage page;
		switch ((PositionsPart) part) {
		case FLAT:
			page = new PositionsViewTablePage(this, mMemento);
			break;
		case HIERARCHICAL:
			page = new PositionsViewTreePage(this, mMemento);
			break;
		default:
			throw new AssertionError("Invalid part"); //$NON-NLS-1$
		}

		initPage(page);
		page.createControl(getPageBook());
		return new PageRec(part, page);
	}

	@Override
	protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
		IPage page = pageRecord.page;
		page.dispose();
		pageRecord.dispose();
	}

	@Override
	protected void showPageRec(PageRec pageRec) {
		super.showPageRec(pageRec);
		if (!(pageRec.page instanceof UnavailablePage)) {
			installContextMenu();
			((PositionsViewPage) pageRec.page).setFilterText(getFilterText());
		}
	}

	/**
	 * Adds the common context menu to the column widget.
	 */
	private void installContextMenu() {
		MenuManager contextMenu = mMenuManager;
		Control control = getColumnWidget();
		Menu menu = contextMenu.createContextMenu(control);
		control.setMenu(menu);
	}

	private void setFilterText(String filterText) {
		mFilterText = filterText;
		((PositionsViewPage) getCurrentPage()).setFilterText(filterText);
	}

	/**
	 * @return the current filter text
	 */
	String getFilterText() {
		return mFilterText;
	}

	/**
	 * @return the current grouping
	 */
	Grouping[] getGrouping() {
		return mGrouping;
	}

	/**
	 * Shows the specified part.
	 * 
	 * @param part
	 *            the part
	 */
	void showPart(PositionsPart part) {
		switch (part) {
		case HIERARCHICAL:
			showHierarchicalPage();
			break;
		case FLAT:
			showFlatPage();
			break;
		default:
			throw new AssertionError();
		}
	}

	/**
	 * Shows the flat page.
	 */
	void showFlatPage() {
		mGrouping = null;
		mLastPart = PositionsPart.FLAT;
		partClosed(PositionsPart.HIERARCHICAL);
		partActivated(PositionsPart.FLAT);
	}

	/**
	 * Shows the hierarchical page with the the default grouping.
	 */
	void showHierarchicalPage() {
		showHierarchicalPage(mLastGrouping);
	}

	/**
	 * Shows the hierarchical page with the provided grouping.
	 * 
	 * @param grouping
	 *            the grouping
	 */
	void showHierarchicalPage(Grouping[] grouping) {
		Validate.noNullElements(grouping);
		Validate.isTrue(grouping.length == 2);
		if (!Arrays.equals(mGrouping, grouping)) {
			mLastPart = PositionsPart.HIERARCHICAL;
			mGrouping = grouping;
			mLastGrouping = grouping;
			// dispose the last tree page if it exists to free up resources
			partClosed(PositionsPart.HIERARCHICAL);
			partActivated(PositionsPart.HIERARCHICAL);
		}
	}

	/**
	 * Convenience method to get the active positions view instance.
	 * 
	 * @return the active PositionsView instance, or null if there is either no active part or the
	 *         active part is not a PositionsView
	 */
	static PositionsView getView() {
		IWorkbenchWindow active = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (active == null) return null;
		IWorkbenchPage page = active.getActivePage();
		if (page == null) return null;
		IWorkbenchPart part = page.getActivePart();
		if (!(part instanceof PositionsView)) return null;
		return (PositionsView) part;
	}
}
