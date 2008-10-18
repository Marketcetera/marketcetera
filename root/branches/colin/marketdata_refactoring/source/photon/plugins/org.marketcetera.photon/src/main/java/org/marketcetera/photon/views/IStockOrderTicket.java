package org.marketcetera.photon.views;

import org.eclipse.jface.viewers.TableViewer;

public interface IStockOrderTicket extends IOrderTicket {

	TableViewer getLevel2BidTableViewer();

	TableViewer getLevel2OfferTableViewer();
}