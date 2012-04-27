/**
 * $License$
 *
 * $Id$
 */
package org.marketcetera.photon.strategy.engine.model.core.util;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.marketcetera.photon.strategy.engine.model.core.*;

import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage
 * @generated
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class StrategyEngineCoreSwitch<T> {
    /**
     * The cached model package
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static StrategyEngineCorePackage modelPackage;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public StrategyEngineCoreSwitch() {
        if (modelPackage == null) {
            modelPackage = StrategyEngineCorePackage.eINSTANCE;
        }
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    public T doSwitch(EObject theEObject) {
        return doSwitch(theEObject.eClass(), theEObject);
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    protected T doSwitch(EClass theEClass, EObject theEObject) {
        if (theEClass.eContainer() == modelPackage) {
            return doSwitch(theEClass.getClassifierID(), theEObject);
        } else {
            List<EClass> eSuperTypes = theEClass.getESuperTypes();
            return eSuperTypes.isEmpty() ? defaultCase(theEObject) : doSwitch(
                    eSuperTypes.get(0), theEObject);
        }
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    protected T doSwitch(int classifierID, EObject theEObject) {
        switch (classifierID) {
        case StrategyEngineCorePackage.STRATEGY_ENGINE: {
            StrategyEngine strategyEngine = (StrategyEngine) theEObject;
            T result = caseStrategyEngine(strategyEngine);
            if (result == null)
                result = defaultCase(theEObject);
            return result;
        }
        case StrategyEngineCorePackage.STRATEGY_ENGINE_CONNECTION: {
            StrategyEngineConnection strategyEngineConnection = (StrategyEngineConnection) theEObject;
            T result = caseStrategyEngineConnection(strategyEngineConnection);
            if (result == null)
                result = defaultCase(theEObject);
            return result;
        }
        case StrategyEngineCorePackage.STRATEGY: {
            Strategy strategy = (Strategy) theEObject;
            T result = caseStrategy(strategy);
            if (result == null)
                result = defaultCase(theEObject);
            return result;
        }
        case StrategyEngineCorePackage.DEPLOYED_STRATEGY: {
            DeployedStrategy deployedStrategy = (DeployedStrategy) theEObject;
            T result = caseDeployedStrategy(deployedStrategy);
            if (result == null)
                result = caseStrategy(deployedStrategy);
            if (result == null)
                result = defaultCase(theEObject);
            return result;
        }
        case StrategyEngineCorePackage.STRING_TO_STRING_MAP_ENTRY: {
            @SuppressWarnings("unchecked")
            Map.Entry<String, String> stringToStringMapEntry = (Map.Entry<String, String>) theEObject;
            T result = caseStringToStringMapEntry(stringToStringMapEntry);
            if (result == null)
                result = defaultCase(theEObject);
            return result;
        }
        default:
            return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Strategy Engine</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Strategy Engine</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseStrategyEngine(StrategyEngine object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Strategy Engine Connection</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Strategy Engine Connection</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseStrategyEngineConnection(StrategyEngineConnection object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Strategy</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Strategy</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseStrategy(Strategy object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Deployed Strategy</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Deployed Strategy</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseDeployedStrategy(DeployedStrategy object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>String To String Map Entry</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>String To String Map Entry</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseStringToStringMapEntry(Map.Entry<String, String> object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch, but this is the last case anyway.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject)
     * @generated
     */
    public T defaultCase(EObject object) {
        return null;
    }

} //StrategyEngineCoreSwitch
