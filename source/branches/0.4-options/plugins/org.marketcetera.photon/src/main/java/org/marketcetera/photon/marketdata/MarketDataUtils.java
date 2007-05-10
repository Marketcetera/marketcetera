package org.marketcetera.photon.marketdata;

import org.marketcetera.core.MSymbol;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.Group;
import quickfix.Message;
import quickfix.field.MarketDepth;
import quickfix.field.MsgType;
import quickfix.field.NoMDEntryTypes;
import quickfix.field.NoRelatedSym;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.Symbol;

public class MarketDataUtils {

	static FIXMessageFactory messageFactory = FIXVersion.FIX44.getMessageFactory();
	
	public static Message newSubscribeLevel2(MSymbol symbol) {
		Message message = newSubscribeHelper(symbol);
		message.setField(new MarketDepth(0)); // full book
		
		return message;
	}
	
	public static Message newSubscribeBBO(MSymbol symbol){
		Message message = newSubscribeHelper(symbol);
		message.setField(new MarketDepth(1)); // top-of-book
		return message;
	}
	private static Message newSubscribeHelper(MSymbol symbol) {
		Message message = messageFactory.createMessage(MsgType.MARKET_DATA_REQUEST);
		message.setField(new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES));
		message.setField(new NoMDEntryTypes(0));
		Group relatedSymGroup = messageFactory.createGroup(MsgType.MARKET_DATA_REQUEST, NoRelatedSym.FIELD);
		relatedSymGroup.setField(new Symbol(symbol.toString()));
		message.addGroup(relatedSymGroup);
		return message;
	}
	

}
