package org.marketcetera.photon.views;

import java.lang.reflect.Field;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.photon.IFieldIdentifier;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.ui.EnumTableFormat;
import org.marketcetera.photon.ui.Level2ContentProvider;

import quickfix.FieldMap;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryTime;
import quickfix.field.MDEntryType;
import quickfix.field.MDMkt;

/* $License$ */

/**
 * This class implements the view that provides the end user
 * the ability to type in--and graphically interact with--stock orders.
 * 
 * Additionally this class manages the stock market data that can be displayed
 * along with the order ticket itself.
 * 
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class StockOrderTicketView extends OrderTicketView {

	private static final String NEW_EQUITY_ORDER = "New Equity Order"; //$NON-NLS-1$

	private static final String REPLACE_EQUITY_ORDER = "Replace Equity Order"; //$NON-NLS-1$

	public static String ID = "org.marketcetera.photon.views.StockOrderTicketView"; //$NON-NLS-1$
	

	public StockOrderTicketView() 
	{
	}
	
	/**
	 * Sets the input for the market data viewers to null, then calls
	 * {@link OrderTicketView#dispose()}
	 */
	@Override
	public void dispose() {
		try {
			getStockOrderTicket().getLevel2BidTableViewer().setInput(null);
			getStockOrderTicket().getLevel2OfferTableViewer().setInput(null);
		} catch (Exception ex) {}
		super.dispose();
	}

	@Override
	protected String getXSWTResourceName() {
		return "/stock_order_ticket.xswt"; //$NON-NLS-1$
	}


	@Override
	protected void setDefaultInput() {
		setInput(PhotonPlugin.getDefault().getStockOrderTicketModel());
	}


	/**
	 * This method simply finishes the setup for the market data tables,
	 * including setting the {@link LabelProvider}, and {@link IContentProvider}
	 * for each table viewer.
	 * 
	 */
	protected void finishUI() {
		super.finishUI();
		
		final IStockOrderTicket ticket = getStockOrderTicket();

		TableViewer bidViewer = ticket.getLevel2BidTableViewer();
		TableViewer offerViewer = ticket.getLevel2OfferTableViewer();
		bidViewer.setLabelProvider(new EnumTableFormat<FieldMap>(bidViewer.getTable(), BookColumns.values()));
		bidViewer.setContentProvider(new Level2ContentProvider(MDEntryType.BID,
		                                                       bids));
		packColumns(bidViewer.getTable());

		offerViewer.setLabelProvider(new EnumTableFormat<FieldMap>(offerViewer.getTable(), BookColumns.values()));
		offerViewer.setContentProvider(new Level2ContentProvider(MDEntryType.OFFER,
		                                                         offers));
		packColumns(offerViewer.getTable());
	}

	private final WritableList bids = new WritableList();
	private final WritableList offers = new WritableList();
	
	/**
	 * Calls {@link OrderTicketView#setInput(OrderTicketModel)}.
	 * Binds the model to the tables for market data.
	 */
	@Override
	public void setInput(OrderTicketModel inModel)
	{
		super.setInput(inModel);
		StockOrderTicketModel model = (StockOrderTicketModel)inModel;
		model.subscribe(new ISubscriber() {
            public boolean isInteresting(Object inData)
            {
                return inData instanceof StockOrderTicketModel.OrderTicketPublication;
            }
            public void publishTo(final Object inData)
            {
                final Runnable action = new Runnable() {
                    public void run()
                    {
                        try {
                            StockOrderTicketModel.OrderTicketPublication publication = (StockOrderTicketModel.OrderTicketPublication)inData;
                            if(publication.getMessage() != null) {
                                if(publication.getType().equals(OrderTicketModel.OrderTicketPublication.Type.BID)) {
                                    bids.clear();
                                    bids.add(publication.getMessage());
                                } else {
                                    offers.clear();
                                    offers.add(publication.getMessage());
                                }
                            }
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }                    
                };
                Display theDisplay = Display.getDefault();
                if (theDisplay.getThread() == Thread.currentThread()){
                    action.run();
                } else { 
                    theDisplay.asyncExec(action);
                }
            }		    
		});
		IStockOrderTicket stockTicket = getStockOrderTicket();
		stockTicket.getLevel2BidTableViewer().setInput(bids);
		stockTicket.getLevel2OfferTableViewer().setInput(offers);
	}

	/**
	 * Get a reference to the underlying {@link IStockOrderTicket} representation,
	 * which at this time is an XSWT proxy object.
	 * @return the XSWT proxy object
	 */
	private IStockOrderTicket getStockOrderTicket() {
		return (IStockOrderTicket) getOrderTicket();
	}

	@Override
	protected String getReplaceOrderString() {
		return REPLACE_EQUITY_ORDER;
	}

	protected String getNewOrderString() {
		return NEW_EQUITY_ORDER;
	}


	/**
	 * Gets the "default" StockOrderTicketView, that is the first one returned
	 * by {@link IWorkbenchPage#findView(String)}
	 * @return the default StockOrderTicketView
	 */
	public static StockOrderTicketView getDefault() {
		return (StockOrderTicketView) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().findView(
						StockOrderTicketView.ID);
	}


	@Override
	protected Class<? extends IOrderTicket> getXSWTInterfaceClass() {
		return IStockOrderTicket.class;
	}
	
	public enum BookColumns implements IFieldIdentifier {
		MDMKT(MDMkt.class), MDENTRYPX(MDEntryPx.class), MDENTRYSIZE(MDEntrySize.class),
		MDENTRYTIME(MDEntryTime.class);

		private String name;
		private Integer fieldID;
		private Integer groupID;
		private Integer groupDiscriminatorID;
		private Object groupDiscriminatorValue;

		BookColumns(Class<?> clazz) {
			name = clazz.getSimpleName();
			try {
				Field fieldField = clazz.getField("FIELD"); //$NON-NLS-1$
				fieldID = (Integer) fieldField.get(null);
			} catch (Throwable t){
				assert(false);
			}
		}

		public String toString() {
			return name;
		}

		public Integer getFieldID() {
			return fieldID;
		}
		
		public Integer getGroupID() {
			return groupID;
		}

		public Integer getGroupDiscriminatorID() {
			return groupDiscriminatorID;
		}

		public Object getGroupDiscriminatorValue() {
			return groupDiscriminatorValue;
		}

	};
}
