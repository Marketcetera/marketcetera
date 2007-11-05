package org.marketcetera.photon.views;

import java.text.SimpleDateFormat;
import java.util.Iterator;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.commands.SendOrderToOrderManagerCommand;
import org.marketcetera.photon.core.TradeRecommendation;
import org.marketcetera.photon.parser.OrderFormatter;

import quickfix.Message;

public class TradeRecommendationView extends ViewPart implements KeyListener, IDoubleClickListener {

	public static final String ID = "org.marketcetera.photon.views.TradeRecommendationView";
	private Table targetTable;
	private static WritableList modelList;
	private TableViewer tableViewer;

	public TradeRecommendationView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		modelList = new WritableList();
		DataBindingContext context = new DataBindingContext();
		
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		toolBarManager.add(new SendSelectedItemsAction());
		toolBarManager.add(new DeleteItemsAction());
		toolBarManager.add(new DeleteAllItemsAction());
		
		targetTable = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		targetTable.setHeaderVisible(true);
		targetTable.setLinesVisible(true);
		targetTable.addKeyListener(this);
		bindGUI(context, modelList);
	}
	
	public static void addTradeRecommendation(final Message orderMessage, final Double score){
		modelList.getRealm().asyncExec(new Runnable() {
			public void run() {
				modelList.add(new TradeRecommendation(orderMessage, score));
			}
		});
	}
	
	protected void bindGUI(DataBindingContext bindingContext, WritableList theModelList) {
		tableViewer = new TableViewer(targetTable);
		TableViewerColumn timeColumn = new TableViewerColumn(tableViewer,
				SWT.NONE);
		timeColumn.getColumn().setWidth(100);
		timeColumn.getColumn().setText("Time");
		tableViewer.addDoubleClickListener(this);
		
		TableViewerColumn tradeColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		tradeColumn.getColumn().setWidth(100);
		tradeColumn.getColumn().setText("Trade");

		TableViewerColumn scoreColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		scoreColumn.getColumn().setWidth(100);
		scoreColumn.getColumn().setText("Score");

		// Create a standard content provider
		ObservableListContentProvider contentProvider = new ObservableListContentProvider();
		tableViewer.setContentProvider(contentProvider);

		// And a standard label provider that maps columns
		IObservableMap[] attributeMaps = BeansObservables.observeMaps(
				contentProvider.getKnownElements(),
				TradeRecommendation.class, new String[] {"creationDate", "stringRepresentation", "score" });
		tableViewer.setLabelProvider(new MyObservableMapLabelProvider(attributeMaps));

		// Now set the Viewer's input
		tableViewer.setInput(theModelList);

//		// bind selectedCommitter label to the name of the current selection
//		IObservableValue selection = ViewersObservables
//				.observeSingleSelection(peopleViewer);
//		bindingContext.bindValue(SWTObservables
//				.observeText(selectedCommitter), BeansObservables
//				.observeDetailValue(Realm.getDefault(), selection, "name",
//						String.class), null, null);

//		bindingContext.bindValue(
//				SWTObservables.observeText(monthLabel),
//				BeansObservables.observeDetailValue(Realm.getDefault(), selection, "month", Month.class),
//				null,
//				null);
	}


	
	
	@Override
	public void setFocus() {
		targetTable.setFocus();
	}
	
	class DeleteItemsAction extends Action {

		private static final String ID = "org.marketcetera.photon.views.TradeRecommendationView.DeleteItemsAction";

		public DeleteItemsAction() {
			super("&Delete selected item(s)", AS_PUSH_BUTTON);
			setId(ID);
			setToolTipText("Delete selected item(s)");
			setImageDescriptor(PhotonPlugin.getImageDescriptor(IImageKeys.DELETE_ITEM));
			setChecked(false);
		}

		@Override
		public void run() {
			deleteSelected();
			//addTradeRecommendation(new NewOrderSingle());
		}
	}

	class DeleteAllItemsAction extends Action {

		private static final String ID = "org.marketcetera.photon.views.TradeRecommendationView.DeleteAllItemsAction";

		public DeleteAllItemsAction() {
			super("Delete &all items", AS_PUSH_BUTTON);
			setId(ID);
			setToolTipText("Delete all items");
			setImageDescriptor(PhotonPlugin.getImageDescriptor(IImageKeys.DELETE_ALL));
			setChecked(false);
		}

		@Override
		public void run() {
			deleteAll();
		}
	}

	class SendSelectedItemsAction extends Action {

		private static final String ID = "org.marketcetera.photon.views.TradeRecommendationView.SendItemsAction";

		public SendSelectedItemsAction() {
			super("&Send selected item(s)", AS_PUSH_BUTTON);
			setId(ID);
			setToolTipText("Send selected item(s)");
			setImageDescriptor(PhotonPlugin.getImageDescriptor(IImageKeys.SEND_ITEM));
			setChecked(false);
		}

		@Override
		public void run() {
			sendSelected();
		}
	}

	
	class MyObservableMapLabelProvider extends ObservableMapLabelProvider {
		OrderFormatter orderFormatter = new OrderFormatter(PhotonPlugin.getDefault().getFIXDataDictionary().getDictionary());
		private IObservableMap[] attributeMaps;

		public MyObservableMapLabelProvider(IObservableMap[] attributeMaps){
			super(attributeMaps);
			this.attributeMaps = attributeMaps;
		}
		@Override
		public String getColumnText(Object element, int columnIndex) {
			Object result = attributeMaps[columnIndex].get(element);
			if (result == null){
				return "";
			} else if (columnIndex == 0){
				SimpleDateFormat format = new SimpleDateFormat("HH:mm");
				return format.format(result);
			} else if (columnIndex == 1){
				TradeRecommendation tradeRec = (TradeRecommendation)element;
				return orderFormatter.format(tradeRec.getMessage());
			} else {
				return result.toString(); //$NON-NLS-1$
			}
		}
	}
	
	private void deleteSelected() {
		ISelection selection = tableViewer.getSelection();
		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			Iterator it = structuredSelection.iterator();
			while (it.hasNext()){
				modelList.remove(it.next());
			}
		}
	}
	
	public void sendSelected() {
		ISelection selection = tableViewer.getSelection();
		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			Iterator it = structuredSelection.iterator();
			while (it.hasNext()){
				modelList.remove(it.next());
			}
		}
	}

	private void deleteAll(){
		modelList.clear();
	}

	public void keyPressed(KeyEvent e) {
		// do nothing
	}

	public void keyReleased(KeyEvent e) {
		if (e.keyCode == SWT.DEL)
		{
			deleteSelected();
		}
	}

	public void doubleClick(DoubleClickEvent event) {
		Object source = event.getSource();
		if (source instanceof TableViewer) {
			TableViewer tableViewer = (TableViewer) source;
			ISelection selection = tableViewer.getSelection();
			if (selection instanceof StructuredSelection) {
				StructuredSelection structuredSelection = (StructuredSelection) selection;
				Object tradeRec = structuredSelection.getFirstElement();
				Message orderMessage = ((TradeRecommendation) tradeRec).getMessage();
				if (orderMessage!= null){
					SendOrderToOrderManagerCommand.sendOrder(orderMessage);
				}
				modelList.remove(tradeRec);
			}
		}
		
	}

}
