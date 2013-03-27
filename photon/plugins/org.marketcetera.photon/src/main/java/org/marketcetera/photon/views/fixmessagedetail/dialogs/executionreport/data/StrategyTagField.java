package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import org.marketcetera.photon.Messages;
import org.marketcetera.quickfix.CustomField;

/**
 * Strategy tag execution report field
 * 
 * @author milan
 *
 */
public class StrategyTagField extends ExecutionReportField 
{
	private int FIELD;
	
	public StrategyTagField(int field)
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
		return Messages.EXECUTION_REPORT_FIELD_STRATEGY_TAG.getText();
	}

	@Override
	public String[] getValues() 
	{
		return NULL_VALUE;
	}

	@Override
	public Object getFieldValue() 
	{
		return new CustomField<String>(FIELD, fSelectedValue);
	}
}
