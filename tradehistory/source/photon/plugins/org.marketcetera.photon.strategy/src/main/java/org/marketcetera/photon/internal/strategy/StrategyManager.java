package org.marketcetera.photon.internal.strategy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Properties;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.jruby.exceptions.RaiseException;
import org.marketcetera.core.Util;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.internal.strategy.Strategy.Destination;
import org.marketcetera.photon.internal.strategy.Strategy.State;
import org.marketcetera.photon.module.ModulePlugin;
import org.marketcetera.scripting.ScriptLoggingUtil;
import org.marketcetera.strategy.Language;
import org.marketcetera.strategy.StrategyMXBean;
import org.marketcetera.strategy.StrategyModuleFactory;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Manages a collection of {@link Strategy} objects and interfaces with the
 * underlying Module Framework.
 * 
 * This class manages a {@link WritableList} of {@link Strategy} objects, and
 * as such, it is thread safe. An exception will be thrown if it is accessed
 * from any thread other than the one that created it.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
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
	 * Attribute for a strategy display name
	 */
	private static final String DISPLAY_NAME_ATTRIBUTE = "displayName"; //$NON-NLS-1$

	/**
	 * Attribute for a strategy class name
	 */
	private static final String CLASS_NAME_ATTRIBUTE = "className"; //$NON-NLS-1$

	/**
	 * Attribute for a strategy order destination
	 */
	private static final String DESTINATION_ATTRIBUTE = "destination"; //$NON-NLS-1$

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

	private final MBeanServerConnection mMBeanServer = ModulePlugin.getDefault().getMBeanServerConnection();
	
	private final ModuleManager mModuleManager = ModulePlugin.getDefault()
			.getModuleManager();

	private final WritableList mStrategies = WritableList
			.withElementType(Strategy.class);

	/**
	 * This object should only be constructed by {@link Activator}.
	 */
	StrategyManager() {
		restoreState();
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
			e.getI18NBoundMessage().error(this, e);
			PhotonPlugin.getMainConsoleLogger().error(
					Messages.STRATEGY_MANAGER_STRATEGY_START_FAILED
							.getText(strategy.getDisplayName()));
			if (e.getCause() instanceof RaiseException) {
				ScriptLoggingUtil.error(PhotonPlugin.getMainConsoleLogger(),
						(RaiseException) e.getCause());
				return;
			}
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
			// TODO: report to user
			e.getI18NBoundMessage().error(this);
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
	public void setDisplayName(Strategy strategy, String name) {
		strategy.setDisplayName(name);
		saveState();
	}

	/**
	 * Sets the parameters for the given strategy.
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
			// TODO: report to user
			ExceptUtils.swallow(e);
		}
	}

	/**
	 * Sets the order destination for the given strategy.
	 * 
	 * @param strategy
	 *            strategy to be changed
	 * @param destination
	 *            the new order destination
	 */
	public void setDestination(Strategy strategy, Destination destination) {
		try {
			ObjectName objectName = strategy.getURN().toObjectName();
			StrategyMXBean proxy = JMX.newMXBeanProxy(mMBeanServer, objectName,
					StrategyMXBean.class);
			proxy.setOrdersDestination(destination.getURN().toString());
			strategy.setDestination(destination);
			saveState();
		} catch (Exception e) {
			// TODO: report to user
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
	 * @param destination
	 *            order destination
	 */
	public void registerStrategy(IFile file, String className,
			String displayName, Destination destination) {
		Properties parameters = new Properties();
		internalRegisterStrategy(file, className, displayName, destination,
				parameters);
		saveState();
	}

	private void internalRegisterStrategy(IFile file, String className,
			String displayName, Destination destination, Properties parameters) {
		try {
			ModuleURN urn = mModuleManager.createModule(
					StrategyModuleFactory.PROVIDER_URN, className,
					Language.RUBY, file.getLocation().toFile(), parameters,
					null, destination.getURN(), TradeSuggestionManager
							.getCurrent().getReceiverURN());
			Strategy strategy = new Strategy(urn, file, className, destination,
					parameters);
			strategy.setDisplayName(displayName);
			strategy.setState(State.STOPPED);
			mStrategies.add(strategy);
		} catch (ModuleException e) {
			// TODO: report to user
			e.getI18NBoundMessage().error(this);
		}
	}

	/**
	 * Returns whether the provided name is unique among the display names of
	 * registered strategies.
	 * 
	 * @param name
	 *            name
	 * @return true if no other registered strategy uses the provided name
	 */
	public boolean isUniqueName(String name) {
		for (Object object : mStrategies) {
			if (((Strategy) object).getDisplayName().equals(name)) {
				return false;
			}
		}
		;
		return true;
	}

	/**
	 * Removes a registered strategy.
	 * 
	 * @param strategy
	 *            strategy to remove
	 */
	public void removeStrategy(Strategy strategy) {
		stop(strategy);
		try {
			mModuleManager.deleteModule(strategy.getURN());
			mStrategies.remove(strategy);
			saveState();
		} catch (ModuleException e) {
			// TODO: report to user
			e.getI18NBoundMessage().error(this);
		}
	}

	private void saveState() {
		File file = getPersistFile();
		XMLMemento mem = XMLMemento.createWriteRoot(STRATEGIES_TAG);
		for (Object object : mStrategies) {
			Strategy strategy = (Strategy) object;
			IMemento strategyMem = mem.createChild(STRATEGY_TAG);
			strategyMem.putString(DISPLAY_NAME_ATTRIBUTE, strategy
					.getDisplayName());
			strategyMem.putString(SCRIPT_ATTRIBUTE, strategy.getFile()
					.getFullPath().toString());
			strategyMem
					.putString(CLASS_NAME_ATTRIBUTE, strategy.getClassName());
			strategyMem.putString(DESTINATION_ATTRIBUTE, strategy
					.getDestination().name());
			for (Map.Entry<Object, Object> entry : strategy.getParameters()
					.entrySet()) {
				IMemento propertyMem = strategyMem.createChild(PROPERTY_TAG);
				propertyMem.putString(KEY_ATTRIBUTE, (String) entry.getKey());
				propertyMem.putString(VALUE_ATTRIBUTE, (String) entry
						.getValue());
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
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(input, "utf-8")); //$NON-NLS-1$
				IMemento mem = XMLMemento.createReadRoot(reader);
				for (IMemento strategyMem : mem.getChildren(STRATEGY_TAG)) {
					String displayName = strategyMem
							.getString(DISPLAY_NAME_ATTRIBUTE);
					String script = strategyMem.getString(SCRIPT_ATTRIBUTE);
					IWorkspaceRoot root = ResourcesPlugin.getWorkspace()
							.getRoot();
					IResource resource = root.findMember(new Path(script));
					if (resource instanceof IFile) {
						String className = strategyMem
								.getString(CLASS_NAME_ATTRIBUTE);
						Destination destination = Destination.SINK;
						try {
							destination = Destination.valueOf(strategyMem
									.getString(DESTINATION_ATTRIBUTE));
						} catch (Exception e) {
							// Let it default to sink
							Messages.STRATEGY_MANAGER_INVALID_DESTINATION.warn(this, e, script);
						}
						Properties properties = new Properties();
						for (IMemento propertyMem : strategyMem
								.getChildren(PROPERTY_TAG)) {
							String key = propertyMem.getString(KEY_ATTRIBUTE);
							String value = propertyMem
									.getString(VALUE_ATTRIBUTE);
							properties.put(key, value);
						}
						internalRegisterStrategy((IFile) resource, className,
								displayName, destination, properties);

					} else {
						Messages.STRATEGY_MANAGER_SCRIPT_NOT_FOUND.warn(this,
								displayName, script);
					}
				}
			} catch (Exception e) {
				Messages.STRATEGY_MANAGER_RESTORE_FAILED.error(this, e);
			}
		} else {
			SLF4JLoggerProxy
					.debug(this,
							"Did not load persisted strategy information because the file does not exist."); //$NON-NLS-1$
		}

	}

	private File getPersistFile() {
		return Activator.getDefault().getStateLocation().append(
				STRATEGIES_FILENAME).toFile();
	}

}
