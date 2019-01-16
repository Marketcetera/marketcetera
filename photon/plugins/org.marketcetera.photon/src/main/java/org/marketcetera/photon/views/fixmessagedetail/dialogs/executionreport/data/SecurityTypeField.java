package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.SecurityType;

import quickfix.FieldNotFound;
import quickfix.Message;

public class SecurityTypeField extends ExecutionReportField {

	@Override
	public String getFieldName() 
	{
		return Messages.EXECUTION_REPORT_FIELD_SECURITY_TYPE.getText();
	}

	@Override
	public String[] getValues() 
	{
		List<String> securityTypeValues = new ArrayList<String>();
		for(SecurityType orderType: SecurityType.values())
		{
			securityTypeValues.add(orderType.name());
		}
	
		return (String[]) securityTypeValues.toArray(new String[securityTypeValues.size()]);
	}

	@Override
	public void insertField(Message message) 
	{
        if(fValue != null && fValue != EMPTY_STRING) {
			message.setField(new quickfix.field.SecurityType(SecurityType.valueOf(fValue).getFIXValue()));
		}
	}

	@Override
	public void parseFromReport(ExecutionReport executionReport) 
	{
		Message message = getMessageFromExecutionReport(executionReport);
		
		if(message != null && message.isSetField(quickfix.field.SecurityType.FIELD)) 
		{
			quickfix.field.SecurityType securityType = new quickfix.field.SecurityType();
			try 
			{
				fValue = SecurityType.getInstanceForFIXValue(message.getField(securityType).getValue()).name();
			} 
			catch (FieldNotFound e) 
			{
			}
		}		
	}

	@Override
	public int getFieldTag() 
	{
		return quickfix.field.SecurityType.FIELD;
	}

}
