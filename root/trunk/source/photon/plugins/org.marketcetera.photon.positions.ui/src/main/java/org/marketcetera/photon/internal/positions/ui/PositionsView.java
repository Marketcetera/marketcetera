package org.marketcetera.photon.internal.positions.ui;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import net.miginfocom.swt.MigLayout;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
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
 * Real-time Positions and P&L view.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class PositionsView extends PageBookView implements IColumnProvider {

	/**
	 * Dummy part that allows us to take advantage of {@link PageBookView} infrastructure to support
	 * switching between table and tree UI.
	 * 
	 * This technique was inspired by
	 * org.eclipse.pde.internal.ui.views.dependencies.DependenciesView.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since $Release$
	 */
	@ClassVersion("$Id$")
	static enum PositionsPart implements IWorkbenchPart {

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

		private PositionsPart part;

		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			PositionsView view = (PositionsView) HandlerUtil.getActivePart(event);
			if (view != null) {
				switch (part) {
				case HIERARCHICAL:
					view.grouping = view.lastGrouping;
					view.partActivated(part);
					break;
				case FLAT:
					if (view.grouping != null) view.lastGrouping = view.grouping;
					view.grouping = null;
					// dispose the last tree page if it exists to free up resources
					view.partClosed(PositionsPart.HIERARCHICAL);
					view.partActivated(part);
					break;
				}
			}
			return null;
		}

		@Override
		public void setInitializationData(IConfigurationElement config, String propertyName,
				Object data) throws CoreException {
			part = PositionsPart.valueOf((String) data);
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

	private static final String DEFAULT_PART_KEY = "defaultPart"; //$NON-NLS-1$
	private static final String GROUPING_KEY_1 = "grouping1"; //$NON-NLS-1$
	private static final String GROUPING_KEY_2 = "grouping2"; //$NON-NLS-1$

	private final EnumMap<PositionsPart, IPageBookViewPage> mPartsToPages = new EnumMap<PositionsPart, IPageBookViewPage>(
			PositionsPart.class);
	private final Map<IPageBookViewPage, PositionsPart> mPagesToParts = new HashMap<IPageBookViewPage, PositionsPart>();
	private PositionsPart defaultPart = PositionsPart.FLAT;
	private Grouping[] grouping = null;
	private Grouping[] lastGrouping = new Grouping[] { Grouping.Symbol, Grouping.Account };
	private String filterText = ""; //$NON-NLS-1$
	private IMemento mMemento;
	private MenuManager mMenuManager;

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		mMemento = memento;
		if (memento != null) {
			try {
				String part = memento.getString(DEFAULT_PART_KEY);
				if (part != null) {
					defaultPart = PositionsPart.valueOf(part);
				}
			} catch (IllegalArgumentException e) {
				Messages.POSITIONS_VIEW_STATE_RESTORE_FAILURE.error(this, e);
			}
			try {
				String one = memento.getString(GROUPING_KEY_1);
				String two = memento.getString(GROUPING_KEY_2);
				if (one != null && two != null) {
					lastGrouping = new Grouping[] { Grouping.valueOf(one), Grouping.valueOf(two) };
				}
			} catch (IllegalArgumentException e) {
				Messages.POSITIONS_VIEW_STATE_RESTORE_FAILURE.error(this, e);
			}
			if (defaultPart == PositionsPart.HIERARCHICAL) {
				grouping = lastGrouping;
			}
		}
		mMenuManager = new MenuManager();
		site.registerContextMenu(mMenuManager, getSelectionProvider());
	}

	@Override
	public void saveState(IMemento memento) {
		memento.putString(DEFAULT_PART_KEY, mPagesToParts.get(getCurrentPage()).toString());
		Grouping[] savedGrouping = grouping != null ? grouping : lastGrouping;
		memento.putString(GROUPING_KEY_1, savedGrouping[0].name());
		memento.putString(GROUPING_KEY_2, savedGrouping[1].name());
		getCurrentPage().saveState(memento);
	}

	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		// We don't care about the active workbench part, just dummy parts "activated" by this view
		return part instanceof PositionsPart;
	}

	@Override
	public PositionsViewPage getCurrentPage() {
		return (PositionsViewPage) super.getCurrentPage();
	}

	@Override
	public Control getColumnWidget() {
		return getCurrentPage().getColumnWidget();
	}

	@Override
	protected IWorkbenchPart getBootstrapPart() {
		return defaultPart;
	}

	@Override
	protected IPage createDefaultPage(PageBook book) {
		return createPage(PositionsPart.FLAT);
	}

	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		IPageBookViewPage page = mPartsToPages.get(part);
		if (page == null && !mPartsToPages.containsKey(part)) {
			page = createPage((PositionsPart) part);
		}
		if (page != null) {
			return new PageRec(part, page);
		}
		return null;
	}

	@Override
	protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
		IPage page = pageRecord.page;
		page.dispose();
		pageRecord.dispose();

		// empty cross-reference cache
		mPartsToPages.remove(part);
		mPagesToParts.remove(page);
	}

	private IPageBookViewPage createPage(final PositionsPart part) {
		final IPageBookViewPage page;
		switch (part) {
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
		mPartsToPages.put(part, page);
		mPagesToParts.put(page, part);
		return page;
	}

	@Override
	protected void showPageRec(PageRec pageRec) {
		super.showPageRec(pageRec);
		installContextMenu();
		getCurrentPage().setFilterText(getFilterText());
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
		this.filterText = filterText;
		getCurrentPage().setFilterText(filterText);
	}

	/**
	 * @return the current filter text
	 */
	String getFilterText() {
		return filterText;
	}

	/**
	 * @return the current grouping
	 */
	Grouping[] getGrouping() {
		return grouping;
	}

	/**
	 * Sets the view grouping.
	 * 
	 * @param grouping
	 *            the new grouping
	 */
	void setGrouping(Grouping[] grouping) {
		if (!Arrays.equals(this.grouping, grouping)) {
			this.grouping = grouping;
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
