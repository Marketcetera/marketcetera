/**
 * $License$
 *
 * $Id$
 */
package org.marketcetera.photon.strategy.engine.model.sa;

import org.eclipse.emf.ecore.EFactory;

import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEnginePackage
 * @generated
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public interface StrategyAgentEngineFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    StrategyAgentEngineFactory eINSTANCE = org.marketcetera.photon.strategy.engine.model.sa.impl.StrategyAgentEngineFactoryImpl
            .init();

    /**
     * Returns a new object of class '<em>Strategy Agent Engine</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Strategy Agent Engine</em>'.
     * @generated
     */
    StrategyAgentEngine createStrategyAgentEngine();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    StrategyAgentEnginePackage getStrategyAgentEnginePackage();

} //StrategyAgentEngineFactory
