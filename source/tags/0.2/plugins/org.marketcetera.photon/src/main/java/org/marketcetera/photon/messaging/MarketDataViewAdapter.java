package org.marketcetera.photon.messaging;

import org.marketcetera.photon.views.MarketDataView;
import org.marketcetera.spring.JMSFIXMessageConverter;

import quickfix.Message;

public class MarketDataViewAdapter extends DirectMessageListenerAdapter {

	private MarketDataView view;

	public MarketDataViewAdapter() {
		super();
		this.setMessageConverter(new JMSFIXMessageConverter());
	}

	@Override
	protected Object doOnMessage(Object convertedMessage) {
		if (view != null){
			view.onQuote(((Message)convertedMessage));
		}
		return null;
	}

	public void setMarketDataView(MarketDataView view) {
		this.view = view;
	}

	public MarketDataView getMarketDataView() {
		return view;
	}

}
