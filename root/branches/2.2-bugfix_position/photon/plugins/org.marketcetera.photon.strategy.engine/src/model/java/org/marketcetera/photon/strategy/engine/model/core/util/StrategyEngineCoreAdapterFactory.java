/**
 * $License$
 *
 * $Id$
 */
package org.marketcetera.photon.strategy.engine.model.core.util;

import java.util.Map;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.marketcetera.photon.strategy.engine.model.core.*;

import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage
 * @generated
 * @since 2.1.0
 */
@ClassVersion("$Id$")
public class StrategyEngineCoreAdapterFactory
        extends AdapterFactoryImpl
{
    /**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static StrategyEngineCorePackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public StrategyEngineCoreAdapterFactory()
    {
        if (modelPackage == null) {
            modelPackage = StrategyEngineCorePackage.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc -->
     * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
     * <!-- end-user-doc -->
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
    @Override
    public boolean isFactoryForType(Object object)
    {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject) object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

    /**
     * The switch that delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected StrategyEngineCoreSwitch<Adapter> modelSwitch = new StrategyEngineCoreSwitch<Adapter>() {
        @Override
        public Adapter caseStrategyEngine(StrategyEngine object)
        {
            return createStrategyEngineAdapter();
        }

        @Override
        public Adapter caseStrategyEngineConnection(StrategyEngineConnection object)
        {
            return createStrategyEngineConnectionAdapter();
        }

        @Override
        public Adapter caseStrategy(Strategy object)
        {
            return createStrategyAdapter();
        }

        @Override
        public Adapter caseDeployedStrategy(DeployedStrategy object)
        {
            return createDeployedStrategyAdapter();
        }

        @Override
        public Adapter caseStringToStringMapEntry(Map.Entry<String, String> object)
        {
            return createStringToStringMapEntryAdapter();
        }

        @Override
        public Adapter defaultCase(EObject object)
        {
            return createEObjectAdapter();
        }
    };

    /**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    @Override
    public Adapter createAdapter(Notifier target)
    {
        return modelSwitch.doSwitch((EObject) target);
    }

    /**
     * Creates a new adapter for an object of class '{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngine <em>Strategy Engine</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngine
     * @generated
     */
    public Adapter createStrategyEngineAdapter()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngineConnection <em>Strategy Engine Connection</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineConnection
     * @generated
     */
    public Adapter createStrategyEngineConnectionAdapter()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.marketcetera.photon.strategy.engine.model.core.Strategy <em>Strategy</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.marketcetera.photon.strategy.engine.model.core.Strategy
     * @generated
     */
    public Adapter createStrategyAdapter()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy <em>Deployed Strategy</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy
     * @generated
     */
    public Adapter createDeployedStrategyAdapter()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>String To String Map Entry</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see java.util.Map.Entry
     * @generated
     */
    public Adapter createStringToStringMapEntryAdapter()
    {
        return null;
    }

    /**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc -->
     * This default implementation returns null.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @generated
     */
    public Adapter createEObjectAdapter()
    {
        return null;
    }

} //StrategyEngineCoreAdapterFactory
