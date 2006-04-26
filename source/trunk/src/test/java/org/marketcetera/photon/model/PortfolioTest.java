package org.marketcetera.photon.model;

import java.math.BigDecimal;
import java.util.Date;

import junit.framework.TestCase;

import org.marketcetera.core.AccessViolator;
import org.marketcetera.core.AccountID;
import org.marketcetera.core.InternalID;

import quickfix.field.OrdStatus;
import quickfix.field.Side;

public class PortfolioTest extends TestCase {

	private static final String PORTFOLIO_NAME = "NAME";

	/*
	 * Test method for 'org.marketcetera.photon.model.Portfolio.getName()'
	 */
	public void testGetName() {
		Portfolio port = new Portfolio(null, PORTFOLIO_NAME);
		assertEquals(PORTFOLIO_NAME, port.getName());
	}

	/*
	 * Test method for 'org.marketcetera.photon.model.Portfolio.getParent()'
	 */
	public void testGetParent() {
		String parentName = PORTFOLIO_NAME + "_PARENT";
		Portfolio parent = new Portfolio(null, parentName);
		Portfolio port = new Portfolio(parent, PORTFOLIO_NAME);
		assertEquals(parentName, port.getParent().getName());
	}

	/*
	 * Test method for 'org.marketcetera.photon.model.Portfolio.getProgress()'
	 */
	public void testGetProgress() {
		Portfolio port = new Portfolio(null, PORTFOLIO_NAME);
		PositionEntry entry = new PositionEntry(port, "NAME", new InternalID(
				"1234"), OrdStatus.PARTIALLY_FILLED, Side.BUY, new BigDecimal(
				1000), "IBM", new BigDecimal("12.3"), false, new AccountID(
				"ACCT"), new Date());
		entry.setCumQty(new BigDecimal(500));

		assertEquals(.5, entry.getProgress(), .001);
		assertEquals(.5, port.getProgress(), .001);

		PositionEntry entry2 = new PositionEntry(port, "NAME", new InternalID(
				"1234"), OrdStatus.PARTIALLY_FILLED, Side.BUY, new BigDecimal(
				5000), "IBM", new BigDecimal("12.3"), false, new AccountID(
				"ACCT"), new Date());
		entry2.setCumQty(new BigDecimal(500));
		assertEquals(.1, entry2.getProgress(), .001);
		assertEquals(.3, port.getProgress(), .001);
	}

	/*
	 * Test method for
	 * 'org.marketcetera.photon.model.Portfolio.addEntry(PositionProgress)'
	 */
	public void testAddEntry() {
		Portfolio port = new Portfolio(null, PORTFOLIO_NAME);
		PositionEntry entry = new PositionEntry(null, "NAME", new InternalID(
				"1234"), OrdStatus.PARTIALLY_FILLED, Side.BUY, new BigDecimal(
				1000), "IBM", new BigDecimal("12.3"), false, new AccountID(
				"ACCT"), new Date());
		entry.setCumQty(new BigDecimal(500));
		port.addEntry(entry);
		assertEquals(.5, port.getProgress(), .001);
	}

	/*
	 * Test method for
	 * 'org.marketcetera.photon.model.Portfolio.removeEntry(PositionProgress)'
	 */
	public void testRemoveEntry() {
		Portfolio port = new Portfolio(null, PORTFOLIO_NAME);
		PositionEntry entry = new PositionEntry(null, "NAME", new InternalID(
				"1234"), OrdStatus.PARTIALLY_FILLED, Side.BUY, new BigDecimal(
				1000), "IBM", new BigDecimal("12.3"), false, new AccountID(
				"ACCT"), new Date());
		entry.setCumQty(new BigDecimal(500));
		port.addEntry(entry);
		port.removeEntry(entry);
		assertEquals(0.0, port.getProgress(), .001);
	}

	/*
	 * Test method for 'org.marketcetera.photon.model.Portfolio.getEntries()'
	 */
	public void testGetEntries() {
		// TODO: Write ME
	}

	/*
	 * Test method for
	 * 'org.marketcetera.photon.model.Portfolio.addPortfolioListener(IPortfolioListener)'
	 */
	public void testAddPortfolioListener() throws NoSuchFieldException,
			IllegalAccessException {
		Portfolio port = new Portfolio(null, PORTFOLIO_NAME);
		PositionEntry entry = new PositionEntry(null, "NAME", new InternalID(
				"1234"), OrdStatus.PARTIALLY_FILLED, Side.BUY, new BigDecimal(
				1000), "IBM", new BigDecimal("12.3"), false, new AccountID(
				"ACCT"), new Date());
		entry.setCumQty(new BigDecimal(500));

		IPortfolioListener portfolioListener = new IPortfolioListener() {
			public Portfolio changedPortfolio;

			public PositionProgress changedProgress;

			public void positionsChanged(Portfolio portfolio,
					PositionProgress entry) {
				changedPortfolio = portfolio;
				changedProgress = entry;
			}
		};
		port.addPortfolioListener(portfolioListener);
		port.addEntry(entry);

		// use the AccessViolator to get the fields out of the anon class
		// portfolioListener
		AccessViolator violator = new AccessViolator(portfolioListener
				.getClass());
		assertEquals(entry, violator.getField("changedProgress",
				portfolioListener));
		assertEquals(port, violator.getField("changedPortfolio",
				portfolioListener));

		entry.setCumQty(new BigDecimal(800));
		port.updateEntry(entry);

		assertEquals(entry, violator.getField("changedProgress",
				portfolioListener));
		assertEquals(port, violator.getField("changedPortfolio",
				portfolioListener));

	}

	/*
	 * Test method for
	 * 'org.marketcetera.photon.model.Portfolio.removePortfolioListener(IPortfolioListener)'
	 */
	public void testRemovePortfolioListener() throws NoSuchFieldException,
			IllegalAccessException {
		Portfolio port = new Portfolio(null, PORTFOLIO_NAME);
		PositionEntry entry = new PositionEntry(null, "NAME", new InternalID(
				"1234"), OrdStatus.PARTIALLY_FILLED, Side.BUY, new BigDecimal(
				1000), "IBM", new BigDecimal("12.3"), false, new AccountID(
				"ACCT"), new Date());
		entry.setCumQty(new BigDecimal(500));

		IPortfolioListener portfolioListener = new IPortfolioListener() {
			public Portfolio changedPortfolio;

			public PositionProgress changedProgress;

			public void positionsChanged(Portfolio portfolio,
					PositionProgress entry) {
				changedPortfolio = portfolio;
				changedProgress = entry;
			}
		};
		port.addPortfolioListener(portfolioListener);
		port.removePortfolioListener(portfolioListener);
		port.addEntry(entry);

		// use the AccessViolator to get the fields out of the anon class
		// portfolioListener
		AccessViolator violator = new AccessViolator(portfolioListener
				.getClass());
		assertEquals(null, violator.getField("changedProgress",
				portfolioListener));
		assertEquals(null, violator.getField("changedPortfolio",
				portfolioListener));

	}

}
