/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.marketcetera.photon.strategy.engine.model.core;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCoreFactory
 * @model kind="package"
 * @generated
 */
public interface StrategyEngineCorePackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "core";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.marketcetera.org/photon/strategy/engine/core/1.0";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "core";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    StrategyEngineCorePackage eINSTANCE = org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineCorePackageImpl
            .init();

    /**
     * The meta object id for the '{@link org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineImpl <em>Strategy Engine</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineImpl
     * @see org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineCorePackageImpl#getStrategyEngine()
     * @generated
     */
    int STRATEGY_ENGINE = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY_ENGINE__NAME = 0;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY_ENGINE__DESCRIPTION = 1;

    /**
     * The feature id for the '<em><b>Connection State</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY_ENGINE__CONNECTION_STATE = 2;

    /**
     * The feature id for the '<em><b>Connection</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY_ENGINE__CONNECTION = 3;

    /**
     * The feature id for the '<em><b>Deployed Strategies</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY_ENGINE__DEPLOYED_STRATEGIES = 4;

    /**
     * The feature id for the '<em><b>Read Only</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY_ENGINE__READ_ONLY = 5;

    /**
     * The number of structural features of the '<em>Strategy Engine</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY_ENGINE_FEATURE_COUNT = 6;

    /**
     * The meta object id for the '{@link org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineConnectionImpl <em>Strategy Engine Connection</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineConnectionImpl
     * @see org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineCorePackageImpl#getStrategyEngineConnection()
     * @generated
     */
    int STRATEGY_ENGINE_CONNECTION = 1;

    /**
     * The feature id for the '<em><b>Engine</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY_ENGINE_CONNECTION__ENGINE = 0;

    /**
     * The number of structural features of the '<em>Strategy Engine Connection</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY_ENGINE_CONNECTION_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link org.marketcetera.photon.strategy.engine.model.core.impl.StrategyImpl <em>Strategy</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.marketcetera.photon.strategy.engine.model.core.impl.StrategyImpl
     * @see org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineCorePackageImpl#getStrategy()
     * @generated
     */
    int STRATEGY = 2;

    /**
     * The feature id for the '<em><b>Instance Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY__INSTANCE_NAME = 0;

    /**
     * The feature id for the '<em><b>Language</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY__LANGUAGE = 1;

    /**
     * The feature id for the '<em><b>Script Path</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY__SCRIPT_PATH = 2;

    /**
     * The feature id for the '<em><b>Class Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY__CLASS_NAME = 3;

    /**
     * The feature id for the '<em><b>Route Orders To Server</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY__ROUTE_ORDERS_TO_SERVER = 4;

    /**
     * The feature id for the '<em><b>Parameters</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY__PARAMETERS = 5;

    /**
     * The number of structural features of the '<em>Strategy</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY_FEATURE_COUNT = 6;

    /**
     * The meta object id for the '{@link org.marketcetera.photon.strategy.engine.model.core.impl.DeployedStrategyImpl <em>Deployed Strategy</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.marketcetera.photon.strategy.engine.model.core.impl.DeployedStrategyImpl
     * @see org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineCorePackageImpl#getDeployedStrategy()
     * @generated
     */
    int DEPLOYED_STRATEGY = 3;

    /**
     * The feature id for the '<em><b>Instance Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEPLOYED_STRATEGY__INSTANCE_NAME = STRATEGY__INSTANCE_NAME;

    /**
     * The feature id for the '<em><b>Language</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEPLOYED_STRATEGY__LANGUAGE = STRATEGY__LANGUAGE;

    /**
     * The feature id for the '<em><b>Script Path</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEPLOYED_STRATEGY__SCRIPT_PATH = STRATEGY__SCRIPT_PATH;

    /**
     * The feature id for the '<em><b>Class Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEPLOYED_STRATEGY__CLASS_NAME = STRATEGY__CLASS_NAME;

    /**
     * The feature id for the '<em><b>Route Orders To Server</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEPLOYED_STRATEGY__ROUTE_ORDERS_TO_SERVER = STRATEGY__ROUTE_ORDERS_TO_SERVER;

    /**
     * The feature id for the '<em><b>Parameters</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEPLOYED_STRATEGY__PARAMETERS = STRATEGY__PARAMETERS;

    /**
     * The feature id for the '<em><b>Engine</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEPLOYED_STRATEGY__ENGINE = STRATEGY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>State</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEPLOYED_STRATEGY__STATE = STRATEGY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Urn</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEPLOYED_STRATEGY__URN = STRATEGY_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the '<em>Deployed Strategy</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEPLOYED_STRATEGY_FEATURE_COUNT = STRATEGY_FEATURE_COUNT + 3;

    /**
     * The meta object id for the '{@link org.marketcetera.photon.strategy.engine.model.core.impl.StringToStringMapEntryImpl <em>String To String Map Entry</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.marketcetera.photon.strategy.engine.model.core.impl.StringToStringMapEntryImpl
     * @see org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineCorePackageImpl#getStringToStringMapEntry()
     * @generated
     */
    int STRING_TO_STRING_MAP_ENTRY = 4;

    /**
     * The feature id for the '<em><b>Key</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRING_TO_STRING_MAP_ENTRY__KEY = 0;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRING_TO_STRING_MAP_ENTRY__VALUE = 1;

    /**
     * The number of structural features of the '<em>String To String Map Entry</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRING_TO_STRING_MAP_ENTRY_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link org.marketcetera.photon.strategy.engine.model.core.ConnectionState <em>Connection State</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.marketcetera.photon.strategy.engine.model.core.ConnectionState
     * @see org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineCorePackageImpl#getConnectionState()
     * @generated
     */
    int CONNECTION_STATE = 5;

    /**
     * The meta object id for the '{@link org.marketcetera.photon.strategy.engine.model.core.StrategyState <em>Strategy State</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyState
     * @see org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineCorePackageImpl#getStrategyState()
     * @generated
     */
    int STRATEGY_STATE = 6;

    /**
     * The meta object id for the '<em>Module URN</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.marketcetera.module.ModuleURN
     * @see org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineCorePackageImpl#getModuleURN()
     * @generated
     */
    int MODULE_URN = 7;

    /**
     * The meta object id for the '<em>Exception</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.lang.Exception
     * @see org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineCorePackageImpl#getException()
     * @generated
     */
    int EXCEPTION = 8;

    /**
     * Returns the meta object for class '{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngine <em>Strategy Engine</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Strategy Engine</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngine
     * @generated
     */
    EClass getStrategyEngine();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#getName()
     * @see #getStrategyEngine()
     * @generated
     */
    EAttribute getStrategyEngine_Name();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#getDescription <em>Description</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Description</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#getDescription()
     * @see #getStrategyEngine()
     * @generated
     */
    EAttribute getStrategyEngine_Description();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#getConnectionState <em>Connection State</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Connection State</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#getConnectionState()
     * @see #getStrategyEngine()
     * @generated
     */
    EAttribute getStrategyEngine_ConnectionState();

    /**
     * Returns the meta object for the reference '{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#getConnection <em>Connection</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Connection</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#getConnection()
     * @see #getStrategyEngine()
     * @generated
     */
    EReference getStrategyEngine_Connection();

    /**
     * Returns the meta object for the reference list '{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#getDeployedStrategies <em>Deployed Strategies</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Deployed Strategies</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#getDeployedStrategies()
     * @see #getStrategyEngine()
     * @generated
     */
    EReference getStrategyEngine_DeployedStrategies();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#isReadOnly <em>Read Only</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Read Only</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#isReadOnly()
     * @see #getStrategyEngine()
     * @generated
     */
    EAttribute getStrategyEngine_ReadOnly();

    /**
     * Returns the meta object for class '{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngineConnection <em>Strategy Engine Connection</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Strategy Engine Connection</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineConnection
     * @generated
     */
    EClass getStrategyEngineConnection();

    /**
     * Returns the meta object for the reference '{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngineConnection#getEngine <em>Engine</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Engine</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineConnection#getEngine()
     * @see #getStrategyEngineConnection()
     * @generated
     */
    EReference getStrategyEngineConnection_Engine();

    /**
     * Returns the meta object for class '{@link org.marketcetera.photon.strategy.engine.model.core.Strategy <em>Strategy</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Strategy</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.core.Strategy
     * @generated
     */
    EClass getStrategy();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.strategy.engine.model.core.Strategy#getInstanceName <em>Instance Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Instance Name</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.core.Strategy#getInstanceName()
     * @see #getStrategy()
     * @generated
     */
    EAttribute getStrategy_InstanceName();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.strategy.engine.model.core.Strategy#getLanguage <em>Language</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Language</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.core.Strategy#getLanguage()
     * @see #getStrategy()
     * @generated
     */
    EAttribute getStrategy_Language();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.strategy.engine.model.core.Strategy#getScriptPath <em>Script Path</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Script Path</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.core.Strategy#getScriptPath()
     * @see #getStrategy()
     * @generated
     */
    EAttribute getStrategy_ScriptPath();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.strategy.engine.model.core.Strategy#getClassName <em>Class Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Class Name</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.core.Strategy#getClassName()
     * @see #getStrategy()
     * @generated
     */
    EAttribute getStrategy_ClassName();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.strategy.engine.model.core.Strategy#isRouteOrdersToServer <em>Route Orders To Server</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Route Orders To Server</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.core.Strategy#isRouteOrdersToServer()
     * @see #getStrategy()
     * @generated
     */
    EAttribute getStrategy_RouteOrdersToServer();

    /**
     * Returns the meta object for the map '{@link org.marketcetera.photon.strategy.engine.model.core.Strategy#getParameters <em>Parameters</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the map '<em>Parameters</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.core.Strategy#getParameters()
     * @see #getStrategy()
     * @generated
     */
    EReference getStrategy_Parameters();

    /**
     * Returns the meta object for class '{@link org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy <em>Deployed Strategy</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Deployed Strategy</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy
     * @generated
     */
    EClass getDeployedStrategy();

    /**
     * Returns the meta object for the reference '{@link org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy#getEngine <em>Engine</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Engine</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy#getEngine()
     * @see #getDeployedStrategy()
     * @generated
     */
    EReference getDeployedStrategy_Engine();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy#getState <em>State</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>State</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy#getState()
     * @see #getDeployedStrategy()
     * @generated
     */
    EAttribute getDeployedStrategy_State();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy#getUrn <em>Urn</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Urn</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy#getUrn()
     * @see #getDeployedStrategy()
     * @generated
     */
    EAttribute getDeployedStrategy_Urn();

    /**
     * Returns the meta object for class '{@link java.util.Map.Entry <em>String To String Map Entry</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>String To String Map Entry</em>'.
     * @see java.util.Map.Entry
     * @model keyDataType="org.eclipse.emf.ecore.EString" keyRequired="true"
     *        valueDataType="org.eclipse.emf.ecore.EString"
     * @generated
     */
    EClass getStringToStringMapEntry();

    /**
     * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Key</em>'.
     * @see java.util.Map.Entry
     * @see #getStringToStringMapEntry()
     * @generated
     */
    EAttribute getStringToStringMapEntry_Key();

    /**
     * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Value</em>'.
     * @see java.util.Map.Entry
     * @see #getStringToStringMapEntry()
     * @generated
     */
    EAttribute getStringToStringMapEntry_Value();

    /**
     * Returns the meta object for enum '{@link org.marketcetera.photon.strategy.engine.model.core.ConnectionState <em>Connection State</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Connection State</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.core.ConnectionState
     * @generated
     */
    EEnum getConnectionState();

    /**
     * Returns the meta object for enum '{@link org.marketcetera.photon.strategy.engine.model.core.StrategyState <em>Strategy State</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Strategy State</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyState
     * @generated
     */
    EEnum getStrategyState();

    /**
     * Returns the meta object for data type '{@link org.marketcetera.module.ModuleURN <em>Module URN</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Module URN</em>'.
     * @see org.marketcetera.module.ModuleURN
     * @model instanceClass="org.marketcetera.module.ModuleURN"
     * @generated
     */
    EDataType getModuleURN();

    /**
     * Returns the meta object for data type '{@link java.lang.Exception <em>Exception</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Exception</em>'.
     * @see java.lang.Exception
     * @model instanceClass="java.lang.Exception"
     * @generated
     */
    EDataType getException();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    StrategyEngineCoreFactory getStrategyEngineCoreFactory();

    /**
     * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals {
        /**
         * The meta object literal for the '{@link org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineImpl <em>Strategy Engine</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineImpl
         * @see org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineCorePackageImpl#getStrategyEngine()
         * @generated
         */
        EClass STRATEGY_ENGINE = eINSTANCE.getStrategyEngine();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STRATEGY_ENGINE__NAME = eINSTANCE.getStrategyEngine_Name();

        /**
         * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STRATEGY_ENGINE__DESCRIPTION = eINSTANCE
                .getStrategyEngine_Description();

        /**
         * The meta object literal for the '<em><b>Connection State</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STRATEGY_ENGINE__CONNECTION_STATE = eINSTANCE
                .getStrategyEngine_ConnectionState();

        /**
         * The meta object literal for the '<em><b>Connection</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference STRATEGY_ENGINE__CONNECTION = eINSTANCE
                .getStrategyEngine_Connection();

        /**
         * The meta object literal for the '<em><b>Deployed Strategies</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference STRATEGY_ENGINE__DEPLOYED_STRATEGIES = eINSTANCE
                .getStrategyEngine_DeployedStrategies();

        /**
         * The meta object literal for the '<em><b>Read Only</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STRATEGY_ENGINE__READ_ONLY = eINSTANCE
                .getStrategyEngine_ReadOnly();

        /**
         * The meta object literal for the '{@link org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineConnectionImpl <em>Strategy Engine Connection</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineConnectionImpl
         * @see org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineCorePackageImpl#getStrategyEngineConnection()
         * @generated
         */
        EClass STRATEGY_ENGINE_CONNECTION = eINSTANCE
                .getStrategyEngineConnection();

        /**
         * The meta object literal for the '<em><b>Engine</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference STRATEGY_ENGINE_CONNECTION__ENGINE = eINSTANCE
                .getStrategyEngineConnection_Engine();

        /**
         * The meta object literal for the '{@link org.marketcetera.photon.strategy.engine.model.core.impl.StrategyImpl <em>Strategy</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.marketcetera.photon.strategy.engine.model.core.impl.StrategyImpl
         * @see org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineCorePackageImpl#getStrategy()
         * @generated
         */
        EClass STRATEGY = eINSTANCE.getStrategy();

        /**
         * The meta object literal for the '<em><b>Instance Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STRATEGY__INSTANCE_NAME = eINSTANCE
                .getStrategy_InstanceName();

        /**
         * The meta object literal for the '<em><b>Language</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STRATEGY__LANGUAGE = eINSTANCE.getStrategy_Language();

        /**
         * The meta object literal for the '<em><b>Script Path</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STRATEGY__SCRIPT_PATH = eINSTANCE.getStrategy_ScriptPath();

        /**
         * The meta object literal for the '<em><b>Class Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STRATEGY__CLASS_NAME = eINSTANCE.getStrategy_ClassName();

        /**
         * The meta object literal for the '<em><b>Route Orders To Server</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STRATEGY__ROUTE_ORDERS_TO_SERVER = eINSTANCE
                .getStrategy_RouteOrdersToServer();

        /**
         * The meta object literal for the '<em><b>Parameters</b></em>' map feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference STRATEGY__PARAMETERS = eINSTANCE.getStrategy_Parameters();

        /**
         * The meta object literal for the '{@link org.marketcetera.photon.strategy.engine.model.core.impl.DeployedStrategyImpl <em>Deployed Strategy</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.marketcetera.photon.strategy.engine.model.core.impl.DeployedStrategyImpl
         * @see org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineCorePackageImpl#getDeployedStrategy()
         * @generated
         */
        EClass DEPLOYED_STRATEGY = eINSTANCE.getDeployedStrategy();

        /**
         * The meta object literal for the '<em><b>Engine</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DEPLOYED_STRATEGY__ENGINE = eINSTANCE
                .getDeployedStrategy_Engine();

        /**
         * The meta object literal for the '<em><b>State</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute DEPLOYED_STRATEGY__STATE = eINSTANCE
                .getDeployedStrategy_State();

        /**
         * The meta object literal for the '<em><b>Urn</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute DEPLOYED_STRATEGY__URN = eINSTANCE.getDeployedStrategy_Urn();

        /**
         * The meta object literal for the '{@link org.marketcetera.photon.strategy.engine.model.core.impl.StringToStringMapEntryImpl <em>String To String Map Entry</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.marketcetera.photon.strategy.engine.model.core.impl.StringToStringMapEntryImpl
         * @see org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineCorePackageImpl#getStringToStringMapEntry()
         * @generated
         */
        EClass STRING_TO_STRING_MAP_ENTRY = eINSTANCE
                .getStringToStringMapEntry();

        /**
         * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STRING_TO_STRING_MAP_ENTRY__KEY = eINSTANCE
                .getStringToStringMapEntry_Key();

        /**
         * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STRING_TO_STRING_MAP_ENTRY__VALUE = eINSTANCE
                .getStringToStringMapEntry_Value();

        /**
         * The meta object literal for the '{@link org.marketcetera.photon.strategy.engine.model.core.ConnectionState <em>Connection State</em>}' enum.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.marketcetera.photon.strategy.engine.model.core.ConnectionState
         * @see org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineCorePackageImpl#getConnectionState()
         * @generated
         */
        EEnum CONNECTION_STATE = eINSTANCE.getConnectionState();

        /**
         * The meta object literal for the '{@link org.marketcetera.photon.strategy.engine.model.core.StrategyState <em>Strategy State</em>}' enum.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.marketcetera.photon.strategy.engine.model.core.StrategyState
         * @see org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineCorePackageImpl#getStrategyState()
         * @generated
         */
        EEnum STRATEGY_STATE = eINSTANCE.getStrategyState();

        /**
         * The meta object literal for the '<em>Module URN</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.marketcetera.module.ModuleURN
         * @see org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineCorePackageImpl#getModuleURN()
         * @generated
         */
        EDataType MODULE_URN = eINSTANCE.getModuleURN();

        /**
         * The meta object literal for the '<em>Exception</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.lang.Exception
         * @see org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineCorePackageImpl#getException()
         * @generated
         */
        EDataType EXCEPTION = eINSTANCE.getException();

    }

} //StrategyEngineCorePackage
