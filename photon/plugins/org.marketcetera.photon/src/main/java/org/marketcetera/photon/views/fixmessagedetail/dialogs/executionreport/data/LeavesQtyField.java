package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.math.BigDecimal;

import org.marketcetera.photon.Messages;

/**
 * Leaves quantity execution report field
 * 
 * @author milan
 *
 */
public class LeavesQtyField extends ExecutionReportField 
{
	private int FIELD;
	
	public LeavesQtyField(int field)
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
		return Messages.EXECUTION_REPORT_FIELD_LEAVES_QTY.getText();
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
