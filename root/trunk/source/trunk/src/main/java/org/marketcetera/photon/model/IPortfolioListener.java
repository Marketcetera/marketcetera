package org.marketcetera.photon.model;

public interface IPortfolioListener {
	public void positionsChanged(Portfolio portfolio, PositionProgress entry);
}
