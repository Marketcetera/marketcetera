/**
 * $License$
 *
 * $Id$
 */
package org.marketcetera.photon.strategy.engine.model.core.impl;

import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.marketcetera.module.ModuleURN;

import org.marketcetera.photon.strategy.engine.model.core.*;

import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 * @since 2.1.0
 */
@ClassVersion("$Id$")
public class StrategyEngineCoreFactoryImpl
        extends EFactoryImpl
        implements StrategyEngineCoreFactory
{
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static StrategyEngineCoreFactory init()
    {
        try {
            StrategyEngineCoreFactory theStrategyEngineCoreFactory = (StrategyEngineCoreFactory) EPackage.Registry.INSTANCE
                    .getEFactory("http://www.marketcetera.org/photon/strategy/engine/core/1.0");
            if (theStrategyEngineCoreFactory != null) {
                return theStrategyEngineCoreFactory;
            }
        } catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new StrategyEngineCoreFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public StrategyEngineCoreFactoryImpl()
    {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EObject create(EClass eClass)
    {
        switch (eClass.getClassifierID()) {
            case StrategyEngineCorePackage.STRATEGY_ENGINE:
                return createStrategyEngine();
            case StrategyEngineCorePackage.STRATEGY:
                return createStrategy();
            case StrategyEngineCorePackage.DEPLOYED_STRATEGY:
                return createDeployedStrategy();
            case StrategyEngineCorePackage.STRING_TO_STRING_MAP_ENTRY:
                return (EObject) createStringToStringMapEntry();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object createFromString(EDataType eDataType,
                                   String initialValue)
    {
        switch (eDataType.getClassifierID()) {
            case StrategyEngineCorePackage.CONNECTION_STATE:
                return createConnectionStateFromString(eDataType,
                                                       initialValue);
            case StrategyEngineCorePackage.STRATEGY_STATE:
                return createStrategyStateFromString(eDataType,
                                                     initialValue);
            case StrategyEngineCorePackage.MODULE_URN:
                return createModuleURNFromString(eDataType,
                                                 initialValue);
            case StrategyEngineCorePackage.EXCEPTION:
                return createExceptionFromString(eDataType,
                                                 initialValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName()
                        + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String convertToString(EDataType eDataType,
                                  Object instanceValue)
    {
        switch (eDataType.getClassifierID()) {
            case StrategyEngineCorePackage.CONNECTION_STATE:
                return convertConnectionStateToString(eDataType,
                                                      instanceValue);
            case StrategyEngineCorePackage.STRATEGY_STATE:
                return convertStrategyStateToString(eDataType,
                                                    instanceValue);
            case StrategyEngineCorePackage.MODULE_URN:
                return convertModuleURNToString(eDataType,
                                                instanceValue);
            case StrategyEngineCorePackage.EXCEPTION:
                return convertExceptionToString(eDataType,
                                                instanceValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName()
                        + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public StrategyEngine createStrategyEngine()
    {
        StrategyEngineImpl strategyEngine = new StrategyEngineImpl();
        return strategyEngine;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Strategy createStrategy()
    {
        StrategyImpl strategy = new StrategyImpl();
        return strategy;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DeployedStrategy createDeployedStrategy()
    {
        DeployedStrategyImpl deployedStrategy = new DeployedStrategyImpl();
        return deployedStrategy;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Map.Entry<String, String> createStringToStringMapEntry()
    {
        StringToStringMapEntryImpl stringToStringMapEntry = new StringToStringMapEntryImpl();
        return stringToStringMapEntry;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ConnectionState createConnectionStateFromString(EDataType eDataType,
                                                           String initialValue)
    {
        ConnectionState result = ConnectionState.get(initialValue);
        if (result == null)
            throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '"
                    + eDataType.getName() + "'");
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertConnectionStateToString(EDataType eDataType,
                                                 Object instanceValue)
    {
        return instanceValue == null ? null : instanceValue.toString();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public StrategyState createStrategyStateFromString(EDataType eDataType,
                                                       String initialValue)
    {
        StrategyState result = StrategyState.get(initialValue);
        if (result == null)
            throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '"
                    + eDataType.getName() + "'");
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertStrategyStateToString(EDataType eDataType,
                                               Object instanceValue)
    {
        return instanceValue == null ? null : instanceValue.toString();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModuleURN createModuleURNFromString(EDataType eDataType,
                                               String initialValue)
    {
        return (ModuleURN) super.createFromString(eDataType,
                                                  initialValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertModuleURNToString(EDataType eDataType,
                                           Object instanceValue)
    {
        return super.convertToString(eDataType,
                                     instanceValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Exception createExceptionFromString(EDataType eDataType,
                                               String initialValue)
    {
        return (Exception) super.createFromString(eDataType,
                                                  initialValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertExceptionToString(EDataType eDataType,
                                           Object instanceValue)
    {
        return super.convertToString(eDataType,
                                     instanceValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public StrategyEngineCorePackage getStrategyEngineCorePackage()
    {
        return (StrategyEngineCorePackage) getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static StrategyEngineCorePackage getPackage()
    {
        return StrategyEngineCorePackage.eINSTANCE;
    }

} //StrategyEngineCoreFactoryImpl
