package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import org.marketcetera.trade.ExecutionReport;

import quickfix.Message;
import quickfix.StringField;

public class CustomNoneFixField extends ExecutionReportNoneFixField {

	/** Custom field number */
	private final String fFieldName;
	
	public CustomNoneFixField(String fieldName)
	{
		fFieldName = fieldName;
	}
		
	@Override
	public String getFieldName() 
	{
		return fFieldName;
	}

	@Override
	public String[] getValues() 
	{
		return NULL_VALUE;
	}

	@Override
	public void insertField(Message message) 
	{
	    if(fValue != null && fValue != EMPTY_STRING) {
	        message.setField(new StringField(Integer.parseInt(fFieldName),fValue));
	    }
	}

	@Override
	public boolean validateValue() {
		if(!super.validateValue())
			return false;
		return fFieldName != null && !fFieldName.equals(EMPTY_STRING);
	}

	@Override
	public void parseFromReport(ExecutionReport executionReport) 
	{
	}

	@Override
	public int getFieldTag() 
	{
		return -1;
	}
	
    @Override
    public boolean equals(Object obj)
   {
       if (this == obj) {
           return true;
       }
       if (obj == null) {
           return false;
       }
       if (!(obj instanceof CustomNoneFixField)) {
           return false;
       }
       CustomNoneFixField other = (CustomNoneFixField) obj;
       if (fFieldName.compareTo(other.fFieldName) != 0) {
           return false;
       }
       return true;
   }

}
