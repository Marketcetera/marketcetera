package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.Side;

/**
 * Side execution report field
 * 
 * @author milan
 *
 */
public class SideField extends ExecutionReportField 
{
	private int FIELD;
	
	public SideField(int field)
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
		return Messages.EXECUTION_REPORT_FIELD_SIDE.getText();
	}

	@Override
	public String[] getValues() 
	{
		List<String> sideValues = new ArrayList<String>();
		for(Side side: Side.values())
		{
			sideValues.add(side.name());
		}
	
		return (String[]) sideValues.toArray(new String[sideValues.size()]);
	}

	@Override
	public Object getFieldValue() 
	{
		Side side = Side.valueOf(fSelectedValue);
		return new Character(side.getFIXValue());
	}
}
