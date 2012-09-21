package org.marketcetera.ors.history;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.position.PositionKeyFactory;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.persist.EntityBase;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.SecurityType;
import org.marketcetera.trade.Side;

public class PersistentCache extends EntityBase {
	// INSTANCE;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static void init() {
		try {
			List<ExecutionReportSummary> eqErList = PersistentReport.fetchEquityReports();
			for (ExecutionReportSummary equityEr : eqErList) {
				String viewerID = String.valueOf(equityEr.getViewerID()
						.getValue());
				List<ExecutionReportSummary> equityReports;
				if (userEqExecReports.containsKey(viewerID)) {
					equityReports = userEqExecReports.get(viewerID);
					equityReports.add(equityEr);
					userEqExecReports.put(viewerID, equityReports);

				} else {
					equityReports = new ArrayList<ExecutionReportSummary>();
					equityReports.add(equityEr);
					userEqExecReports.put(viewerID,	equityReports);
				}

			}

			List<ExecutionReportSummary> futErList = PersistentReport.fetchFutureReports();
			for (ExecutionReportSummary futEr : futErList) {
				String viewerID = String
						.valueOf(futEr.getViewerID().getValue());
				List<ExecutionReportSummary> futureReports;
				if (userFutExecReports.containsKey(viewerID)) {
					futureReports = userFutExecReports.get(viewerID);
					futureReports.add(futEr);
					userFutExecReports.put(viewerID, futureReports);
				} else {
					futureReports = new ArrayList<ExecutionReportSummary>();
					futureReports.add(futEr);
					userFutExecReports.put(viewerID, futureReports);
				}

			}
		} catch (PersistenceException e1) {
			e1.printStackTrace();
		}
	}

	public static Map<PositionKey<Equity>, BigDecimal> getAllEquityPositionsAsOf(
			SimpleUser user, Date inDate) {
		 String viewerID = String.valueOf(user.getUserID().getValue());
		Map<PositionKey<Equity>, BigDecimal> posKeyMap = null;
		PositionKey<Equity> posKey;
		
		posKeyMap = new HashMap<PositionKey<Equity>, BigDecimal>();
		
		if (!userEqExecReports.containsKey(viewerID)) {
			return posKeyMap;
		}
		List<ExecutionReportSummary> es = userEqExecReports.get(viewerID);

			for (ExecutionReportSummary report : es) {

				if (report.getSendingTime().compareTo(inDate) > 0) {
					continue;
				}
				posKey = PositionKeyFactory.createEquityKey(report.getSymbol(),report.getAccount(),viewerID);

				if (posKeyMap.containsKey(posKey)) {
					if (report.getSide() == Side.Buy) {
						BigDecimal lastPos = posKeyMap.get(posKey);
						BigDecimal newPos = lastPos.add(report.getLastQuantity());
						posKeyMap.put(posKey, newPos);
					} else {
						BigDecimal lastPos = posKeyMap.get(posKey);
						BigDecimal newPos = lastPos.subtract(report	.getLastQuantity());
						posKeyMap.put(posKey, newPos);
					}

				} else {
					if (report.getSide() == Side.Buy) {
						posKeyMap.put(posKey, report.getLastQuantity());

					} else {
						posKeyMap.put(posKey, BigDecimal.ZERO.subtract(report.getLastQuantity()));
					}

				}

			}
		
		return posKeyMap;
	}

	public static Map<PositionKey<Future>, BigDecimal> getAllFuturePositionsAsOf(
			SimpleUser user, Date inDate) {
		 String viewerID = String.valueOf(user.getUserID().getValue());
		 Map<PositionKey<Future>, BigDecimal> posKeyMap = null;
		
			PositionKey<Future> posKey;
			posKeyMap = new HashMap<PositionKey<Future>, BigDecimal>();
			
			if (!userFutExecReports.containsKey(viewerID)) {
				return posKeyMap;
			}
			List<ExecutionReportSummary> es = userFutExecReports.get(viewerID);

			for (ExecutionReportSummary report : es) {

				if (report.getSendingTime().compareTo(inDate) > 0) {
					continue;
				}
				posKey = PositionKeyFactory.createFutureKey(report.getSymbol(),report.getExpiry(), report.getAccount(),viewerID);

				if (posKeyMap.containsKey(posKey)) {
					if (report.getSide() == Side.Buy) {
						BigDecimal lastPos = posKeyMap.get(posKey);
						BigDecimal newPos = lastPos.add(report.getLastQuantity());
						posKeyMap.put(posKey, newPos);
					} else {
						BigDecimal lastPos = posKeyMap.get(posKey);
						BigDecimal newPos = lastPos.subtract(report.getLastQuantity());
						posKeyMap.put(posKey, newPos);
					}

				} else {
					if (report.getSide() == Side.Buy) {
						posKeyMap.put(posKey, report.getLastQuantity());

					} else {
						posKeyMap.put(posKey,BigDecimal.ZERO.subtract(report.getLastQuantity()));
					}

				}

			}
		
		return posKeyMap;
	}

	public static void cache(ExecutionReportSummary inReport) {

		if (inReport.getSecurityType() == null) {
			return;
		}
		if (!(inReport.getOrderStatus() == OrderStatus.PartiallyFilled || inReport
				.getOrderStatus() == OrderStatus.Filled)) {
			return;
		}
		String viewrID = String.valueOf(inReport.getViewerID().getValue());
		if (inReport.getSecurityType() == SecurityType.CommonStock) {
			List<ExecutionReportSummary> eqExecReports;
			if (userEqExecReports.containsKey(viewrID)) {
				eqExecReports = userEqExecReports.get(viewrID);
				eqExecReports.add(inReport);
			} else {
				eqExecReports = new ArrayList<ExecutionReportSummary>();
				eqExecReports.add(inReport);
				userEqExecReports.put(viewrID, eqExecReports);
			}

		} else if (inReport.getSecurityType() == SecurityType.Future) {
			List<ExecutionReportSummary> eqExecReports;
			if (userFutExecReports.containsKey(viewrID)) {
				eqExecReports = userFutExecReports.get(viewrID);
				eqExecReports.add(inReport);
			} else {
				eqExecReports = new ArrayList<ExecutionReportSummary>();
				eqExecReports.add(inReport);
				userFutExecReports.put(viewrID, eqExecReports);
			}

		}

	}

	private static Map<String, List<ExecutionReportSummary>> userEqExecReports = new TreeMap<String, List<ExecutionReportSummary>>();
	private static Map<String, List<ExecutionReportSummary>> userFutExecReports = new TreeMap<String, List<ExecutionReportSummary>>();

}
