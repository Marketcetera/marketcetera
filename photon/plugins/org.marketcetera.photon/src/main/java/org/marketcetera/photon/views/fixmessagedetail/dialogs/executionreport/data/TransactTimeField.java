package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;

/**
 * Transaction time execution report field
 * 
 * @author milan
 *
 */
public class TransactTimeField extends ExecutionReportField 
{
	private static final String timeFormat = "dd-MMM-yyyy HH:mm:ss:SSS"; //$NON-NLS-1$

	private int FIELD;
	
	public TransactTimeField(int field)
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
		return Messages.EXECUTION_REPORT_FIELD_TRANSACT_TIME.getText();
	}

	@Override
	public String[] getValues() 
	{
		return NULL_VALUE;
	}

	@Override
	public Object getFieldValue() 
	{
		try {
			Calendar transactTime = Calendar.getInstance();
			SimpleDateFormat transactTimeFormat = new SimpleDateFormat(timeFormat);
			transactTime.setTime(transactTimeFormat.parse(fSelectedValue));
			
			
			return transactTime.getTime();
		} 
		catch (ParseException e) 
		{
			PhotonPlugin.LOGGER.error("Time formatting", e);
		}

		return null;
	}
}
