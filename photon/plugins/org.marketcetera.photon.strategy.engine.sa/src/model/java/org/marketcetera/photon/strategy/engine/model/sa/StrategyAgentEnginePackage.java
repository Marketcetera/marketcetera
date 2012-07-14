/**
 * $License$
 *
 * $Id$
 */
package org.marketcetera.photon.strategy.engine.model.sa;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage;

import org.marketcetera.util.misc.ClassVersion;

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
 * @see org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngineFactory
 * @model kind="package"
 * @generated
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public interface StrategyAgentEnginePackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "sa";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.marketcetera.org/photon/strategy/engine/strategyagent/1.0";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "strategyagent";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    StrategyAgentEnginePackage eINSTANCE = org.marketcetera.photon.strategy.engine.model.sa.impl.StrategyAgentEnginePackageImpl
            .init();

    /**
     * The meta object id for the '{@link org.marketcetera.photon.strategy.engine.model.sa.impl.StrategyAgentEngineImpl <em>Strategy Agent Engine</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.marketcetera.photon.strategy.engine.model.sa.impl.StrategyAgentEngineImpl
     * @see org.marketcetera.photon.strategy.engine.model.sa.impl.StrategyAgentEnginePackageImpl#getStrategyAgentEngine()
     * @generated
     */
    int STRATEGY_AGENT_ENGINE = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY_AGENT_ENGINE__NAME = StrategyEngineCorePackage.STRATEGY_ENGINE__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY_AGENT_ENGINE__DESCRIPTION = StrategyEngineCorePackage.STRATEGY_ENGINE__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Connection State</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY_AGENT_ENGINE__CONNECTION_STATE = StrategyEngineCorePackage.STRATEGY_ENGINE__CONNECTION_STATE;

    /**
     * The feature id for the '<em><b>Connection</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY_AGENT_ENGINE__CONNECTION = StrategyEngineCorePackage.STRATEGY_ENGINE__CONNECTION;

    /**
     * The feature id for the '<em><b>Deployed Strategies</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY_AGENT_ENGINE__DEPLOYED_STRATEGIES = StrategyEngineCorePackage.STRATEGY_ENGINE__DEPLOYED_STRATEGIES;

    /**
     * The feature id for the '<em><b>Read Only</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY_AGENT_ENGINE__READ_ONLY = StrategyEngineCorePackage.STRATEGY_ENGINE__READ_ONLY;

    /**
     * The feature id for the '<em><b>Jms Url</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY_AGENT_ENGINE__JMS_URL = StrategyEngineCorePackage.STRATEGY_ENGINE_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Web Service Hostname</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY_AGENT_ENGINE__WEB_SERVICE_HOSTNAME = StrategyEngineCorePackage.STRATEGY_ENGINE_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Web Service Port</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY_AGENT_ENGINE__WEB_SERVICE_PORT = StrategyEngineCorePackage.STRATEGY_ENGINE_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the '<em>Strategy Agent Engine</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STRATEGY_AGENT_ENGINE_FEATURE_COUNT = StrategyEngineCorePackage.STRATEGY_ENGINE_FEATURE_COUNT + 3;

    /**
     * Returns the meta object for class '{@link org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine <em>Strategy Agent Engine</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Strategy Agent Engine</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine
     * @generated
     */
    EClass getStrategyAgentEngine();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine#getJmsUrl <em>Jms Url</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Jms Url</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine#getJmsUrl()
     * @see #getStrategyAgentEngine()
     * @generated
     */
    EAttribute getStrategyAgentEngine_JmsUrl();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine#getWebServiceHostname <em>Web Service Hostname</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Web Service Hostname</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine#getWebServiceHostname()
     * @see #getStrategyAgentEngine()
     * @generated
     */
    EAttribute getStrategyAgentEngine_WebServiceHostname();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine#getWebServicePort <em>Web Service Port</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Web Service Port</em>'.
     * @see org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine#getWebServicePort()
     * @see #getStrategyAgentEngine()
     * @generated
     */
    EAttribute getStrategyAgentEngine_WebServicePort();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    StrategyAgentEngineFactory getStrategyAgentEngineFactory();

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
         * The meta object literal for the '{@link org.marketcetera.photon.strategy.engine.model.sa.impl.StrategyAgentEngineImpl <em>Strategy Agent Engine</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.marketcetera.photon.strategy.engine.model.sa.impl.StrategyAgentEngineImpl
         * @see org.marketcetera.photon.strategy.engine.model.sa.impl.StrategyAgentEnginePackageImpl#getStrategyAgentEngine()
         * @generated
         */
        EClass STRATEGY_AGENT_ENGINE = eINSTANCE.getStrategyAgentEngine();

        /**
         * The meta object literal for the '<em><b>Jms Url</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STRATEGY_AGENT_ENGINE__JMS_URL = eINSTANCE
                .getStrategyAgentEngine_JmsUrl();

        /**
         * The meta object literal for the '<em><b>Web Service Hostname</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STRATEGY_AGENT_ENGINE__WEB_SERVICE_HOSTNAME = eINSTANCE
                .getStrategyAgentEngine_WebServiceHostname();

        /**
         * The meta object literal for the '<em><b>Web Service Port</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STRATEGY_AGENT_ENGINE__WEB_SERVICE_PORT = eINSTANCE
                .getStrategyAgentEngine_WebServicePort();

    }

} //StrategyAgentEnginePackage
