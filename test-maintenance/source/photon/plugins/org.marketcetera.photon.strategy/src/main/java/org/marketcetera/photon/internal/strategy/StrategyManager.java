package org.marketcetera.photon.internal.strategy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.apache.commons.lang.Validate;
import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.marketcetera.core.Util;
import org.marketcetera.module.InvalidURNException;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.SinkModuleFactory;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.internal.strategy.AbstractStrategyConnection.State;
import org.marketcetera.photon.module.ModuleSupport;
import org.marketcetera.strategy.Language;
import org.marketcetera.strategy.StrategyMXBean;
import org.marketcetera.strategy.StrategyModuleFactory;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

/* $License$ */

/**
 * Manages a collection of {@link Strategy} objects and interfaces with the underlying Module
 * Framework.
 * 
 * This class manages a {@link WritableList} of {@link Strategy} objects, and as such, it is thread
 * safe. An exception will be thrown if it is accessed from any thread other than the one that
 * created it.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public final class StrategyManager {

	/**
	 * Returns the singleton instance for the currently running plug-in.
	 * 
	 * @return the singleton instance
	 */
	public static StrategyManager getCurrent() {
		return Activator.getDefault().getStrategyManager();
	}

	/**
	 * Name of file where this object's state is persisted.
	 */
	private static final String STRATEGIES_FILENAME = "strategies.xml"; //$NON-NLS-1$

	/**
	 * Root tag for persisted strategies
	 */
	private static final String STRATEGIES_TAG = "strategies"; //$NON-NLS-1$

	/**
	 * Tag for a single strategy
	 */
	private static final String STRATEGY_TAG = "strategy"; //$NON-NLS-1$

	/**
	 * Attribute for a strategy connection display name
	 */
	private static final String DISPLAY_NAME_ATTRIBUTE = "displayName"; //$NON-NLS-1$

	/**
	 * Attribute for a strategy class name
	 */
	private static final String CLASS_NAME_ATTRIBUTE = "className"; //$NON-NLS-1$

	/**
	 * Attribute for a strategy routing
	 */
	private static final String ROUTE_TO_SERVER_ATTRIBUTE = "routeToServer"; //$NON-NLS-1$

	/**
	 * Attribute for a strategy script file
	 */
	private static final String SCRIPT_ATTRIBUTE = "script"; //$NON-NLS-1$

	/**
	 * Tag for a property
	 */
	private static final String PROPERTY_TAG = "property"; //$NON-NLS-1$

	/**
	 * Attribute for a property key
	 */
	private static final String KEY_ATTRIBUTE = "key"; //$NON-NLS-1$

	/**
	 * Attribute for a property value
	 */
	private static final String VALUE_ATTRIBUTE = "value"; //$NON-NLS-1$

	/**
	 * Tag for a single remote strategy agent
	 */
	private static final String REMOTE_AGENT_TAG = "remoteAgent"; //$NON-NLS-1$

	/**
	 * Attribute for a remote agent URI
	 */
	private static final String URI_ATTRIBUTE = "uri"; //$NON-NLS-1$

	/**
	 * Attribute for a remote agent username
	 */
	private static final String USERNAME_ATTRIBUTE = "username"; //$NON-NLS-1$

	/**
	 * Attribute for a remote agent password
	 */
	private static final String PASSWORD_ATTRIBUTE = "password"; //$NON-NLS-1$

	/**
	 * Currently, only one remote instance is supported and the instance name is hardcoded
	 */
	private static final String REMOTE_INSTANCE_NAME = "photon"; //$NON-NLS-1$

	private final MBeanServerConnection mMBeanServer = ModuleSupport.getMBeanServerConnection();

	private final ModuleManager mModuleManager = ModuleSupport.getModuleManager();

	private final Map<RemoteStrategyAgent, RemoteAgentManager> mRemoteAgentManagers = new HashMap<RemoteStrategyAgent, RemoteAgentManager>();

	/**
	 * The singleton remote agent
	 */
	private RemoteStrategyAgent mRemoteAgent;

	private final WritableList mStrategies = WritableList
			.withElementType(AbstractStrategyConnection.class);

	private final SetMultimap<IFile, Strategy> mTrackedFiles = Multimaps
			.synchronizedSetMultimap(HashMultimap.<IFile, Strategy> create());

	private final IResourceDeltaVisitor mResourceDeltaVisitor = new IResourceDeltaVisitor() {

		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			if (delta.getResource() instanceof IFile && delta.getKind() == IResourceDelta.REMOVED) {
				Set<Strategy> strategies = ImmutableSet.copyOf(mTrackedFiles.get((IFile) delta.getResource()));
				for (Strategy strategy : strategies) {
					removeStrategy(strategy);
				}
				return false;
			}
			return true;
		}
	};

	/**
	 * This object should only be constructed by {@link Activator}.
	 */
	StrategyManager() {
		restoreState();
		// we track resource changes so we can unregister strategies when there files
		// are deleted
		ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener() {
			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				try {
					event.getDelta().accept(mResourceDeltaVisitor);
				} catch (CoreException e) {
					SLF4JLoggerProxy.error(StrategyManager.this, e);
				}
			}
		}, IResourceChangeEvent.POST_CHANGE);
	}

	/**
	 * Returns the collection of registered strategies.
	 * 
	 * @return the registered strategies
	 */
	public IObservableList getStrategies() {
		return Observables.unmodifiableObservableList(mStrategies);
	}

	/**
	 * Starts the given strategy.
	 * 
	 * @param strategy
	 *            strategy to start
	 */
	public void start(Strategy strategy) {
		try {
			mModuleManager.start(strategy.getURN());
			strategy.setState(State.RUNNING);
		} catch (ModuleException e) {
			PhotonPlugin.getMainConsoleLogger().error(
					Messages.STRATEGY_MANAGER_STRATEGY_START_FAILED.getText(strategy
							.getDisplayName()));
			Messages.STRATEGY_MANAGER_STRATEGY_START_FAILED.error(this, e, strategy
					.getDisplayName());
		}
	}

	/**
	 * Stops the given strategy.
	 * 
	 * @param strategy
	 *            strategy to start
	 */
	public void stop(Strategy strategy) {
		try {
			mModuleManager.stop(strategy.getURN());
			strategy.setState(State.STOPPED);
		} catch (ModuleException e) {
			PhotonPlugin.getMainConsoleLogger().error(
					Messages.STRATEGY_MANAGER_STRATEGY_STOP_FAILED.getText(strategy
							.getDisplayName()));
			Messages.STRATEGY_MANAGER_STRATEGY_STOP_FAILED
					.error(this, e, strategy.getDisplayName());
		}
	}

	/**
	 * Sets the human readable name of the given strategy.
	 * 
	 * @param strategy
	 *            strategy to be changed
	 * @param name
	 *            the new human readable name
	 */
	public void setDisplayName(AbstractStrategyConnection strategy, String name) {
		strategy.setDisplayName(name);
		saveState();
	}

	/**
	 * Sets the parameters for the given strategy.
	 * 
	 * Note: currently the UI prevents this from being called when the strategy module is stopped.
	 * 
	 * @param strategy
	 *            strategy to be changed
	 * @param parameters
	 *            the new parameters
	 */
	public void setParameters(Strategy strategy, Properties parameters) {
		try {
			ObjectName objectName = strategy.getURN().toObjectName();
			StrategyMXBean proxy = JMX.newMXBeanProxy(mMBeanServer, objectName,
					StrategyMXBean.class);
			proxy.setParameters(Util.propertiesToString(parameters));
			strategy.setParameters(parameters);
			saveState();
		} catch (Exception e) {
			PhotonPlugin.getMainConsoleLogger().error(e.getLocalizedMessage());
			ExceptUtils.swallow(e);
		}
	}

	/**
	 * Specify whether the given strategy should route orders to the server.
	 * 
	 * Note: currently the UI prevents this from being called when the strategy module is stopped
	 * 
	 * @param strategy
	 *            strategy to be changed
	 * @param routeToServer
	 *            true if the strategy should send orders to the server, false otherwise
	 */
	public void setRouteToServer(Strategy strategy, boolean routeToServer) {
		try {
			ObjectName objectName = strategy.getURN().toObjectName();
			StrategyMXBean proxy = JMX.newMXBeanProxy(mMBeanServer, objectName,
					StrategyMXBean.class);
			proxy.setIsRountingOrdersToORS(routeToServer);
			strategy.setRouteToServer(routeToServer);
			saveState();
		} catch (Exception e) {
			PhotonPlugin.getMainConsoleLogger().error(e.getLocalizedMessage());
			ExceptUtils.swallow(e);
		}
	}

	/**
	 * Creates a new strategy for the given parameters.
	 * 
	 * @param file
	 *            strategy script file
	 * @param className
	 *            name of strategy class in script to run
	 * @param displayName
	 *            human readable display name
	 * @param routeToServer
	 *            true if the strategy should send orders to the server, false otherwise
	 */
	public void registerStrategy(IFile file, String className, String displayName,
			boolean routeToServer) {
		Properties parameters = new Properties();
		internalRegisterStrategy(file, className, displayName, routeToServer, parameters);
		saveState();
	}

	private void internalRegisterStrategy(IFile file, String className, String displayName,
			boolean routeToServer, Properties parameters) {
		try {
			ModuleURN urn = mModuleManager.createModule(StrategyModuleFactory.PROVIDER_URN, null,
					className, Language.RUBY, file.getLocation().toFile(), parameters,
					routeToServer, SinkModuleFactory.INSTANCE_URN);
			Strategy strategy = new Strategy(displayName, urn, file, className, routeToServer,
					parameters);
			mStrategies.add(strategy);
			mTrackedFiles.put(file, strategy);
		} catch (ModuleException e) {
			PhotonPlugin.getMainConsoleLogger().error(e.getLocalizedMessage());
			ExceptUtils.swallow(e);
		}
	}

	/**
	 * Returns whether the provided name is unique among the display names of registered strategies.
	 * 
	 * @param name
	 *            name
	 * @return true if no other registered strategy uses the provided name
	 */
	public boolean isUniqueName(String name) {
		for (Object object : mStrategies) {
			if (((AbstractStrategyConnection) object).getDisplayName().equals(name)) {
				return false;
			}
		}
		return true;
	}

	public void enableRemoteAgent() {
		if (mRemoteAgent == null) {
			mRemoteAgent = new RemoteStrategyAgent(
					Messages.STRATEGY_MANAGER_REMOTE_AGENT_DISPLAY_NAME.getText());
			try {
				mRemoteAgentManagers.put(mRemoteAgent, new RemoteAgentManager(mModuleManager,
						mMBeanServer, mRemoteAgent, REMOTE_INSTANCE_NAME));
			} catch (InvalidURNException e) {
				// the name is hardcoded to a valid value - should never happen
				throw new IllegalStateException(e);
			}
		}
		if (!showingRemoteAgent()) {
			mStrategies.add(0, mRemoteAgent);
		}
	}

	public void disableRemoteAgent() {
		if (showingRemoteAgent()) {
			disconnect(mRemoteAgent);
			mStrategies.remove(0);
		}
	}

	public void connect(RemoteStrategyAgent agent) {
		IStatus status = getManager(agent).connect();
		if (!status.isOK()) {
			ErrorDialog.openError(null, null, null, status);
		}
	}

	public void disconnect(RemoteStrategyAgent agent) {
		IStatus status = getManager(agent).disconnect();
		if (!status.isOK()) {
			ErrorDialog.openError(null, null, null, status);
		}
	}

	private RemoteAgentManager getManager(RemoteStrategyAgent agent) {
		RemoteAgentManager manager = mRemoteAgentManagers.get(agent);
		if (manager == null) {
			throw new IllegalArgumentException("Invalid agent"); //$NON-NLS-1$
		}
		return manager;
	}

	/**
	 * Update a {@link RemoteStrategyAgent}.
	 * 
	 * @param agent
	 *            the agent to update, should not be null
	 * @param uri
	 *            the new URI, or null if it should be unset
	 * @param username
	 *            the new username, or null if it should be unset
	 * @param password
	 *            the new password, or null if it should be unset
	 * @throws IllegalArgumentException
	 *             if agent is null
	 */
	public void updateAgent(RemoteStrategyAgent agent, URI uri, String username, String password) {
		Validate.notNull(agent);
		agent.setURI(uri);
		agent.setUsername(username);
		agent.setPassword(password);
		saveState();
	}

	private boolean showingRemoteAgent() {
		return mStrategies.size() > 0 && mStrategies.get(0) == mRemoteAgent;
	}

	/**
	 * Removes a registered strategy.
	 * 
	 * @param strategy
	 *            strategy to remove
	 */
	public void removeStrategy(Strategy strategy) {
		try {
			ModuleURN urn = strategy.getURN();
			if (mModuleManager.getModuleInfo(urn).getState().isStarted()) {
				stop(strategy);
			}
			mModuleManager.deleteModule(urn);
			mStrategies.remove(strategy);
			mTrackedFiles.remove(strategy.getFile(), strategy);
			saveState();
		} catch (ModuleException e) {
			PhotonPlugin.getMainConsoleLogger().error(e.getLocalizedMessage());
			ExceptUtils.swallow(e);
		}
	}

	private void saveState() {
		File file = getPersistFile();
		XMLMemento mem = XMLMemento.createWriteRoot(STRATEGIES_TAG);
		for (Object object : mStrategies) {
			if (object instanceof Strategy) {
				Strategy strategy = (Strategy) object;
				IMemento strategyMem = mem.createChild(STRATEGY_TAG);
				strategyMem.putString(DISPLAY_NAME_ATTRIBUTE, strategy.getDisplayName());
				strategyMem
						.putString(SCRIPT_ATTRIBUTE, strategy.getFile().getFullPath().toString());
				strategyMem.putString(CLASS_NAME_ATTRIBUTE, strategy.getClassName());
				strategyMem.putBoolean(ROUTE_TO_SERVER_ATTRIBUTE, strategy.getRouteToServer());
				for (Map.Entry<Object, Object> entry : strategy.getParameters().entrySet()) {
					IMemento propertyMem = strategyMem.createChild(PROPERTY_TAG);
					propertyMem.putString(KEY_ATTRIBUTE, (String) entry.getKey());
					propertyMem.putString(VALUE_ATTRIBUTE, (String) entry.getValue());
				}
			} else if (object instanceof RemoteStrategyAgent) {
				RemoteStrategyAgent agent = (RemoteStrategyAgent) object;
				IMemento agentMem = mem.createChild(REMOTE_AGENT_TAG);
				agentMem.putString(DISPLAY_NAME_ATTRIBUTE, agent.getDisplayName());
				agentMem.putString(URI_ATTRIBUTE, agent.getURI().toString());
				agentMem.putString(USERNAME_ATTRIBUTE, agent.getUsername());
				agentMem.putString(PASSWORD_ATTRIBUTE, encode(agent.getPassword()));
			}
		}
		try {
			FileOutputStream stream = new FileOutputStream(file);
			OutputStreamWriter writer = new OutputStreamWriter(stream, "utf-8"); //$NON-NLS-1$
			mem.save(writer);
			writer.close();
		} catch (IOException e) {
			file.delete();
			Messages.STRATEGY_MANAGER_PERSIST_FAILED.error(this, e);
		}
	}

	private void restoreState() {
		File file = getPersistFile();
		if (file.exists()) {
			try {
				FileInputStream input = new FileInputStream(file);
				BufferedReader reader = new BufferedReader(new InputStreamReader(input, "utf-8")); //$NON-NLS-1$
				IMemento mem = XMLMemento.createReadRoot(reader);
				for (IMemento strategyMem : mem.getChildren(STRATEGY_TAG)) {
					String displayName = strategyMem.getString(DISPLAY_NAME_ATTRIBUTE);
					String script = strategyMem.getString(SCRIPT_ATTRIBUTE);
					IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
					IResource resource = root.findMember(new Path(script));
					if (resource instanceof IFile) {
						String className = strategyMem.getString(CLASS_NAME_ATTRIBUTE);
						boolean routeToServer = strategyMem.getBoolean(ROUTE_TO_SERVER_ATTRIBUTE) == Boolean.TRUE ? true
								: false;
						Properties properties = new Properties();
						for (IMemento propertyMem : strategyMem.getChildren(PROPERTY_TAG)) {
							String key = propertyMem.getString(KEY_ATTRIBUTE);
							String value = propertyMem.getString(VALUE_ATTRIBUTE);
							properties.put(key, value);
						}
						internalRegisterStrategy((IFile) resource, className, displayName,
								routeToServer, properties);

					} else {
						Messages.STRATEGY_MANAGER_SCRIPT_NOT_FOUND.warn(this, displayName, script);
					}
				}
				IMemento[] agents = mem.getChildren(REMOTE_AGENT_TAG);
				if (agents.length > 0) {
					// only support one now
					assert agents.length == 1;
					enableRemoteAgent();
					IMemento agentMem = agents[0];
					try {
						mRemoteAgent.setURI(new URI(agentMem.getString(URI_ATTRIBUTE)));
					} catch (URISyntaxException e) {
						Messages.STRATEGY_MANAGER_RESTORE_URI_FAILED.warn(this, e);
					}
					mRemoteAgent.setUsername(agentMem.getString(USERNAME_ATTRIBUTE));
					mRemoteAgent.setPassword(decode(agentMem.getString(PASSWORD_ATTRIBUTE)));
				}
			} catch (Exception e) {
				Messages.STRATEGY_MANAGER_RESTORE_FAILED.error(this, e);
			}
		} else {
			SLF4JLoggerProxy.debug(this,
					"Did not load persisted strategy information because the file does not exist."); //$NON-NLS-1$
		}

	}

	private String encode(String string) {
		if (string == null) return null;
		try {
			return new BigInteger(string.getBytes("UTF-8")).toString(Character.MAX_RADIX); //$NON-NLS-1$
		} catch (UnsupportedEncodingException e) {
			// java should have UTF-8
			throw new IllegalStateException(e);
		}
	}

	private String decode(String string) {
		if (string == null) return null;
		try {
			return new String(new BigInteger(string, Character.MAX_RADIX).toByteArray(), "UTF-8"); //$NON-NLS-1$
		} catch (UnsupportedEncodingException e) {
			// java should have UTF-8
			throw new IllegalStateException(e);
		}
	}

	private File getPersistFile() {
		return Activator.getDefault().getStateLocation().append(STRATEGIES_FILENAME).toFile();
	}

}
