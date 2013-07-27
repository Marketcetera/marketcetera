package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.ExecutionType;

import quickfix.Message;
import quickfix.field.ExecTransType;

/**
 * Execution transact type execution report field
 * 
 * @author milan
 *
 */
public class ExecTransTypeField extends ExecutionReportField {
	@Override
	public String getFieldName() 
	{
		return Messages.EXECUTION_REPORT_FIELD_EXEC_TRANS_TYPE.getText();
	}

	@Override
	public String[] getValues() 
	{
		/*using package org.marketcetera.trade.FIXUtil
		  line 240
		  		if(inMessage.isSetField(ExecTransType.FIELD)) {
         			char c = inMessage.getChar(ExecTransType.FIELD);
         			switch(c) {
                      case ExecTransType.NEW:
                          return ExecutionType.New;
                      case ExecTransType.CANCEL:
                          return ExecutionType.TradeCancel;
                      case ExecTransType.CORRECT:
                          return ExecutionType.TradeCorrect;
                      case ExecTransType.STATUS:
                          return ExecutionType.OrderStatus;
                      default:
                          return ExecutionType.Unknown;
              }
       */
		return new String[]{
				ExecutionType.Unknown.name(),
				ExecutionType.New.name(),
				ExecutionType.TradeCancel.name(),
				ExecutionType.TradeCorrect.name(),
				ExecutionType.OrderStatus.name()
		};
	}

	@Override
	public void insertField(Message message) {
		ExecutionType fixValue = ExecutionType.valueOf(fValue);
		ExecutionType.New.getFIXValue();
		switch (fixValue) {		
			case Unknown:
				message.setField(new ExecTransType(ExecutionType.Unknown.getFIXValue()));
				break;
			case New:
				message.setField(new ExecTransType(ExecTransType.NEW));
				break;
			case TradeCancel:
				message.setField(new ExecTransType(ExecTransType.CANCEL));
				break;
			case TradeCorrect:
				message.setField(new ExecTransType(ExecTransType.CORRECT));
				break;
			case OrderStatus:
				message.setField(new ExecTransType(ExecTransType.STATUS));
				break;
		default:
			break;
		}
		
	}
}
