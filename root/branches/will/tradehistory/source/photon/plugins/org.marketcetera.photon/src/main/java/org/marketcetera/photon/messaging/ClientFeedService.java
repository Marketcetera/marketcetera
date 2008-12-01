package org.marketcetera.photon.messaging;



import java.beans.ExceptionListener;

import org.marketcetera.client.Client;
import org.marketcetera.client.ClientInitException;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.ClientParameters;
import org.marketcetera.client.ConnectionException;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.photon.FeedComponentAdapterBase;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.util.misc.ClassVersion;
import org.osgi.framework.ServiceRegistration;

/* $License$ */

/**
 * Provides a service that tracks Client's connection to the
 * System Server.
 * 
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ClientFeedService
    extends FeedComponentAdapterBase
    implements Messages, ExceptionListener
{

	private boolean mExceptionOccurred = true;
	private ServiceRegistration mServiceRegistration;
	
	public FeedStatus getFeedStatus() {
		return mExceptionOccurred ? FeedStatus.ERROR : FeedStatus.AVAILABLE;
	}

	public void afterPropertiesSet() throws Exception {
		mExceptionOccurred = false;
		getClient().addReportListener(PhotonPlugin.getDefault().getPhotonController());
		getClient().addExceptionListener(this);
	}
	public void disconnect() {
		try {
			getClient().removeExceptionListener(this);
			getClient().removeReportListener(PhotonPlugin.getDefault().getPhotonController());
			getClient().close();
		} catch (ClientInitException ignore) {
			//Already disconnected.
		}
	}

	public String getID() {
		return Messages.CLIENT_CONNECTION_NAME.getText();
	}

	@Override
	public void exceptionThrown(Exception inException) {
		mExceptionOccurred = true;
		PhotonPlugin.getMainConsoleLogger().error(CLIENT_EXCEPTION.getText(),
				inException);
		fireFeedComponentChanged();
	}
	
	/**
	 * Fetches the client instance that has been initialized before.
	 * 
	 * @return the client instance.
	 * 
	 * @throws ClientInitException if the client has not been initialized.
	 */
	public Client getClient() throws ClientInitException {
		return ClientManager.getInstance();
	}

	/**
	 * Initializes the client instance.
	 * 
	 * @param inParameter the parameters containing information
	 * needed by the client to connect to the server.
	 * 
	 * @throws ConnectionException if there were issues connecting 
	 * to the server.
	 * 
	 * @throws ClientInitException if the client has not been initialized.
	 */
	public void initClient(ClientParameters inParameter) throws ConnectionException, ClientInitException {
		ClientManager.init(inParameter);
	}

	public void setServiceRegistration(ServiceRegistration serviceRegistration){
		this.mServiceRegistration = serviceRegistration;
	}

	public ServiceRegistration getServiceRegistration() {
		return mServiceRegistration;
	}

	@Override
	protected void fireFeedComponentChanged() {
		if (mServiceRegistration != null)
			mServiceRegistration.setProperties(null);
	}

	public boolean hasExceptionOccurred() {
		return mExceptionOccurred;
	}

	public void setExceptionOccurred(boolean exceptionOccurred) {
		this.mExceptionOccurred = exceptionOccurred;
	}
}
