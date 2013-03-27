package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.math.BigDecimal;

import org.marketcetera.photon.Messages;

/**
 * Average price execution report field
 * 
 * @author milan
 *
 */
public class AvgPxField extends ExecutionReportField 
{
	private int FIELD;
	
	public AvgPxField(int field)
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
		return Messages.EXECUTION_REPORT_FIELD_AVG_PX.getText();
	}

	@Override
	public String[] getValues() 
	{
		return NULL_VALUE;
	}

	@Override
	public Object getFieldValue() 
	{
		return new BigDecimal(fSelectedValue);
	}
}
