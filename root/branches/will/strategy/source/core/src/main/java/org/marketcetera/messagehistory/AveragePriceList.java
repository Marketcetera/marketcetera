package org.marketcetera.messagehistory;

import java.util.ArrayList;
import java.util.HashMap;

import org.marketcetera.core.MSymbol;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.Account;
import quickfix.field.AvgPx;
import quickfix.field.CumQty;
import quickfix.field.LastPx;
import quickfix.field.LastShares;
import quickfix.field.LeavesQty;
import quickfix.field.MsgType;
import quickfix.field.OrderQty;
import quickfix.field.Side;
import quickfix.field.Symbol;
import ca.odell.glazedlists.AbstractEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

public class AveragePriceList extends AbstractEventList<MessageHolder> implements ListEventListener<MessageHolder> {

	private HashMap<SymbolSide, Integer> averagePriceIndexes = new HashMap<SymbolSide, Integer>();
	private ArrayList<MessageHolder> averagePricesList = new ArrayList<MessageHolder>();
	
	private FIXMessageFactory messageFactory;
	
	public AveragePriceList(FIXMessageFactory messageFactory, EventList<MessageHolder> source) {
		super(source.getPublisher());
		this.messageFactory = messageFactory;
		source.addListEventListener(this);

		readWriteLock = source.getReadWriteLock();
	}

	public void listChanged(ListEvent<MessageHolder> listChanges) {
        // all of these changes to this list happen "atomically"
        updates.beginEvent();

        // handle reordering events
        if(!listChanges.isReordering()) {
            // for all changes, one index at a time
            while(listChanges.next()) {

                // get the current change info
                int changeType = listChanges.getType();

                EventList<MessageHolder> sourceList = listChanges.getSourceList();
            	// handle delete events
                if(changeType == ListEvent.DELETE || changeType == ListEvent.UPDATE) {
                	throw new UnsupportedOperationException();
                } else if(changeType == ListEvent.INSERT) {
	            	MessageHolder deltaMessageHolder = sourceList.get(listChanges.getIndex());
	
	            	Integer averagePriceIndex = null;
	            	
	            	try {
		            	Message deltaMessage = deltaMessageHolder.getMessage();
						String symbol = deltaMessage.getString(Symbol.FIELD);
						String side = deltaMessage.getString(Side.FIELD);
						SymbolSide symbolSide = new SymbolSide(new MSymbol(symbol), side);
						averagePriceIndex = averagePriceIndexes.get(symbolSide);
		
						if(averagePriceIndex != null) {
	                    	MessageHolder averagePriceMessageHolder = averagePricesList.get(averagePriceIndex);
		                    Message averagePriceMessage = averagePriceMessageHolder.getMessage();
	
		                    if (deltaMessageHolder instanceof IncomingMessageHolder && FIXMessageUtil.isExecutionReport(deltaMessage) &&
	                    			deltaMessage.getDouble(LastShares.FIELD) > 0){
	
		                    	double existingCumQty = 0.0; 
		                    	try { existingCumQty = averagePriceMessage.getDouble(CumQty.FIELD); } catch (FieldNotFound fnf) {}
		            			double existingAvgPx = 0.0;
		            			try { existingAvgPx = averagePriceMessage.getDouble(AvgPx.FIELD); } catch (FieldNotFound fnf) {}
		            			double newLastQty = deltaMessage.getDouble(LastShares.FIELD);
		            			double newLastPx = deltaMessage.getDouble(LastPx.FIELD);
		            			double newTotal = existingCumQty + newLastQty;
		            			if (newTotal != 0.0){
		            				double numerator = (existingCumQty * existingAvgPx)+(newLastQty * newLastPx);
		            				double newAvgPx = numerator / newTotal;
		            				averagePriceMessage.setDouble(AvgPx.FIELD, newAvgPx);
		            				averagePriceMessage.setDouble(CumQty.FIELD, newTotal);
		            			}
	                    	} else if (FIXMessageUtil.isOrderSingle(deltaMessage)){
	                    		double orderQty = 0.0;
	                    		try { 
	                    			try { orderQty = averagePriceMessage.getDouble(OrderQty.FIELD); } catch (FieldNotFound fnf) {}
	                    			orderQty = orderQty + deltaMessage.getDouble(OrderQty.FIELD);
	                    			averagePriceMessage.setDouble(OrderQty.FIELD, orderQty);
	                    		} catch (FieldNotFound fnf) {}
	                    	}	
			                updates.addUpdate(averagePriceIndex);
						} else {
		                    if ((FIXMessageUtil.isExecutionReport(deltaMessage) &&
	                    			deltaMessage.getDouble(LastShares.FIELD) > 0) || FIXMessageUtil.isOrderSingle(deltaMessage)){
		            			Message averagePriceMessage = messageFactory.createMessage(MsgType.EXECUTION_REPORT);
		            			averagePriceMessage.setField(deltaMessage.getField(new Side()));
		            			averagePriceMessage.setField(deltaMessage.getField(new Symbol()));
		            			if (FIXMessageUtil.isOrderSingle(deltaMessage)){
			            			averagePriceMessage.setField(new OrderQty(deltaMessage.getDouble(OrderQty.FIELD)));
		            			} else {
			            			averagePriceMessage.setField(deltaMessage.getField(new LeavesQty()));
			            			averagePriceMessage.setField(new StringField(CumQty.FIELD, deltaMessage.getString(LastShares.FIELD)));
			            			averagePriceMessage.setField(new StringField(AvgPx.FIELD, deltaMessage.getString(LastPx.FIELD)));
		            			}
		            			try { averagePriceMessage.setField(deltaMessage.getField(new Account())); } catch (FieldNotFound ex) { /* do nothing */ }
	
		                    	averagePricesList.add(new IncomingMessageHolder(averagePriceMessage));
		                    	averagePriceIndex = averagePricesList.size()-1;
		                    	averagePriceIndexes.put(symbolSide, averagePriceIndex);
				                updates.addInsert(averagePriceIndex);
		                    }
	                	}
						// if this value was not filtered out, it is now so add a change
	
	            	} catch (FieldNotFound fnf){
	            		// ignore...
	            	}
                }
            }
        }

        // commit the changes and notify listeners
        updates.commitEvent();
	}

	@Override
	public MessageHolder get(int index) {
		return averagePricesList.get(index);
	}

	@Override
	public int size() {
		return averagePricesList.size();
	}

}
