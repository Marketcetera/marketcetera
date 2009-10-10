package org.marketcetera.photon.ui.marketdata;

import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.Instrument;

import quickfix.Group;
import quickfix.Message;
import quickfix.field.MarketDepth;
import quickfix.field.MsgType;
import quickfix.field.NoMDEntryTypes;
import quickfix.field.NoRelatedSym;
import quickfix.field.NoUnderlyings;
import quickfix.field.SecurityType;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.Symbol;
import quickfix.field.UnderlyingSymbol;

@Deprecated
public class MarketDataUtils {

	static final String UTC_TIME_ZONE = "UTC"; //$NON-NLS-1$
	
	static FIXMessageFactory messageFactory = FIXVersion.FIX44
			.getMessageFactory();

	public static Message newSubscribeLevel2(Instrument instrument) {
		Message message = newSubscribeHelper(instrument, null);
		message.setField(new MarketDepth(0)); // full book

		return message;
	}

	public static Message newSubscribeBBO(Instrument instrument) {
		Message message = newSubscribeHelper(instrument, null);
		message.setField(new MarketDepth(1)); // top-of-book
		return message;
	}

	public static Message newSubscribeBBO(Instrument instrument, String securityType) {
		Message message = newSubscribeHelper(instrument, securityType);
		message.setField(new MarketDepth(1)); // top-of-book
		return message;
	}

	private static Message newSubscribeHelper(Instrument instrument, String securityType) {
		Message message = messageFactory
				.createMessage(MsgType.MARKET_DATA_REQUEST);
		message.setField(new SubscriptionRequestType(
				SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES));
		message.setField(new NoMDEntryTypes(0));
		Group relatedSymGroup = messageFactory.createGroup(
				MsgType.MARKET_DATA_REQUEST, NoRelatedSym.FIELD);
		relatedSymGroup.setField(new Symbol(instrument.getSymbol()));
		if (securityType != null && !"".equals(securityType)){ //$NON-NLS-1$
			relatedSymGroup.setField(new SecurityType(securityType));
		}
		message.addGroup(relatedSymGroup);
		return message;
	}
	
	public static Message newSubscribeOptionUnderlying(Instrument underlying){
		Message message = messageFactory.createMessage(MsgType.MARKET_DATA_REQUEST);
		message.setField(new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES));
		message.setField(new NoMDEntryTypes(0));
		message.setField(new NoRelatedSym(0));
		message.setField(new MarketDepth(1)); // top-of-book
		Group relatedSymGroup = messageFactory.createGroup(
				MsgType.MARKET_DATA_REQUEST, NoRelatedSym.FIELD);
		Group underlyingGroup = messageFactory.createGroup(
				MsgType.MARKET_DATA_REQUEST, NoUnderlyings.FIELD);
		relatedSymGroup.setString(Symbol.FIELD, "[N/A]"); //$NON-NLS-1$
		relatedSymGroup.setField(new SecurityType(SecurityType.OPTION));

		underlyingGroup.setString(UnderlyingSymbol.FIELD, underlying.getSymbol());

		relatedSymGroup.addGroup(underlyingGroup);
		message.addGroup(relatedSymGroup);

		return message;
	}

}
