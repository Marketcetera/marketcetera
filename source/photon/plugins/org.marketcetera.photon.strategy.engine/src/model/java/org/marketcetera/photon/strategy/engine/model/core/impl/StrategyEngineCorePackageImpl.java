/**
 * $License$
 *
 * $Id$
 */
package org.marketcetera.photon.strategy.engine.model.core.impl;

import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.marketcetera.module.ModuleURN;

import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineConnection;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCoreFactory;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage;
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;

import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class StrategyEngineCorePackageImpl extends EPackageImpl implements
        StrategyEngineCorePackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass strategyEngineEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass strategyEngineConnectionEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass strategyEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass deployedStrategyEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass stringToStringMapEntryEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum connectionStateEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum strategyStateEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType moduleURNEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType exceptionEDataType = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage#eNS_URI
     * @see #init()
     * @generated
     */
    private StrategyEngineCorePackageImpl() {
        super(eNS_URI, StrategyEngineCoreFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
     * 
     * <p>This method is used to initialize {@link StrategyEngineCorePackage#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static StrategyEngineCorePackage init() {
        if (isInited)
            return (StrategyEngineCorePackage) EPackage.Registry.INSTANCE
                    .getEPackage(StrategyEngineCorePackage.eNS_URI);

        // Obtain or create and register package
        StrategyEngineCorePackageImpl theStrategyEngineCorePackage = (StrategyEngineCorePackageImpl) (EPackage.Registry.INSTANCE
                .get(eNS_URI) instanceof StrategyEngineCorePackageImpl ? EPackage.Registry.INSTANCE
                .get(eNS_URI)
                : new StrategyEngineCorePackageImpl());

        isInited = true;

        // Create package meta-data objects
        theStrategyEngineCorePackage.createPackageContents();

        // Initialize created meta-data
        theStrategyEngineCorePackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theStrategyEngineCorePackage.freeze();

        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(StrategyEngineCorePackage.eNS_URI,
                theStrategyEngineCorePackage);
        return theStrategyEngineCorePackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getStrategyEngine() {
        return strategyEngineEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getStrategyEngine_Name() {
        return (EAttribute) strategyEngineEClass.getEStructuralFeatures()
                .get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getStrategyEngine_Description() {
        return (EAttribute) strategyEngineEClass.getEStructuralFeatures()
                .get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getStrategyEngine_ConnectionState() {
        return (EAttribute) strategyEngineEClass.getEStructuralFeatures()
                .get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getStrategyEngine_Connection() {
        return (EReference) strategyEngineEClass.getEStructuralFeatures()
                .get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getStrategyEngine_DeployedStrategies() {
        return (EReference) strategyEngineEClass.getEStructuralFeatures()
                .get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getStrategyEngine_ReadOnly() {
        return (EAttribute) strategyEngineEClass.getEStructuralFeatures()
                .get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getStrategyEngineConnection() {
        return strategyEngineConnectionEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getStrategyEngineConnection_Engine() {
        return (EReference) strategyEngineConnectionEClass
                .getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getStrategy() {
        return strategyEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getStrategy_InstanceName() {
        return (EAttribute) strategyEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getStrategy_Language() {
        return (EAttribute) strategyEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getStrategy_ScriptPath() {
        return (EAttribute) strategyEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getStrategy_ClassName() {
        return (EAttribute) strategyEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getStrategy_RouteOrdersToServer() {
        return (EAttribute) strategyEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getStrategy_Parameters() {
        return (EReference) strategyEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getDeployedStrategy() {
        return deployedStrategyEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getDeployedStrategy_Engine() {
        return (EReference) deployedStrategyEClass.getEStructuralFeatures()
                .get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getDeployedStrategy_State() {
        return (EAttribute) deployedStrategyEClass.getEStructuralFeatures()
                .get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getDeployedStrategy_Urn() {
        return (EAttribute) deployedStrategyEClass.getEStructuralFeatures()
                .get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getStringToStringMapEntry() {
        return stringToStringMapEntryEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getStringToStringMapEntry_Key() {
        return (EAttribute) stringToStringMapEntryEClass
                .getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getStringToStringMapEntry_Value() {
        return (EAttribute) stringToStringMapEntryEClass
                .getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getConnectionState() {
        return connectionStateEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getStrategyState() {
        return strategyStateEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getModuleURN() {
        return moduleURNEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getException() {
        return exceptionEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public StrategyEngineCoreFactory getStrategyEngineCoreFactory() {
        return (StrategyEngineCoreFactory) getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void createPackageContents() {
        if (isCreated)
            return;
        isCreated = true;

        // Create classes and their features
        strategyEngineEClass = createEClass(STRATEGY_ENGINE);
        createEAttribute(strategyEngineEClass, STRATEGY_ENGINE__NAME);
        createEAttribute(strategyEngineEClass, STRATEGY_ENGINE__DESCRIPTION);
        createEAttribute(strategyEngineEClass,
                STRATEGY_ENGINE__CONNECTION_STATE);
        createEReference(strategyEngineEClass, STRATEGY_ENGINE__CONNECTION);
        createEReference(strategyEngineEClass,
                STRATEGY_ENGINE__DEPLOYED_STRATEGIES);
        createEAttribute(strategyEngineEClass, STRATEGY_ENGINE__READ_ONLY);

        strategyEngineConnectionEClass = createEClass(STRATEGY_ENGINE_CONNECTION);
        createEReference(strategyEngineConnectionEClass,
                STRATEGY_ENGINE_CONNECTION__ENGINE);

        strategyEClass = createEClass(STRATEGY);
        createEAttribute(strategyEClass, STRATEGY__INSTANCE_NAME);
        createEAttribute(strategyEClass, STRATEGY__LANGUAGE);
        createEAttribute(strategyEClass, STRATEGY__SCRIPT_PATH);
        createEAttribute(strategyEClass, STRATEGY__CLASS_NAME);
        createEAttribute(strategyEClass, STRATEGY__ROUTE_ORDERS_TO_SERVER);
        createEReference(strategyEClass, STRATEGY__PARAMETERS);

        deployedStrategyEClass = createEClass(DEPLOYED_STRATEGY);
        createEReference(deployedStrategyEClass, DEPLOYED_STRATEGY__ENGINE);
        createEAttribute(deployedStrategyEClass, DEPLOYED_STRATEGY__STATE);
        createEAttribute(deployedStrategyEClass, DEPLOYED_STRATEGY__URN);

        stringToStringMapEntryEClass = createEClass(STRING_TO_STRING_MAP_ENTRY);
        createEAttribute(stringToStringMapEntryEClass,
                STRING_TO_STRING_MAP_ENTRY__KEY);
        createEAttribute(stringToStringMapEntryEClass,
                STRING_TO_STRING_MAP_ENTRY__VALUE);

        // Create enums
        connectionStateEEnum = createEEnum(CONNECTION_STATE);
        strategyStateEEnum = createEEnum(STRATEGY_STATE);

        // Create data types
        moduleURNEDataType = createEDataType(MODULE_URN);
        exceptionEDataType = createEDataType(EXCEPTION);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void initializePackageContents() {
        if (isInitialized)
            return;
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Create type parameters

        // Set bounds for type parameters

        // Add supertypes to classes
        deployedStrategyEClass.getESuperTypes().add(this.getStrategy());

        // Initialize classes and features; add operations and parameters
        initEClass(strategyEngineEClass, StrategyEngine.class,
                "StrategyEngine", !IS_ABSTRACT, !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getStrategyEngine_Name(), ecorePackage.getEString(),
                "name", null, 1, 1, StrategyEngine.class, !IS_TRANSIENT,
                !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
                !IS_DERIVED, IS_ORDERED);
        initEAttribute(getStrategyEngine_Description(), ecorePackage
                .getEString(), "description", null, 0, 1, StrategyEngine.class,
                !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
                !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getStrategyEngine_ConnectionState(), this
                .getConnectionState(), "connectionState", "", 1, 1,
                StrategyEngine.class, IS_TRANSIENT, !IS_VOLATILE,
                IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
                IS_ORDERED);
        initEReference(getStrategyEngine_Connection(), this
                .getStrategyEngineConnection(), this
                .getStrategyEngineConnection_Engine(), "connection", null, 0,
                1, StrategyEngine.class, IS_TRANSIENT, !IS_VOLATILE,
                IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
                !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getStrategyEngine_DeployedStrategies(), this
                .getDeployedStrategy(), this.getDeployedStrategy_Engine(),
                "deployedStrategies", null, 0, -1, StrategyEngine.class,
                IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
                IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
                IS_ORDERED);
        initEAttribute(getStrategyEngine_ReadOnly(),
                ecorePackage.getEBoolean(), "readOnly", null, 1, 1,
                StrategyEngine.class, !IS_TRANSIENT, !IS_VOLATILE,
                IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
                IS_ORDERED);

        initEClass(strategyEngineConnectionEClass,
                StrategyEngineConnection.class, "StrategyEngineConnection",
                IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getStrategyEngineConnection_Engine(), this
                .getStrategyEngine(), this.getStrategyEngine_Connection(),
                "engine", null, 1, 1, StrategyEngineConnection.class,
                IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
                IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
                IS_ORDERED);

        EOperation op = addEOperation(strategyEngineConnectionEClass, this
                .getDeployedStrategy(), "deploy", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, this.getStrategy(), "strategy", 1, 1, IS_UNIQUE,
                IS_ORDERED);
        addEException(op, this.getException());

        op = addEOperation(strategyEngineConnectionEClass, null, "undeploy", 0,
                1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, this.getDeployedStrategy(), "strategy", 1, 1,
                IS_UNIQUE, IS_ORDERED);
        addEException(op, this.getException());

        op = addEOperation(strategyEngineConnectionEClass, null, "start", 0, 1,
                IS_UNIQUE, IS_ORDERED);
        addEParameter(op, this.getDeployedStrategy(), "strategy", 1, 1,
                IS_UNIQUE, IS_ORDERED);
        addEException(op, this.getException());

        op = addEOperation(strategyEngineConnectionEClass, null, "stop", 0, 1,
                IS_UNIQUE, IS_ORDERED);
        addEParameter(op, this.getDeployedStrategy(), "strategy", 1, 1,
                IS_UNIQUE, IS_ORDERED);
        addEException(op, this.getException());

        op = addEOperation(strategyEngineConnectionEClass, null, "update", 0,
                1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, this.getDeployedStrategy(), "strategy", 1, 1,
                IS_UNIQUE, IS_ORDERED);
        addEParameter(op, this.getStrategy(), "newConfiguration", 0, 1,
                IS_UNIQUE, IS_ORDERED);
        addEException(op, this.getException());

        op = addEOperation(strategyEngineConnectionEClass, null, "refresh", 0,
                1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, this.getDeployedStrategy(), "strategy", 1, 1,
                IS_UNIQUE, IS_ORDERED);
        addEException(op, this.getException());

        op = addEOperation(strategyEngineConnectionEClass, null, "refresh", 0,
                1, IS_UNIQUE, IS_ORDERED);
        addEException(op, this.getException());

        initEClass(strategyEClass, Strategy.class, "Strategy", !IS_ABSTRACT,
                !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getStrategy_InstanceName(), ecorePackage.getEString(),
                "instanceName", null, 1, 1, Strategy.class, !IS_TRANSIENT,
                !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE,
                !IS_DERIVED, IS_ORDERED);
        initEAttribute(getStrategy_Language(), ecorePackage.getEString(),
                "language", null, 1, 1, Strategy.class, !IS_TRANSIENT,
                !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
                !IS_DERIVED, IS_ORDERED);
        initEAttribute(getStrategy_ScriptPath(), ecorePackage.getEString(),
                "scriptPath", null, 0, 1, Strategy.class, !IS_TRANSIENT,
                !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
                !IS_DERIVED, IS_ORDERED);
        initEAttribute(getStrategy_ClassName(), ecorePackage.getEString(),
                "className", null, 1, 1, Strategy.class, !IS_TRANSIENT,
                !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
                !IS_DERIVED, IS_ORDERED);
        initEAttribute(getStrategy_RouteOrdersToServer(), ecorePackage
                .getEBoolean(), "routeOrdersToServer", null, 1, 1,
                Strategy.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
                !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getStrategy_Parameters(), this
                .getStringToStringMapEntry(), null, "parameters", null, 0, -1,
                Strategy.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
                IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
                !IS_DERIVED, IS_ORDERED);

        initEClass(deployedStrategyEClass, DeployedStrategy.class,
                "DeployedStrategy", !IS_ABSTRACT, !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);
        initEReference(getDeployedStrategy_Engine(), this.getStrategyEngine(),
                this.getStrategyEngine_DeployedStrategies(), "engine", null, 0,
                1, DeployedStrategy.class, IS_TRANSIENT, !IS_VOLATILE,
                IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
                !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getDeployedStrategy_State(), this.getStrategyState(),
                "state", "Stopped", 1, 1, DeployedStrategy.class,
                !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
                !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getDeployedStrategy_Urn(), this.getModuleURN(), "urn",
                null, 0, 1, DeployedStrategy.class, IS_TRANSIENT, !IS_VOLATILE,
                IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
                IS_ORDERED);

        initEClass(stringToStringMapEntryEClass, Map.Entry.class,
                "StringToStringMapEntry", !IS_ABSTRACT, !IS_INTERFACE,
                !IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getStringToStringMapEntry_Key(), ecorePackage
                .getEString(), "key", null, 1, 1, Map.Entry.class,
                !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
                !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getStringToStringMapEntry_Value(), ecorePackage
                .getEString(), "value", null, 0, 1, Map.Entry.class,
                !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
                !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        // Initialize enums and add enum literals
        initEEnum(connectionStateEEnum, ConnectionState.class,
                "ConnectionState");
        addEEnumLiteral(connectionStateEEnum, ConnectionState.DISCONNECTED);
        addEEnumLiteral(connectionStateEEnum, ConnectionState.CONNECTED);

        initEEnum(strategyStateEEnum, StrategyState.class, "StrategyState");
        addEEnumLiteral(strategyStateEEnum, StrategyState.STOPPED);
        addEEnumLiteral(strategyStateEEnum, StrategyState.RUNNING);

        // Initialize data types
        initEDataType(moduleURNEDataType, ModuleURN.class, "ModuleURN",
                IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
        initEDataType(exceptionEDataType, Exception.class, "Exception",
                IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

        // Create resource
        createResource(eNS_URI);
    }

} //StrategyEngineCorePackageImpl
