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
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.SecurityType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.UserID;

public class PersistentCache extends EntityBase {
	// INSTANCE;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Map<UserID,List<PositionKey<Equity>>> userEquityPositionKey=new HashMap<UserID, List<PositionKey<Equity>>>();
	private static Map<PositionKey<Equity>,List<TradePosition>> equityKeyData= new HashMap<PositionKey<Equity>,List<TradePosition>>();
	private static Map<UserID,List<PositionKey<Future>>> userFuturePositionKey=new HashMap<UserID, List<PositionKey<Future>>>();
	private static Map<PositionKey<Future>,List<TradePosition>> futureKeyData= new HashMap<PositionKey<Future>,List<TradePosition>>();
	private static Map<UserID,List<PositionKey<Option>>> userOptionPositionKey=new HashMap<UserID, List<PositionKey<Option>>>();
	private static Map<PositionKey<Option>,List<TradePosition>> optionKeyData= new HashMap<PositionKey<Option>,List<TradePosition>>();
	
	public static void init() {
		try {
			List<ExecutionReportSummary> eqErList = PersistentReport.fetchEquityReports();
			List<ExecutionReportSummary> futErList = PersistentReport.fetchFutureReports();
			List<ExecutionReportSummary> optErList = PersistentReport.fetchOptionReports();
			for(ExecutionReportSummary equityER:eqErList){
				populateUserEquityPosKey(equityER);
			}
			for(ExecutionReportSummary futureER:futErList){
				populateUserFuturePosKey(futureER);
			}
			for(ExecutionReportSummary optionER:optErList){
				populateUserFuturePosKey(optionER);
			}
			
			
		} catch (PersistenceException e1) {
			e1.printStackTrace();
		}
	}
	
	public static void cache(ExecutionReportSummary inReport) {

		if (!(inReport.getOrderStatus() == OrderStatus.PartiallyFilled || inReport
				.getOrderStatus() == OrderStatus.Filled)) {
			return;
		}
		
		if (inReport.getSecurityType() == null) {
			return;
		}
	
		if (inReport.getSecurityType() == SecurityType.CommonStock) {
			populateUserEquityPosKey(inReport);
		}
		
		if (inReport.getSecurityType() == SecurityType.Future) {
			populateUserFuturePosKey(inReport);
		}

		if (inReport.getSecurityType() == SecurityType.Option) {
			populateUserOptionPosKey(inReport);
		}
	}
	
	public static Map<PositionKey<Equity>, BigDecimal> getAllEquityPositionsAsOf(
			SimpleUser user, Date inDate) {
		 Map<PositionKey<Equity>, BigDecimal> posKeyMap = new HashMap<PositionKey<Equity>, BigDecimal>();
		if(userEquityPositionKey.isEmpty()){
			return posKeyMap;
		}
		
		if(user.isSuperuser()){
			for(Map.Entry<UserID,List<PositionKey<Equity>>> pKeys:userEquityPositionKey.entrySet()){
				List<PositionKey<Equity>> pKey = pKeys.getValue();
				for(PositionKey<Equity> p:pKey){
					BigDecimal poss=BigDecimal.ZERO;
					List<TradePosition> dd=equityKeyData.get(p);
					for(TradePosition d:dd){
						if(d.getSendingTime().compareTo(inDate)<0){
							poss=poss.add(d.getQuantity());
						}
					}
					posKeyMap.put(p, poss);
				}
			}
			return  posKeyMap;
		}
		List<PositionKey<Equity>> pKey = userEquityPositionKey.get(user.getUserID());
		
		if(pKey==null){
			return posKeyMap;
		}
		
		for(PositionKey<Equity> p:pKey){
			BigDecimal poss=BigDecimal.ZERO;
			List<TradePosition> dd=equityKeyData.get(p);
			for(TradePosition d:dd){
				if(d.getSendingTime().compareTo(inDate)<0){
					poss=poss.add(d.getQuantity());
				}
			}
			posKeyMap.put(p, poss);
		}
		
		return  posKeyMap;
		
	}

	public static Map<PositionKey<Future>, BigDecimal> getAllFuturePositionsAsOf(
			SimpleUser user, Date inDate) {
		 Map<PositionKey<Future>, BigDecimal> posKeyMap = new HashMap<PositionKey<Future>, BigDecimal>();
		
		 if(userFuturePositionKey.isEmpty()){
			return posKeyMap;
		}
		posKeyMap = new HashMap<PositionKey<Future>, BigDecimal>();
		if(user.isSuperuser()){
			for(Map.Entry<UserID,List<PositionKey<Future>>> pKeys:userFuturePositionKey.entrySet()){
				List<PositionKey<Future>> pKey = pKeys.getValue();
				for(PositionKey<Future> p:pKey){
					BigDecimal poss=BigDecimal.ZERO;
					List<TradePosition> dd=futureKeyData.get(p);
					for(TradePosition d:dd){
						if(d.getSendingTime().compareTo(inDate)<0){
							poss=poss.add(d.getQuantity());
						}
					}
					posKeyMap.put(p, poss);
				}
			}
			return  posKeyMap;
		}
		List<PositionKey<Future>> pKey = userFuturePositionKey.get(user.getUserID());
		
		if(pKey==null){
			return posKeyMap;
		}
		for(PositionKey<Future> p:pKey){
			BigDecimal poss=BigDecimal.ZERO;
			List<TradePosition> dd=futureKeyData.get(p);
			for(TradePosition d:dd){
				if(d.getSendingTime().compareTo(inDate)<0){
					poss=poss.add(d.getQuantity());
				}
			}
			posKeyMap.put(p, poss);
		}
		
		return  posKeyMap;
		
	}

