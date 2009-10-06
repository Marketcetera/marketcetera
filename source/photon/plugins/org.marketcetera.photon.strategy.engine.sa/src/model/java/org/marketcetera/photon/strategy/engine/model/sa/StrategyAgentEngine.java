/**
 * $License$
 *
 * $Id$
 */
package org.marketcetera.photon.strategy.engine.model.sa;

import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;

import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Strategy Agent Engine</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine#getJmsUrl <em>Jms Url</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine#getWebServiceHostname <em>Web Service Hostname</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine#getWebServicePort <em>Web Service Port</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEnginePackage#getStrategyAgentEngine()
 * @model
 * @generated
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface StrategyAgentEngine extends StrategyEngine {
    /**
     * Returns the value of the '<em><b>Jms Url</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Jms Url</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Jms Url</em>' attribute.
     * @see #setJmsUrl(String)
     * @see org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEnginePackage#getStrategyAgentEngine_JmsUrl()
     * @model required="true"
     * @generated
     */
    String getJmsUrl();

    /**
     * Sets the value of the '{@link org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine#getJmsUrl <em>Jms Url</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Jms Url</em>' attribute.
     * @see #getJmsUrl()
     * @generated
     */
    void setJmsUrl(String value);

    /**
     * Returns the value of the '<em><b>Web Service Hostname</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Web Service Hostname</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Web Service Hostname</em>' attribute.
     * @see #setWebServiceHostname(String)
     * @see org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEnginePackage#getStrategyAgentEngine_WebServiceHostname()
     * @model required="true"
     * @generated
     */
    String getWebServiceHostname();

    /**
     * Sets the value of the '{@link org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine#getWebServiceHostname <em>Web Service Hostname</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Web Service Hostname</em>' attribute.
     * @see #getWebServiceHostname()
     * @generated
     */
    void setWebServiceHostname(String value);

    /**
     * Returns the value of the '<em><b>Web Service Port</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Web Service Port</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Web Service Port</em>' attribute.
     * @see #setWebServicePort(Integer)
     * @see org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEnginePackage#getStrategyAgentEngine_WebServicePort()
     * @model required="true"
     * @generated
     */
    Integer getWebServicePort();

    /**
     * Sets the value of the '{@link org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine#getWebServicePort <em>Web Service Port</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Web Service Port</em>' attribute.
     * @see #getWebServicePort()
     * @generated
     */
    void setWebServicePort(Integer value);

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model exceptions="org.marketcetera.photon.strategy.engine.model.core.Exception"
     * @generated
     */
    void connect() throws Exception;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model exceptions="org.marketcetera.photon.strategy.engine.model.core.Exception"
     * @generated
     */
    void disconnect() throws Exception;

} // StrategyAgentEngine
