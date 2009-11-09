/**
 * $License$
 *
 * $Id$
 */
package org.marketcetera.photon.strategy.engine.model.sa.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage;

import org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine;
import org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngineFactory;
import org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEnginePackage;

import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 * @since $Release$
 */
@ClassVersion("$Id$")
public class StrategyAgentEnginePackageImpl extends EPackageImpl implements
        StrategyAgentEnginePackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass strategyAgentEngineEClass = null;

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
     * @see org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEnginePackage#eNS_URI
     * @see #init()
     * @generated
     */
    private StrategyAgentEnginePackageImpl() {
        super(eNS_URI, StrategyAgentEngineFactory.eINSTANCE);
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
     * <p>This method is used to initialize {@link StrategyAgentEnginePackage#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static StrategyAgentEnginePackage init() {
        if (isInited)
            return (StrategyAgentEnginePackage) EPackage.Registry.INSTANCE
                    .getEPackage(StrategyAgentEnginePackage.eNS_URI);

        // Obtain or create and register package
        StrategyAgentEnginePackageImpl theStrategyAgentEnginePackage = (StrategyAgentEnginePackageImpl) (EPackage.Registry.INSTANCE
                .get(eNS_URI) instanceof StrategyAgentEnginePackageImpl ? EPackage.Registry.INSTANCE
                .get(eNS_URI)
                : new StrategyAgentEnginePackageImpl());

        isInited = true;

        // Initialize simple dependencies
        StrategyEngineCorePackage.eINSTANCE.eClass();

        // Create package meta-data objects
        theStrategyAgentEnginePackage.createPackageContents();

        // Initialize created meta-data
        theStrategyAgentEnginePackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theStrategyAgentEnginePackage.freeze();

        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(StrategyAgentEnginePackage.eNS_URI,
                theStrategyAgentEnginePackage);
        return theStrategyAgentEnginePackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getStrategyAgentEngine() {
        return strategyAgentEngineEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getStrategyAgentEngine_JmsUrl() {
        return (EAttribute) strategyAgentEngineEClass.getEStructuralFeatures()
                .get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getStrategyAgentEngine_WebServiceHostname() {
        return (EAttribute) strategyAgentEngineEClass.getEStructuralFeatures()
                .get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getStrategyAgentEngine_WebServicePort() {
        return (EAttribute) strategyAgentEngineEClass.getEStructuralFeatures()
                .get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public StrategyAgentEngineFactory getStrategyAgentEngineFactory() {
        return (StrategyAgentEngineFactory) getEFactoryInstance();
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
        strategyAgentEngineEClass = createEClass(STRATEGY_AGENT_ENGINE);
        createEAttribute(strategyAgentEngineEClass,
                STRATEGY_AGENT_ENGINE__JMS_URL);
        createEAttribute(strategyAgentEngineEClass,
                STRATEGY_AGENT_ENGINE__WEB_SERVICE_HOSTNAME);
        createEAttribute(strategyAgentEngineEClass,
                STRATEGY_AGENT_ENGINE__WEB_SERVICE_PORT);
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

        // Obtain other dependent packages
        StrategyEngineCorePackage theStrategyEngineCorePackage = (StrategyEngineCorePackage) EPackage.Registry.INSTANCE
                .getEPackage(StrategyEngineCorePackage.eNS_URI);

        // Create type parameters

        // Set bounds for type parameters

        // Add supertypes to classes
        strategyAgentEngineEClass.getESuperTypes().add(
                theStrategyEngineCorePackage.getStrategyEngine());

        // Initialize classes and features; add operations and parameters
        initEClass(strategyAgentEngineEClass, StrategyAgentEngine.class,
                "StrategyAgentEngine", !IS_ABSTRACT, !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getStrategyAgentEngine_JmsUrl(), ecorePackage
                .getEString(), "jmsUrl", null, 1, 1, StrategyAgentEngine.class,
                !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
                !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getStrategyAgentEngine_WebServiceHostname(),
                ecorePackage.getEString(), "webServiceHostname", null, 1, 1,
                StrategyAgentEngine.class, !IS_TRANSIENT, !IS_VOLATILE,
                IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
                IS_ORDERED);
        initEAttribute(getStrategyAgentEngine_WebServicePort(), ecorePackage
                .getEIntegerObject(), "webServicePort", null, 1, 1,
                StrategyAgentEngine.class, !IS_TRANSIENT, !IS_VOLATILE,
                IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
                IS_ORDERED);

        EOperation op = addEOperation(strategyAgentEngineEClass, null,
                "connect", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEException(op, theStrategyEngineCorePackage.getException());

        op = addEOperation(strategyAgentEngineEClass, null, "disconnect", 0, 1,
                IS_UNIQUE, IS_ORDERED);
        addEException(op, theStrategyEngineCorePackage.getException());

        // Create resource
        createResource(eNS_URI);
    }

} //StrategyAgentEnginePackageImpl
