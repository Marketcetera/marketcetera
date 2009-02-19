package org.marketcetera.photon.internal.positions.ui;

import java.util.EnumMap;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
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
public class PositionsView extends PageBookView {

	/**
	 * Dummy part that allows us to take advantage of {@link PageBookView}
	 * infrastructure to support switching between table and tree UI.
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
		TABLE,

		/**
		 * Grouped tree UI.
		 */
		TREE;

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

	private final EnumMap<PositionsPart, IPageBookViewPage> mPartsToPages = new EnumMap<PositionsPart, IPageBookViewPage>(
			PositionsPart.class);

	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		// We don't care about the active part, but this could be one of our
		// dummy parts
		return part instanceof PositionsPart;
	}

	@Override
	protected IWorkbenchPart getBootstrapPart() {
		return getDefaultPart();
	}

	@Override
	protected IPage createDefaultPage(PageBook book) {
		return createPage(getDefaultPart());
	}

	private PositionsPart getDefaultPart() {
		// TODO: use preferences
		return PositionsPart.TABLE;
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
	}

	private IPageBookViewPage createPage(final PositionsPart part) {
		final IPageBookViewPage page;
		switch (part) {
		case TABLE:
			page = new PositionsViewTablePage(this);
			break;
		case TREE:
			// TODO: implement tree
			page = new PositionsViewTablePage(this);
			break;
		default:
			throw new AssertionError("Invalid part"); //$NON-NLS-1$
		}

		initPage(page);
		page.createControl(getPageBook());
		mPartsToPages.put(part, page);
		return page;
	}

}
