package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.marketcetera.client.Client;
import org.marketcetera.client.ClientInitException;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.ClientParameters;
import org.marketcetera.client.ConnectionException;
import org.marketcetera.photon.actions.ConnectionDetails;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.UserID;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.AvgPx;
import quickfix.field.BrokerOfCredit;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.ExecType;
import quickfix.field.InstrumentPartyID;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.LastShares;
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.Text;
import quickfix.field.TransactTime;


/**
 * Class representing table contents of the Execution Report
 * 
 * @author milan
 *
 */
public class ExecutionReportContainer
{
	/** Execution report fields */
	private List<ExecutionReportField> fExecutionReportFields;
	
	public ExecutionReportContainer()
	{
	}
	
	public void addExecutionReportField(ExecutionReportField field)
	{
		if(fExecutionReportFields == null)
			fExecutionReportFields = new ArrayList<ExecutionReportField>();
		
		if(fExecutionReportFields.contains(field))
		{
			int index = fExecutionReportFields.indexOf(field);
			ExecutionReportField existingField = fExecutionReportFields.get(index);
			
			existingField.setSelectedValue(field.getSelectedValue());
		}
		else
		{
			fExecutionReportFields.add(field);
		}
	}
	
	public void removeExecutionReportFields(ExecutionReportField[] fields)
	{
		if(fExecutionReportFields == null)
			return;
		
		for(ExecutionReportField field: fields)
		{
			if(fExecutionReportFields.contains(field))
				fExecutionReportFields.remove(field);
		}
	}
		
	public ExecutionReportField[] getExecutionReportFields()
	{
		if(fExecutionReportFields == null)
			return new ExecutionReportField[] {};
		
		return (ExecutionReportField[]) fExecutionReportFields.toArray(new ExecutionReportField[fExecutionReportFields.size()]);
	}
	
	private Object getField(int field)	
	{
		if(fExecutionReportFields == null)
			return null;
		for(ExecutionReportField reportField: fExecutionReportFields)
		{
			if(reportField.getField() == field)
				return reportField.getFieldValue();
		}
		
		return null;
	}
	
	public ExecutionReport createExecutionReport() throws FieldNotFound, MessageCreationException
	{
		Message message = null;
		try{
		 message = createBasicExecutionReport(
				getField(OrderID.FIELD),
				getField(OrderID.FIELD), 
				getField(ExecID.FIELD), 
				getField(OrdStatus.FIELD),
				getField(Side.FIELD),
				getField(OrderQty.FIELD),
				getField(Price.FIELD),
				getField(LastQty.FIELD),
				getField(LastPx.FIELD),
				getField(CumQty.FIELD),
				getField(AvgPx.FIELD),
				getField(InstrumentPartyID.FIELD),
				getField(Account.FIELD),
				getField(Text.FIELD));
        message.setField(new TransactTime((Date) getField(TransactTime.FIELD)));
        message.setField(new ExecType(((Character)getField(ExecType.FIELD)).charValue()));
		}catch(Exception e){
			e.printStackTrace();
		}
		UserID userID = new UserID(1);
		

		ExecutionReport executionReport = Factory
				.getInstance()
					.createExecutionReport(
							message,
							(BrokerID)getField(BrokerOfCredit.FIELD),
							Originator.Broker,
							userID,
							userID);
		
		return executionReport;
	}	
	
	public Message createBasicExecutionReport(Object orderID,
			Object clOrderID,
			Object execID,
			Object ordStatus,
			Object side,
			Object orderQty,
			Object orderPrice,
			Object lastQty,
			Object lastPrice,
			Object cumQty,
			Object avgPrice,
			Object instrument,
			Object inAccount,
			Object inText) throws FieldNotFound, MessageCreationException
	{
		Message message = FIXVersion.FIX_SYSTEM
				.getMessageFactory()
					.newExecutionReport(
							orderID != null ? orderID.toString() : null, 
							clOrderID != null ? clOrderID.toString() : null, 
							(String)execID, 
							ordStatus != null ? ((Character)ordStatus).charValue(): null, 
							side != null ?((Character)side).charValue() : null, 
							(BigDecimal)orderQty, 
							(BigDecimal)orderPrice, 
							(BigDecimal)lastQty, 
							(BigDecimal)lastPrice, 
							(BigDecimal)cumQty, 
							(BigDecimal)avgPrice, 
							(Instrument)instrument, 
							(String)inAccount, 
							(String)inText);
		return message;
	}
}