	public static Map<PositionKey<Option>, BigDecimal> getAllOptionPositionsAsOf(
			SimpleUser user, Date inDate) {
		 Map<PositionKey<Option>, BigDecimal> posKeyMap = new HashMap<PositionKey<Option>, BigDecimal>();
		
		 if(userOptionPositionKey.isEmpty()){
				return posKeyMap;
			}
		
		if(user.isSuperuser()){
			for(Map.Entry<UserID,List<PositionKey<Option>>> pKeys:userOptionPositionKey.entrySet()){
				List<PositionKey<Option>> pKey = pKeys.getValue();
				for(PositionKey<Option> p:pKey){
					BigDecimal poss=BigDecimal.ZERO;
					List<TradePosition> dd=optionKeyData.get(p);
					for(TradePosition d:dd){
						if(d.getSendingTime().compareTo(inDate)<0){
							poss=poss.add(d.getQuantity());
						}
					}
					posKeyMap.put(p, poss);
				}
			}
			return  posKeyMap;
		}
		List<PositionKey<Option>> pKey = userOptionPositionKey.get(user.getUserID());
		
		if(pKey==null){
			return posKeyMap;
		}
		for(PositionKey<Option> p:pKey){
			BigDecimal poss=BigDecimal.ZERO;
			List<TradePosition> dd=optionKeyData.get(p);
			for(TradePosition d:dd){
				if(d.getSendingTime().compareTo(inDate)<0){
					poss=poss.add(d.getQuantity());
				}
			}
			posKeyMap.put(p, poss);
		}
		
		return  posKeyMap;
		
	}

	
	private static void updateEquityDatamap(PositionKey<Equity> posKey,ExecutionReportSummary execreport){
		List<TradePosition> dataList = null;
		
		if(equityKeyData.containsKey(posKey)){
			dataList=equityKeyData.get(posKey);
			dataList.add(new TradePosition(execreport));
		}else{
			dataList=new ArrayList<TradePosition>();
			dataList.add(new TradePosition(execreport));
		}
		equityKeyData.put(posKey, dataList);
	}
	
	private static void updateFutureDatamap(PositionKey<Future> posKey,ExecutionReportSummary execreport){
		List<TradePosition> data = null;
		if(futureKeyData.containsKey(posKey)){
			data=futureKeyData.get(posKey);
			data.add(new TradePosition(execreport));
		}else{
			data=new ArrayList<TradePosition>();
			data.add(new TradePosition(execreport));
		}
		futureKeyData.put(posKey, data);
	}
	
	private static void updateOptionDatamap(PositionKey<Option> posKey,ExecutionReportSummary execreport){
		List<TradePosition> data = null;
		if(optionKeyData.containsKey(posKey)){
			data=optionKeyData.get(posKey);
			data.add(new TradePosition(execreport));
		}else{
			data=new ArrayList<TradePosition>();
			data.add(new TradePosition(execreport));
		}
		optionKeyData.put(posKey, data);
	}


	private static void populateUserEquityPosKey(
			ExecutionReportSummary equityEr) {
		List<PositionKey<Equity>> equityPosKeys;
		
		UserID viewerID = equityEr.getViewerID();
		PositionKey<Equity> posKey = PositionKeyFactory.createEquityKey(equityEr.getSymbol(),equityEr.getAccount(),String.valueOf(viewerID.getValue()));
			if(userEquityPositionKey.containsKey(equityEr.getViewerID())){
				equityPosKeys=userEquityPositionKey.get(viewerID);
				equityPosKeys.add(posKey);
				
			}else{
				equityPosKeys=new ArrayList<PositionKey<Equity>>();
				equityPosKeys.add(posKey);
				
			}
			userEquityPositionKey.put(viewerID,equityPosKeys);
			updateEquityDatamap(posKey,equityEr);
			

		
		
	}

	private static void populateUserFuturePosKey(
			ExecutionReportSummary futureEr) {
		List<PositionKey<Future>> futurePosKeys;
		UserID viewerID = futureEr.getViewerID();
		PositionKey<Future> posKey = PositionKeyFactory.createFutureKey(futureEr.getSymbol(),futureEr.getExpiry(),futureEr.getAccount(),String.valueOf(viewerID.getValue()));
	
		if(userFuturePositionKey.containsKey(futureEr.getViewerID())){
				futurePosKeys=userFuturePositionKey.get(viewerID);
				futurePosKeys.add(posKey);
				
			}else{
				futurePosKeys=new ArrayList<PositionKey<Future>>();
				futurePosKeys.add(posKey);
				
			}
			userFuturePositionKey.put(viewerID,futurePosKeys);
			updateFutureDatamap(posKey,futureEr);
		
	}

	private static void populateUserOptionPosKey(
			ExecutionReportSummary optionEr) {
		List<PositionKey<Option>> optionPosKeys;
		UserID viewerID = optionEr.getViewerID();
		PositionKey<Option> posKey = PositionKeyFactory.createOptionKey(optionEr.getSymbol(),optionEr.getExpiry(),optionEr.getStrikePrice(),optionEr.getOptionType(),optionEr.getAccount(),String.valueOf(viewerID.getValue()));
		if(userOptionPositionKey.containsKey(optionEr.getViewerID())){
				optionPosKeys=userOptionPositionKey.get(viewerID);
				optionPosKeys.add(posKey);
		
		}else{
			optionPosKeys=new ArrayList<PositionKey<Option>>();
			optionPosKeys.add(posKey);
			
		}
		userOptionPositionKey.put(viewerID,optionPosKeys);
		updateOptionDatamap(posKey,optionEr);
		
	}



	

}
