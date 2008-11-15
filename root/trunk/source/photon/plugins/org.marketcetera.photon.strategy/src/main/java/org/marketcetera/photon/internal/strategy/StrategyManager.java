package org.marketcetera.photon.internal.strategy;

import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.resources.IFile;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.internal.strategy.Strategy.State;
import org.marketcetera.photon.module.ModulePlugin;
import org.marketcetera.strategy.Language;
import org.marketcetera.strategy.StrategyModuleFactory;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Manages a collection of {@link Strategy} objects and interfaces with the
 * underlying Module Framework.
 * 
 * This class manages an {@link WritableList} of {@link Strategy} objects, and
 * as such, it is thread safe.  An exception will be thrown if it is accessed from 
 * any thread other than the one that created it. 
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

	private final ModuleManager mModuleManager = ModulePlugin.getDefault()
			.getModuleManager();

	private final WritableList mStrategies = WritableList
			.withElementType(Strategy.class);

	/**
	 * This object should only be constructed by {@link Activator}.
	 */
	StrategyManager() {
	}

	/**
	 * Returns the collection of register strategies.
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
			// TODO: report to user
			e.getI18NBoundMessage().error(this);
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
	 * Changes the human readable name of the given strategy.
	 * 
	 * @param strategy
	 *            strategy to be changed
	 * @param name
	 *            the new human readable name
	 */
	public void changeDisplayName(Strategy strategy, String name) {
		strategy.setDisplayName(name);
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
	 */
	public void registerStrategy(IFile file, String className,
			String displayName) {
		try {
			ModuleURN urn = mModuleManager.createModule(
					StrategyModuleFactory.PROVIDER_URN, className,
					Language.RUBY, file.getLocation().toFile(), null, null,
					null, null);
			Strategy strategy = new Strategy(urn, file, className);
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
		} catch (ModuleException e) {
			// TODO: report to user
			e.getI18NBoundMessage().error(this);
		}
	}

}
