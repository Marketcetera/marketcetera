/**
 * $License$
 *
 * $Id$
 */
package org.marketcetera.photon.strategy.engine.model.core;

import org.eclipse.emf.ecore.EFactory;

import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage
 * @generated
 * @since 2.1.0
 */
@ClassVersion("$Id$")
public interface StrategyEngineCoreFactory
        extends EFactory
{
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    StrategyEngineCoreFactory eINSTANCE = org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineCoreFactoryImpl
            .init();

    /**
     * Returns a new object of class '<em>Strategy Engine</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Strategy Engine</em>'.
     * @generated
     */
    StrategyEngine createStrategyEngine();

    /**
     * Returns a new object of class '<em>Strategy</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Strategy</em>'.
     * @generated
     */
    Strategy createStrategy();

    /**
     * Returns a new object of class '<em>Deployed Strategy</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Deployed Strategy</em>'.
     * @generated
     */
    DeployedStrategy createDeployedStrategy();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    StrategyEngineCorePackage getStrategyEngineCorePackage();

} //StrategyEngineCoreFactory
