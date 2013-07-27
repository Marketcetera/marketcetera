package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.client.ClientInitException;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.ConnectionException;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.photon.Messages;

import quickfix.Message;

/**
 * Broker ID execution report field
 * 
 * @author milan
 *
 */
public class BrokerIDField extends ExecutionReportNoneFixField 
{
	public static final String BROKER_ID_FIELD_NAME = Messages.EXECUTION_REPORT_FIELD_BROKER_ID.getText();
	
	@Override
	public String getFieldName() 
	{
		return BROKER_ID_FIELD_NAME;
	}

	@Override
	public String[] getValues() 
	{
		ArrayList<String> brokerIDs = new ArrayList<String>();
		BrokersStatus bs;
		try {
			bs = ClientManager.getInstance().getBrokersStatus();
			List<BrokerStatus> brokers = bs.getBrokers();
			for (BrokerStatus broker : brokers) 
			{
				brokerIDs.add(broker.getId().getValue());
			}
		} catch (ConnectionException e) {
		} catch (ClientInitException e) {
		}
		return brokerIDs.toArray(new String[brokerIDs.size()]);
	}

	@Override
	public void insertField(Message message) {
		System.out.println("Insert BrokerID into message");	
	}
}
