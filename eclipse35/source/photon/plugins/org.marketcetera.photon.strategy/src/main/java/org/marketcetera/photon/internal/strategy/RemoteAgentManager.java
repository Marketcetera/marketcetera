package org.marketcetera.photon.internal.strategy;

import java.net.URI;

import javax.management.AttributeChangeNotification;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.marketcetera.module.DataFlowException;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataFlowNotFoundException;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.InvalidURNException;
import org.marketcetera.module.MXBeanOperationException;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleNotFoundException;
import org.marketcetera.module.ModuleState;
import org.marketcetera.module.ModuleStateException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.URNUtils;
import org.marketcetera.modules.remote.emitter.EmitterFactory;
import org.marketcetera.modules.remote.emitter.EmitterModuleMXBean;
import org.marketcetera.photon.internal.strategy.AbstractStrategyConnection.State;
import org.marketcetera.photon.module.ModuleSupport;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Manages a single remote agent connection and updates the agent bean accordingly.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
class RemoteAgentManager {

	private static final String CONNECTION_STATUS_ATTRIBUTE = "Connected"; //$NON-NLS-1$

	private static final String URL_ATTRIBUTE = "URL"; //$NON-NLS-1$

	private static final String USERNAME_ATTRIBUTE = "Username"; //$NON-NLS-1$

	private static final String PASSWORD_ATTRIBUTE = "Password"; //$NON-NLS-1$

	private final ModuleManager mModuleManager;

	private final MBeanServerConnection mMBeanServer;

	private final RemoteStrategyAgent mAgent;

	private final ModuleURN mAgentURN;

	private DataFlowID mSinkFlowID;

	private NotificationListener mConnectionTracker;

	/**
	 * Constructor.
	 * 
	 * @param moduleManager
	 *            module manager
	 * @param beanServer
	 *            MBean server for module manipulation
	 * @param agent
	 *            the remote agent bean
	 * @param instanceName
	 *            the instance name to use for the module
	 * @throws IllegalArgumentException
	 *             if any parameters are null
	 * @throws InvalidURNException
	 *             if the instance name is invalid
	 */
	RemoteAgentManager(ModuleManager moduleManager, MBeanServerConnection beanServer,
			RemoteStrategyAgent agent, String instanceName) throws InvalidURNException {
		Validate.noNullElements(new Object[] { moduleManager, beanServer, agent, instanceName });
		mModuleManager = moduleManager;
		mMBeanServer = beanServer;
		mAgent = agent;
		mAgentURN = new ModuleURN(EmitterFactory.PROVIDER_URN, instanceName);
		URNUtils.validateInstanceURN(mAgentURN, EmitterFactory.PROVIDER_URN);
	}

