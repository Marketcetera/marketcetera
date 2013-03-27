package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.ExecutionType;

/**
 * Execution type execution report field
 * 
 * @author milan
 *
 */
public class ExecutionTypeField extends ExecutionReportField 
{
	private int FIELD;
	
	public ExecutionTypeField(int field)
	{
		FIELD = field;
	}
	
	@Override
	public int getField() 
	{
		return FIELD;
	}
	
	@Override
	public String getFieldName() 
	{
		return Messages.EXECUTION_REPORT_FIELD_EXECUTION_TYPE.getText();
	}

	@Override
	public String[] getValues() 
	{
		List<String> executionTypeValues = new ArrayList<String>();
		for(ExecutionType executionType: ExecutionType.values())
		{
			executionTypeValues.add(executionType.name());
		}
	
		return (String[]) executionTypeValues.toArray(new String[executionTypeValues.size()]);
	}

	@Override
	public Object getFieldValue() 
	{
		ExecutionType execType = ExecutionType.valueOf(fSelectedValue);
		return new Character(execType.getFIXValue());
	}
}
