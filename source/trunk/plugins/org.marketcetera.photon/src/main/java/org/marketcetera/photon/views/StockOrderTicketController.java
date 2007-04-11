package org.marketcetera.photon.views;

import java.math.BigDecimal;

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.core.MSymbol;
import org.marketcetera.marketdata.ConjunctionMessageSelector;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MessageTypeSelector;
import org.marketcetera.marketdata.SymbolMessageSelector;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.marketcetera.photon.marketdata.MarketDataFeedTracker;
import org.marketcetera.photon.ui.validation.fix.AbstractFIXExtractor;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;

public class StockOrderTicketController {
	IStockOrderTicket ticket;

	private MarketDataFeedTracker marketDataTracker;
	
	private MSymbol listenedSymbol = null;

	private ConjunctionMessageSelector currentSubscription;
	
	private MarketDataListener marketDataListener;

	private Message targetOrder;



	public StockOrderTicketController(IStockOrderTicket ticket) {
		this.ticket = ticket;
		marketDataTracker = new MarketDataFeedTracker(PhotonPlugin.getDefault().getBundleContext());
		marketDataTracker.open();
		
		marketDataListener = new MarketDataListener(){
			public void onLevel2Quote(Message aQuote) {
				StockOrderTicketController.this.onQuote(aQuote);
			}

			public void onQuote(Message aQuote) {
				StockOrderTicketController.this.onQuote(aQuote);
			}

			public void onTrade(Message aTrade) {
			}
			
		};
		marketDataTracker.setMarketDataListener(marketDataListener);

		ticket.getSymbolText().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				((Text) e.widget).selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				listenMarketData(((Text) e.widget).getText());
			}
		});
		
		ticket.getSymbolText().addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				Text symbolText = (Text) e.widget;
				if (!symbolText.isFocusControl())
				{
					String symbolTextString = symbolText.getText();
					if (symbolTextString == null || symbolTextString.length()==0){
						unlisten();
					} else {
						listenMarketData(symbolTextString);
					}
				}
			}
		});

		ticket.getCancelButton().addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				handleCancel();
			}
		});
		ticket.getSendButton().addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				handleSend();
			}
		});

	}
	
	public void dispose()
	{
		marketDataTracker.setMarketDataListener(null);
		marketDataTracker.close();
	}

	
	protected void listenMarketData(String symbol) {
		unlisten();
		if (symbol != null && !"".equals(symbol)){
			MSymbol newListenedSymbol = new MSymbol(symbol);
			MarketDataFeedService service = marketDataTracker.getMarketDataFeedService();
			
			if (service != null
					&& !newListenedSymbol.equals(listenedSymbol)) {
				ConjunctionMessageSelector subscription = new ConjunctionMessageSelector(
												new SymbolMessageSelector(newListenedSymbol),
												new MessageTypeSelector(false, false, true));
				service.subscribe(subscription);
				listenedSymbol = newListenedSymbol;
				currentSubscription = subscription;
			}
		}
	}

	protected void unlisten() {
		MarketDataFeedService service = marketDataTracker
				.getMarketDataFeedService();

		if (service != null) {
			if (currentSubscription != null) {
				service.unsubscribe(currentSubscription);
				listenedSymbol = null;
				currentSubscription = null;
			}
		}
		ticket.getBookComposite().setInput(null);
	}

	public void onQuote(Message message) {
		try {
			if (listenedSymbol!=null){
				String listenedSymbolString = listenedSymbol.toString();
				if (message.isSetField(Symbol.FIELD) &&
						listenedSymbolString.equals(message.getString(Symbol.FIELD))){
					ticket.getBookComposite().onQuote(message);
				}
			}
		} catch (FieldNotFound e) {
			// Do nothing
		}
	}
	
	public void handleSend() {
		try {
			if (ticket.validateAll()) {
				Message aMessage;
				PhotonPlugin plugin = PhotonPlugin.getDefault();
				if (targetOrder == null) {
					String orderID = plugin.getIDFactory().getNext();
					aMessage = plugin.getMessageFactory()
							.newLimitOrder(orderID, Side.BUY,
									BigDecimal.ZERO, new MSymbol(""),
									BigDecimal.ZERO, TimeInForce.DAY, null);
					aMessage.removeField(Side.FIELD);
					aMessage.removeField(OrderQty.FIELD);
					aMessage.removeField(Symbol.FIELD);
					aMessage.removeField(Price.FIELD);
					aMessage.removeField(TimeInForce.FIELD);
				} else {
					aMessage = targetOrder;
				}
				ticket.updateMessage(aMessage);
				plugin.getPhotonController().handleInternalMessage(aMessage);
				ticket.clear();
			}
		} catch (Exception e) {
			PhotonPlugin.getMainConsoleLogger().error(
					"Error sending order: " + e.getMessage(), e);
		}
	}


	protected void handleCancel() {
		ticket.clear();
	}

	public void showMessage(Message aMessage){
		targetOrder = aMessage;
		ticket.showMessage(aMessage);
	}

}
