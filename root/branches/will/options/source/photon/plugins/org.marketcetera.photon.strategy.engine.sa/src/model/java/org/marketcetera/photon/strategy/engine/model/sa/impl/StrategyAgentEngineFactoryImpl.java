/**
 * $License$
 *
 * $Id$
 */
package org.marketcetera.photon.strategy.engine.model.sa.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.marketcetera.photon.strategy.engine.model.sa.*;

import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 * @since $Release$
 */
@ClassVersion("$Id$")
public class StrategyAgentEngineFactoryImpl extends EFactoryImpl implements
        StrategyAgentEngineFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static StrategyAgentEngineFactory init() {
        try {
            StrategyAgentEngineFactory theStrategyAgentEngineFactory = (StrategyAgentEngineFactory) EPackage.Registry.INSTANCE
                    .getEFactory("http://www.marketcetera.org/photon/strategy/engine/strategyagent/1.0");
            if (theStrategyAgentEngineFactory != null) {
                return theStrategyAgentEngineFactory;
            }
        } catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new StrategyAgentEngineFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public StrategyAgentEngineFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
        case StrategyAgentEnginePackage.STRATEGY_AGENT_ENGINE:
            return createStrategyAgentEngine();
        default:
            throw new IllegalArgumentException("The class '" + eClass.getName()
                    + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public StrategyAgentEngine createStrategyAgentEngine() {
        StrategyAgentEngineImpl strategyAgentEngine = new StrategyAgentEngineImpl();
        return strategyAgentEngine;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public StrategyAgentEnginePackage getStrategyAgentEnginePackage() {
        return (StrategyAgentEnginePackage) getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static StrategyAgentEnginePackage getPackage() {
        return StrategyAgentEnginePackage.eINSTANCE;
    }

} //StrategyAgentEngineFactoryImpl
