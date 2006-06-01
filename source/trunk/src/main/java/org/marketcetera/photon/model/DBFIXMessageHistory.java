package org.marketcetera.photon.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.AvgPx;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.LastMkt;
import quickfix.field.LastPx;
import quickfix.field.LastShares;
import quickfix.field.LeavesQty;
import quickfix.field.MsgType;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TransactTime;

public class DBFIXMessageHistory extends FIXMessageHistory {
	private static final String SELECT_AVERAGE_PRICE_STRING = "SELECT Side, Symbol, SUM(OrderQty) as OrderQty, SUM(CumQty) AS CumQty,"
			+ "SUM(LeavesQty) AS LeavesQty,"
			+ " ((SUM(CumQty*AvgPx))/SUM(CumQty)) AS AvgPx, Account FROM RECENT_EXECUTION_REPORTS"
			+ " GROUP BY Side, Symbol, Account";

	private static final String SELECT_AVERAGE_PRICE_METADATA_STRING = "SELECT Side, Symbol, SUM(OrderQty) as OrderQty, SUM(CumQty) AS CumQty,"
		+ "SUM(LeavesQty) AS LeavesQty,"
		+ " ((SUM(CumQty*AvgPx))/SUM(CumQty)) AS AvgPx, Account FROM RECENT_EXECUTION_REPORTS WHERE FALSE"
		+ " GROUP BY Side, Symbol, Account";

	private static final String SELECT_LATEST_EXECUTION_STRING = "SELECT ID, SYMBOL FROM RECENT_EXECUTION_REPORTS ORDER BY ClOrdID";

	private static final String TABLE_NAME = "MESSAGES";

	private static final String SELECT_FILLS_STRING = "SELECT (ID) FROM "
			+ TABLE_NAME + " WHERE MsgTYPE='" + MsgType.EXECUTION_REPORT
			+ "' AND LastShares IS NOT NULL";

	private static final String INSERT_MESSAGE_STRING = "INSERT INTO "
			+ TABLE_NAME
			+ "(TransactTime, MsgType, ClOrdID, Side, Symbol, OrderQty,"
			+ "CumQty, LeavesQty, Price, AvgPx, LastShares, LastPx, LastMkt, ACCOUNT) VALUES "
			+ "(?,?,?,?,?,?,?,?,?,?,?,?,?, ?);";
	Connection connection = null;
	Map<Integer, Message> messageMap = new HashMap<Integer, Message>();
	private PreparedStatement insertMessageStatement;
	private PreparedStatement selectFillsStatement;
	private CallableStatement callIdentityStatement;
	private PreparedStatement selectLatestExecutionStatement;
	private PreparedStatement selectAveragePriceStatement;
	private PreparedStatement averagePriceMetadataStatement;
	