	/**
	 * Initializes the remote agent connection. If the operation succeeds, the agent will be in the
	 * running state.
	 * 
	 * This method may open a message dialog and thus assumes it is being called as a result of a
	 * user operation.
	 * 
	 * @return the result of the operation
	 */
	synchronized IStatus connect() {
		// the agent (UI) should be in the stopped state
		if (mAgent.getState() != State.STOPPED) {
			assert false : mAgent.getState();
			SLF4JLoggerProxy.debug(this, "Unexpected invocation of connect(), agent state is {0}.", mAgent.getState()); //$NON-NLS-1$
			mAgent.setState(State.STOPPED);
		}

		// ensure the module doesn't exist
		try {
			// this call should throw ModuleNotFoundException
			mModuleManager.getModuleInfo(mAgentURN);
			// the exception wasn't thrown so we have to deal with an existing module
			if (MessageDialog.openQuestion(null, null,
					Messages.REMOTE_AGENT_MANAGER_DELETE_MODULE_QUESTION.getText())) {
				try {
					deleteModule();
				} catch (ModuleException e) {
					// delete attempt was not successful
					Messages.REMOTE_AGENT_MANAGER_DISCONNECT_FAILED.error(this);
					return createError(Messages.REMOTE_AGENT_MANAGER_DISCONNECT_FAILED.getText(), e);
				}
			} else {
				// user declined to attempt deletion
				SLF4JLoggerProxy.debug(this, "User declined to attempt module deletion."); //$NON-NLS-1$
				return Status.CANCEL_STATUS;
			}
		} catch (ModuleNotFoundException e) {
			// do nothing, this is the happy path
		} catch (InvalidURNException e) {
			// very strange since the URN is final and validated in the constructor
			// don't know how to recover from this
			throw new IllegalStateException(e);
		}

		// at this point the module doesn't exist, so the sink data flow doesn't exist
		// the id should not null, but if it is not, we can recover
		assert mSinkFlowID == null;
		mSinkFlowID = null;

		// the connection tracker shouldn't exist either
		assert mConnectionTracker == null;
		mConnectionTracker = null;

		// ensure the URI has been specified
		URI remoteAgentURI = mAgent.getURI();
		if (remoteAgentURI == null) {
			return createError(Messages.REMOTE_AGENT_MANAGER_MISSING_URI.getText());
		}

		// set the configuration properties, using module creation defaults since the emitter module
		// auto-starts itself
		ModuleSupport.getModuleAttributeSupport().setDefaultFor(mAgentURN, URL_ATTRIBUTE,
				remoteAgentURI.toString());
		ModuleSupport.getModuleAttributeSupport().setDefaultFor(mAgentURN, USERNAME_ATTRIBUTE,
				StringUtils.defaultString(mAgent.getUsername()));
		ModuleSupport.getModuleAttributeSupport().setDefaultFor(mAgentURN, PASSWORD_ATTRIBUTE,
				StringUtils.defaultString(mAgent.getPassword()));

		// create and start the module
		boolean success = false;
		try {
			ModuleURN urn =
					mModuleManager.createModule(EmitterFactory.PROVIDER_URN, mAgentURN
							.instanceName());

			// assert the factory gives the urn we expect
			if (!mAgentURN.equals(urn)) {
				throw new IllegalStateException(urn.toString());
			}
			// assert that the module indeed auto-started, need to use try catch since
			// we want a runtime exception no matter what goes wrong in the validation
			try {
				ModuleState state = mModuleManager.getModuleInfo(mAgentURN).getState();
				if (!state.isStarted()) {
					throw new IllegalStateException(state.toString());
				}
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}

			// subscribe to connection status notifications
			mConnectionTracker = new ConnectionTracker();
			try {
				((NotificationEmitter) getMXBean()).addNotificationListener(mConnectionTracker,
						new DisconnectFilter(), null);
			} catch (Exception e) {
				// something is wrong with the emitter module, but we can still continue
				assert false : e;
				Messages.REMOTE_AGENT_MANAGER_COULD_NOT_SUBSCRIBE_TO_STATUS_UPDATES.warn(this, e);
				mConnectionTracker = null;
			}

			// set up data flow to sink
			mSinkFlowID =
					mModuleManager.createDataFlow(new DataRequest[] { new DataRequest(mAgentURN) },
							true);

			// success!
			mAgent.setState(State.RUNNING);
			success = true;
		} catch (ModuleException e) {
			// createModule or createDataFlow failed
			Messages.REMOTE_AGENT_MANAGER_CONNECT_FAILED.error(this, e, remoteAgentURI);
			MultiStatus status =
					new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR,
							Messages.REMOTE_AGENT_MANAGER_CONNECT_FAILED.getText(remoteAgentURI), e);
			// add the underlying cause if it exists
			if (e.getCause() != null) {
				status.add(createError(e.getCause().getLocalizedMessage()));
			}
			return status;
		} finally {
			if (!success) {
				try {
					deleteModule();
				} catch (ModuleException e) {
					Messages.REMOTE_AGENT_MANAGER_CLEANUP_FAILED.error(this, e);
				}
			}
		}
		return Status.OK_STATUS;
	}

	/**
	 * Shuts down the remote agent connection. If the operation succeeds, the agent will be in the
	 * stopped state.
	 * 
	 * @return the result of the operation
	 */
	synchronized IStatus disconnect() {
		// This variable caches the connection tracker in case it needs to be reinstalled
		// in the finally clause
		NotificationListener lastTracker = mConnectionTracker;
		
		// This invalidates the current tracker
		mConnectionTracker = null;
		
		boolean success = false;
		try {
			deleteModule();
			mAgent.setState(State.STOPPED);
			success = true;
		} catch (ModuleException e) {
			Messages.REMOTE_AGENT_MANAGER_DISCONNECT_FAILED.error(this, e);
			return createError(Messages.REMOTE_AGENT_MANAGER_DISCONNECT_FAILED.getText(), e);
		} finally {
			if (!success) {
				// something went wrong, try to reinstall the connection tracker
				mConnectionTracker = lastTracker;
			}
		}
		return Status.OK_STATUS;
	}

	/**
	 * Terminates the sink flow (if necessary) and deletes the remote emitter module.
	 * 
	 * If an exception is not thrown, then it is guaranteed that the module does not exist and
	 * mSinkFlowID is null. If an exception is thrown, then mSinkFlowID is still guaranteed to be
	 * null unless the data flow could not be canceled.
	 * 
	 * @param info
	 *            the most recent ModuleInfo for the remote emitter module
	 * @throws ModuleException
	 *             if the module exists, but the delete failed
	 */
	private void deleteModule() throws ModuleException {
		boolean success = false;
		try {
			ModuleInfo info = mModuleManager.getModuleInfo(mAgentURN);
			try {
				if (info.getState().isStarted()) {
					// make sure the data flow to sink is stopped
					if (mSinkFlowID != null) {
						try {
							mModuleManager.cancel(mSinkFlowID);
							mSinkFlowID = null;
						} catch (DataFlowNotFoundException e) {
							// somehow the data flow was stopped without mSinkFlowID getting set to
							// null
							// this is unexpected, but we can recover
							assertFalseOrDebug(e);
							mSinkFlowID = null;
						} catch (ModuleStateException e) {
							// strange since the module was in the started state above
							// this is unexpected, but we may be able to recover
							assertFalseOrDebug(e);
						} catch (DataFlowException e) {
							// strange since no one else should be manipulating this data flow
							// this is unexpected, but we may be able to recover
							assertFalseOrDebug(e);
						}
					}
					mModuleManager.stop(mAgentURN);
				}
				mModuleManager.deleteModule(mAgentURN);
				success = true;
			} catch (ModuleNotFoundException e) {
				// strange since the module was in the started state above
				// this is unexpected, but we can recover
				assertFalseOrDebug(e);
			}
		} catch (ModuleNotFoundException e) {
			// couldn't get module info because the module wasn't found
			// that means our work is done
		} catch (InvalidURNException e) {
			// very strange since the URN is final and validated in the constructor
			// don't know how to recover from this
			throw new IllegalStateException(e);
		} finally {
			if (success) {
				// the module was successfully deleted so the data flow should be null
				mSinkFlowID = null;
			}
		}
	}

	private EmitterModuleMXBean getMXBean() throws MXBeanOperationException {
		return JMX.newMXBeanProxy(mMBeanServer, mAgentURN.toObjectName(),
				EmitterModuleMXBean.class, true);
	}

	/**
	 * Handles an unexpected exception that we still try to recover from. When assertions are
	 * enabled, this throws an assertion error. Otherwise, we just log the exception to the debug
	 * log.
	 * 
	 * @param e
	 *            the unexpected exception
	 */
	private void assertFalseOrDebug(Exception e) {
		assert false : e;
		org.marketcetera.util.except.Messages.THROWABLE_IGNORED.debug(this, e);
	}

	private Status createError(String message) {
		return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
	}

	private Status createError(String message, Throwable throwable) {
		return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, throwable);
	}

	/**
	 * Filter that matches only disconnect notifications.
	 */
	@ClassVersion("$Id$")
	private static final class DisconnectFilter implements NotificationFilter {

		private static final long serialVersionUID = 1L;

		@Override
		public boolean isNotificationEnabled(Notification notification) {
			if (notification instanceof AttributeChangeNotification) {
				AttributeChangeNotification change = (AttributeChangeNotification) notification;
				Object newValue = change.getNewValue();
				return change.getAttributeName().equals(CONNECTION_STATUS_ATTRIBUTE)
						&& newValue instanceof Boolean && (Boolean) newValue == false;
			}
			return false;
		}
	};

	/**
	 * Cleans up when a connection goes down. Intended to be used only in conjunction with
	 * {@link DisconnectFilter}.
	 */
	@ClassVersion("$Id$")
	private class ConnectionTracker implements NotificationListener {

		@Override
		public void handleNotification(Notification notification, Object handback) {
			SLF4JLoggerProxy.debug(RemoteAgentManager.this, "Received disconnect notification."); //$NON-NLS-1$
			synchronized (RemoteAgentManager.this) {
				if (mConnectionTracker == this) {
					// try to log the error
					String cause =
							Messages.REMOTE_AGENT_MANAGER_LOST_CONNECTION_UNKNOWN_CAUSE.getText();
					try {
						cause = getMXBean().getLastFailure();
					} catch (Exception e) {
						// maybe the module no longer exists, or something else is wrong
						// ignore the exception, the default cause will be used
						org.marketcetera.util.except.Messages.THROWABLE_IGNORED.debug(this, e);
					}
					Messages.REMOTE_AGENT_MANAGER_LOST_CONNECTION.error(
							org.marketcetera.core.Messages.USER_MSG_CATEGORY, cause);
					disconnect();
				} else {
					// this tracker is obsolete
					// note, this will always happen once during a normal disconnect since the emitter
					// module fires a disconnect notification during stop
					SLF4JLoggerProxy.debug(RemoteAgentManager.this,
							"Ignoring notification to obsolete tracker."); //$NON-NLS-1$
				}
			}
		}

	};
}
