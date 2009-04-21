package org.marketcetera.photon.internal.marketdata.ui;

import java.util.Comparator;

import net.miginfocom.swt.MigLayout;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.time.DateFormatUtils;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.emf.databinding.EMFObservables;
import org.eclipse.emf.databinding.EObjectObservableMap;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.photon.commons.ui.table.ColumnConfiguration;
import org.marketcetera.photon.commons.ui.table.TableConfiguration;
import org.marketcetera.photon.commons.ui.table.TableSupport;
import org.marketcetera.photon.marketdata.IMarketData;
import org.marketcetera.photon.marketdata.IMarketDataReference;
import org.marketcetera.photon.marketdata.MarketDataManager;
import org.marketcetera.photon.model.marketdata.MDDepthOfBook;
import org.marketcetera.photon.model.marketdata.MDPackage;
import org.marketcetera.photon.model.marketdata.MDQuote;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ObjectArrays;

/* $License$ */

/**
 * A view that displays market data. It is parameterized by its secondary id in the format:
 * 
 * <pre>
 * symbol,product
 * </pre>
 * 
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class MarketDepthView extends ViewPart {

	/**
	 * The view id
	 */
	public static final String ID = "org.marketcetera.photon.marketdata.MarketDepthView"; //$NON-NLS-1$

	/**
	 * The bids table context menu id
	 */
	public static final String BIDS_TABLE_POPUP = ID + ".bidsPopup"; //$NON-NLS-1$

	/**
	 * The asks table context menu id
	 */
	public static final String ASKS_TABLE_POPUP = ID + ".asksPopup"; //$NON-NLS-1$

	/**
	 * The secondary id parameter delimiter
	 */
	public static final String PARAMETER_DELIMITER = ","; //$NON-NLS-1$

	private final IMarketData mMarketData;
	private TableSupport mBidsTable;
	private TableSupport mAsksTable;
	private String mSymbol;
	private Content mProduct;
	private IMarketDataReference<MDDepthOfBook> mDataItem;

	/**
	 * Constructor.
	 */
	public MarketDepthView() {
		mMarketData = MarketDataManager.getCurrent().getMarketData();
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		String[] params = site.getSecondaryId().split(PARAMETER_DELIMITER);
		mSymbol = params[0];
		mProduct = Content.valueOf(params[1]);
	}

	@Override
	public void createPartControl(Composite parent) {
		final ScrolledComposite sc2 = new ScrolledComposite(parent, SWT.H_SCROLL);
		sc2.setExpandHorizontal(true);
		sc2.setExpandVertical(true);
		Composite composite = new Composite(sc2, SWT.NONE);
		sc2.setContent(composite);
		composite.setLayout(new MigLayout("ins 0, gap 0, fill", "[sg][sg]", "[][grow]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		createHeading(composite).setLayoutData("grow, span, wrap"); //$NON-NLS-1$

		mBidsTable = createTableSupport(getBidColumns());
		mBidsTable.createTable(composite);

		mBidsTable.getTableViewer().getTable().getParent().setLayoutData("grow, hmin 0, wmin 0"); //$NON-NLS-1$
		MenuManager menuManager = new MenuManager();
		getViewSite().registerContextMenu(BIDS_TABLE_POPUP, menuManager,
				mBidsTable.getTableViewer());
		mBidsTable.getTableViewer().getTable().setMenu(
				menuManager.createContextMenu(mBidsTable.getTableViewer().getTable()));
		mAsksTable = createTableSupport(getAskColumns());
		mAsksTable.createTable(composite);
		mAsksTable.getTableViewer().getTable().getParent().setLayoutData("grow, hmin 0, wmin 0"); //$NON-NLS-1$
		MenuManager menuManager2 = new MenuManager();
		getViewSite().registerContextMenu(ASKS_TABLE_POPUP, menuManager2,
				mAsksTable.getTableViewer());
		mAsksTable.getTableViewer().getTable().setMenu(
				menuManager2.createContextMenu(mAsksTable.getTableViewer().getTable()));
		sc2.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).x, 0);

		hookupData();
	}

	private void hookupData() {
		mDataItem = mMarketData.getDepthOfBook(mSymbol, mProduct);
		ObservableListContentProvider bids = new ObservableListContentProvider();
		mBidsTable.getTableViewer().setContentProvider(bids);
		mBidsTable.getTableViewer().setLabelProvider(
				new ObservableMapLabelProvider(ObjectArrays.concat(createTimeMap(bids),
						EMFObservables.observeMaps(bids.getKnownElements(),
								new EStructuralFeature[] { MDPackage.Literals.MD_QUOTE__SOURCE,
										MDPackage.Literals.MD_QUOTE__SIZE,
										MDPackage.Literals.MD_QUOTE__PRICE }))));
		setSortColumn(mBidsTable, new BidComparator(), 3, SWT.DOWN);
		mBidsTable.getTableViewer().setInput(
				EMFObservables.observeList(mDataItem.get(),
						MDPackage.Literals.MD_DEPTH_OF_BOOK__BIDS));

		ObservableListContentProvider asks = new ObservableListContentProvider();
		mAsksTable.getTableViewer().setContentProvider(asks);
		mAsksTable.getTableViewer().setLabelProvider(
				new ObservableMapLabelProvider(ObjectArrays.concat(EMFObservables.observeMaps(asks
						.getKnownElements(), new EStructuralFeature[] {
						MDPackage.Literals.MD_QUOTE__PRICE, MDPackage.Literals.MD_QUOTE__SIZE,
						MDPackage.Literals.MD_QUOTE__SOURCE }), createTimeMap(asks))));
		setSortColumn(mAsksTable, new AskComparator(), 0, SWT.UP);
		mAsksTable.getTableViewer().setInput(
				EMFObservables.observeList(mDataItem.get(),
						MDPackage.Literals.MD_DEPTH_OF_BOOK__ASKS));
	}

	private void setSortColumn(TableSupport tableSupport, final Comparator<MDQuote> comparator,
			int column, int dir) {
		TableViewer viewer = tableSupport.getTableViewer();
		Table table = viewer.getTable();
		table.setSortColumn(table.getColumn(column));
		table.setSortDirection(dir);
		viewer.setComparator(new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				MDQuote q1 = (MDQuote) e1;
				MDQuote q2 = (MDQuote) e2;
				return comparator.compare(q1, q2);
			}
		});

	}

	private IObservableMap createTimeMap(ObservableListContentProvider bids) {
		// a custom map that formats the time
		return new EObjectObservableMap(bids.getKnownElements(), MDPackage.Literals.MD_QUOTE__TIME) {
			@Override
			protected Object doGet(Object key) {
				long l = (Long) super.doGet(key);
				return DateFormatUtils.ISO_TIME_NO_T_FORMAT.format(l);
			}
		};
	}

	@Override
	public void dispose() {
		super.dispose();
		mDataItem.dispose();
	}

	private Composite createHeading(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.MARKET_DEPTH_VIEW_SYMBOL_LABEL.getText());
		{
			CLabel text = new CLabel(composite, SWT.BORDER);
			text.setText(mSymbol);
			text.setLayoutData("growx, w 120"); //$NON-NLS-1$
		}
		label = new Label(composite, SWT.NONE);
		label.setText(Messages.MARKET_DEPTH_VIEW_SOURCE_LABEL.getText());
		label.setLayoutData("gap 10"); //$NON-NLS-1$
		{
			CLabel text = new CLabel(composite, SWT.BORDER);
			text.setText(getProductLabel(mProduct));
			text.setLayoutData("growx, w 120"); //$NON-NLS-1$
		}
		composite.setLayout(new MigLayout());
		return composite;
	}

	private String getProductLabel(Content product) {
		switch (product) {
		case LEVEL_2:
			return Messages.MARKET_DEPTH_VIEW_LEVEL_2_LABEL.getText();
		case TOTAL_VIEW:
			return Messages.MARKET_DEPTH_VIEW_TOTAL_VIEW_LABEL.getText();
		case OPEN_BOOK:
			return Messages.MARKET_DEPTH_VIEW_OPEN_BOOK_LABEL.getText();
		default:
			// new type needs new label
			assert false : product;
			// we can just return the enum name
			return mProduct.toString();
		}
	}

	private TableSupport createTableSupport(ColumnConfiguration[] bidColumns) {
		return TableSupport.create(TableConfiguration.defaults().tableStyle(
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION).columns(
				bidColumns).headerVisible(true));
	}

	private ColumnConfiguration[] getBidColumns() {
		return new ColumnConfiguration[] {
				ColumnConfiguration.defaults().heading(
						Messages.MARKET_DEPTH_VIEW_TIME_COLUMN_LABEL.getText()).sortable(false),
				ColumnConfiguration.defaults().heading(
						Messages.MARKET_DEPTH_VIEW_MPID_COLUMN_LABEL.getText()).sortable(false),
				ColumnConfiguration.defaults().heading(
						Messages.MARKET_DEPTH_VIEW_SIZE_COLUMN_LABEL.getText()).style(SWT.RIGHT)
						.sortable(false),
				ColumnConfiguration.defaults().heading(
						Messages.MARKET_DEPTH_VIEW_BID_PRICE_COLUMN_LABEL.getText()).style(
						SWT.RIGHT).layoutData(new ColumnWeightData(10, 60)).sortable(false)
						.comparator(new BidComparator()) };
	}

	private ColumnConfiguration[] getAskColumns() {
		return new ColumnConfiguration[] {
				ColumnConfiguration.defaults().heading(
						Messages.MARKET_DEPTH_VIEW_ASK_PRICE_COLUMN_LABEL.getText()).layoutData(
						new ColumnWeightData(10, 60)).sortable(false).comparator(
						new AskComparator()),
				ColumnConfiguration.defaults().heading(
						Messages.MARKET_DEPTH_VIEW_SIZE_COLUMN_LABEL.getText()).sortable(false),
				ColumnConfiguration.defaults().heading(
						Messages.MARKET_DEPTH_VIEW_MPID_COLUMN_LABEL.getText()).sortable(false),
				ColumnConfiguration.defaults().heading(
						Messages.MARKET_DEPTH_VIEW_TIME_COLUMN_LABEL.getText()).sortable(false) };
	}

	@Override
	public void setFocus() {
		mBidsTable.setFocus();
	}

	/**
	 * Compares {@link MDQuote} based on ascending price, then descending time.
	 */
	@ClassVersion("$Id$")
	private static class AskComparator implements Comparator<MDQuote> {

		@Override
		public int compare(MDQuote o1, MDQuote o2) {
			// this comparator is only intended to be used on the ask list which does not have null
			// elements
			assert o1 != null;
			assert o2 != null;
			// lower price, higher time comes first
			return new CompareToBuilder().append(o1.getPrice(), o2.getPrice()).append(o2.getTime(),
					o1.getTime()).toComparison();
		}

	}

	/**
	 * Compares {@link MDQuote} based on descending price, then descending time.
	 */
	@ClassVersion("$Id$")
	private static class BidComparator implements Comparator<MDQuote> {

		@Override
		public int compare(MDQuote o1, MDQuote o2) {
			// this comparator is only intended to be used on the bid list which does not have null
			// elements
			assert o1 != null;
			assert o2 != null;
			// higher price, higher time comes first
			return new CompareToBuilder().append(o2.getPrice(), o1.getPrice()).append(o2.getTime(),
					o1.getTime()).toComparison();
		}

	}

}