	public DBFIXMessageHistory() {
		try {
			Class.forName("org.hsqldb.jdbcDriver");
			connection = DriverManager.getConnection("jdbc:hsqldb:mem:"
					+ this.hashCode(), "sa", "");
			String createTableMessages = "CREATE TABLE "
					+ TABLE_NAME
					+ " (ID IDENTITY, TransactTime DATE, MsgType VARCHAR, "
					+ "ClOrdID VARCHAR, Side CHAR(1), Symbol VARCHAR, "
					+ "OrderQty DECIMAL(20,5), CumQty DECIMAL(20,5), LeavesQty DECIMAL(20,5), "
					+ "Price DECIMAL(20,5), AvgPx DECIMAL(20,5), LastShares DECIMAL(20,5), "
					+ "LastPx DECIMAL(20,5), LastMkt VARCHAR, Account VARCHAR "
					+ " )";
			String createViewRecentExecutionReports = "CREATE VIEW RECENT_EXECUTION_REPORTS AS "
					+ "SELECT * FROM "
					+ TABLE_NAME
					+ " AS TBL1 WHERE MsgType='"
					+ MsgType.EXECUTION_REPORT
					+ "' AND ID IN (SELECT MAX(ID) AS ID FROM "
					+ TABLE_NAME
					+ " AS TBL2 WHERE TBL1.ClOrdID=TBL2.ClOrdID)";

			Statement stmt = connection.createStatement();
			stmt.executeUpdate(createTableMessages);
			stmt.executeUpdate(createViewRecentExecutionReports);
			stmt.close();

			insertMessageStatement = connection
					.prepareStatement(INSERT_MESSAGE_STRING);
			selectFillsStatement = connection
					.prepareStatement(SELECT_FILLS_STRING);
			selectLatestExecutionStatement = connection
					.prepareStatement(SELECT_LATEST_EXECUTION_STRING);
			selectAveragePriceStatement = connection
					.prepareStatement(SELECT_AVERAGE_PRICE_STRING);
			averagePriceMetadataStatement = connection.prepareStatement(SELECT_AVERAGE_PRICE_METADATA_STRING);
			callIdentityStatement = connection.prepareCall("CALL IDENTITY()");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.marketcetera.photon.model.FIXMessageHistory#addIncomingMessage(quickfix.Message)
	 */
	@Override
	public void addIncomingMessage(Message fixMessage) {
		try {
			insertMessage(fixMessage);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		super.addIncomingMessage(fixMessage);
	}
	
	private BigDecimal getBigDecimal(String from){
		BigDecimal bigDecimal = new BigDecimal(from,new MathContext(25, RoundingMode.HALF_UP));
		int existingScale = bigDecimal.scale();
		int newScale = existingScale > 6 ? existingScale : 6;
		return bigDecimal.setScale(newScale );
	}

	private void insertMessage(Message fixMessage) throws SQLException {
		insertMessageStatement.clearParameters();
		int i = 1;
		try { insertMessageStatement.setDate(i++, new java.sql.Date(fixMessage.getUtcTimeStamp(TransactTime.FIELD).getTime())); } catch (FieldNotFound ex) {}
		try { insertMessageStatement.setString(i++, fixMessage.getHeader().getString(MsgType.FIELD)); } catch (FieldNotFound ex) {}
		try { insertMessageStatement.setString(i++, fixMessage.getString(ClOrdID.FIELD)); } catch (FieldNotFound ex) {}
		try { insertMessageStatement.setString(i++, fixMessage.getString(Side.FIELD)); } catch (FieldNotFound ex) {}
		try { insertMessageStatement.setString(i++, fixMessage.getString(Symbol.FIELD)); } catch (FieldNotFound ex) {}
		try { insertMessageStatement.setBigDecimal(i++, getBigDecimal(fixMessage.getString(OrderQty.FIELD))); } catch (FieldNotFound ex) {}
		try { insertMessageStatement.setBigDecimal(i++, getBigDecimal(fixMessage.getString(CumQty.FIELD))); } catch (FieldNotFound ex) {}
		try { insertMessageStatement.setBigDecimal(i++, getBigDecimal(fixMessage.getString(LeavesQty.FIELD))); } catch (FieldNotFound ex) {}
		try { insertMessageStatement.setBigDecimal(i++, getBigDecimal(fixMessage.getString(Price.FIELD))); } catch (FieldNotFound ex) {}
		try { insertMessageStatement.setBigDecimal(i++, getBigDecimal(fixMessage.getString(AvgPx.FIELD))); } catch (FieldNotFound ex) {}
		try { insertMessageStatement.setBigDecimal(i++, getBigDecimal(fixMessage.getString(LastShares.FIELD))); } catch (FieldNotFound ex) {}
		try { insertMessageStatement.setBigDecimal(i++, getBigDecimal(fixMessage.getString(LastPx.FIELD))); } catch (FieldNotFound ex) {}
		try { insertMessageStatement.setString(i++, fixMessage.getString(LastMkt.FIELD)); } catch (FieldNotFound ex) {}
		try { insertMessageStatement.setString(i++, fixMessage.getString(Account.FIELD)); } catch (FieldNotFound ex) {}
		
		insertMessageStatement.execute();

		ResultSet set = callIdentityStatement.executeQuery();
		if (set.next()){
			messageMap.put(set.getInt(1), fixMessage);
		} else {
			System.err.println("Couldn't add message to history");
		}
	}

	/* (non-Javadoc)
	 * @see org.marketcetera.photon.model.FIXMessageHistory#addOutgoingMessage(quickfix.Message)
	 */
	@Override
	public void addOutgoingMessage(Message fixMessage) {
		try {
			insertMessage(fixMessage);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		super.addOutgoingMessage(fixMessage);
	}

	/* (non-Javadoc)
	 * @see org.marketcetera.photon.model.FIXMessageHistory#getFills()
	 */
	@Override
	public Object[] getFills() {
		try {
			return getMessagesFromStatement(selectFillsStatement);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new Object[0];
	}

	private Object[] getMessagesFromStatement(PreparedStatement stmt) throws SQLException {
		ResultSet set = stmt.executeQuery();
		ArrayList<Message> messages = new ArrayList<Message>();
		while (set.next()) {
		    Integer id = set.getInt("ID");
			messages.add(messageMap.get(id));
		}
		return messages.toArray();
	}

	/* (non-Javadoc)
	 * @see org.marketcetera.photon.model.FIXMessageHistory#getHistory()
	 */
	@Override
	public Object[] getHistory() {
		return super.getHistory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.marketcetera.photon.model.FIXMessageHistory#getLatestExecutionReports()
	 */
	@Override
	public Object[] getLatestExecutionReports() {
		try {
			Statement stmt = connection.createStatement();
			ResultSet set = stmt.executeQuery("SELECT * FROM "+TABLE_NAME);
			set.next();
			
			
			return getMessagesFromStatement(selectLatestExecutionStatement);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new Object[0];
		//return super.getLatestExecutionReports();
	}

	/* (non-Javadoc)
	 * @see org.marketcetera.photon.model.FIXMessageHistory#getLatestMessageForFields(quickfix.FieldMap)
	 */
	@Override
	public Message getLatestMessageForFields(FieldMap fields) {
		return super.getLatestMessageForFields(fields);
	}
	
	public ResultSet getAveragePriceResultSet() throws SQLException {
		return selectAveragePriceStatement.executeQuery();
	}
	public ResultSetMetaData getAveragePriceMetaData() throws SQLException {
		return averagePriceMetadataStatement.executeQuery().getMetaData();
	}
}
