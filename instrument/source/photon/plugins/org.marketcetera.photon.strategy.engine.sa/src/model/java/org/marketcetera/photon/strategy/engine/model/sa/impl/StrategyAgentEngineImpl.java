/**
 * $License$
 *
 * $Id$
 */
package org.marketcetera.photon.strategy.engine.model.sa.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineImpl;

import org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine;
import org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEnginePackage;

import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Strategy Agent Engine</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.sa.impl.StrategyAgentEngineImpl#getJmsUrl <em>Jms Url</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.sa.impl.StrategyAgentEngineImpl#getWebServiceHostname <em>Web Service Hostname</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.sa.impl.StrategyAgentEngineImpl#getWebServicePort <em>Web Service Port</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 * @since $Release$
 */
@ClassVersion("$Id$")
public class StrategyAgentEngineImpl extends StrategyEngineImpl implements
        StrategyAgentEngine {
    /**
     * The default value of the '{@link #getJmsUrl() <em>Jms Url</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getJmsUrl()
     * @generated
     * @ordered
     */
    protected static final String JMS_URL_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getJmsUrl() <em>Jms Url</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getJmsUrl()
     * @generated
     * @ordered
     */
    protected volatile String jmsUrl = JMS_URL_EDEFAULT;

    /**
     * The default value of the '{@link #getWebServiceHostname() <em>Web Service Hostname</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getWebServiceHostname()
     * @generated
     * @ordered
     */
    protected static final String WEB_SERVICE_HOSTNAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getWebServiceHostname() <em>Web Service Hostname</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getWebServiceHostname()
     * @generated
     * @ordered
     */
    protected volatile String webServiceHostname = WEB_SERVICE_HOSTNAME_EDEFAULT;

    /**
     * The default value of the '{@link #getWebServicePort() <em>Web Service Port</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getWebServicePort()
     * @generated
     * @ordered
     */
    protected static final Integer WEB_SERVICE_PORT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getWebServicePort() <em>Web Service Port</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getWebServicePort()
     * @generated
     * @ordered
     */
    protected volatile Integer webServicePort = WEB_SERVICE_PORT_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public StrategyAgentEngineImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return StrategyAgentEnginePackage.Literals.STRATEGY_AGENT_ENGINE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getJmsUrl() {
        return jmsUrl;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setJmsUrl(String newJmsUrl) {
        String oldJmsUrl = jmsUrl;
        jmsUrl = newJmsUrl;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    StrategyAgentEnginePackage.STRATEGY_AGENT_ENGINE__JMS_URL,
                    oldJmsUrl, jmsUrl));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getWebServiceHostname() {
        return webServiceHostname;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setWebServiceHostname(String newWebServiceHostname) {
        String oldWebServiceHostname = webServiceHostname;
        webServiceHostname = newWebServiceHostname;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this,
                    Notification.SET,
                    StrategyAgentEnginePackage.STRATEGY_AGENT_ENGINE__WEB_SERVICE_HOSTNAME,
                    oldWebServiceHostname, webServiceHostname));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Integer getWebServicePort() {
        return webServicePort;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setWebServicePort(Integer newWebServicePort) {
        Integer oldWebServicePort = webServicePort;
        webServicePort = newWebServicePort;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this,
                    Notification.SET,
                    StrategyAgentEnginePackage.STRATEGY_AGENT_ENGINE__WEB_SERVICE_PORT,
                    oldWebServicePort, webServicePort));
    }

    @Override
    public void connect() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void disconnect() throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case StrategyAgentEnginePackage.STRATEGY_AGENT_ENGINE__JMS_URL:
            return getJmsUrl();
        case StrategyAgentEnginePackage.STRATEGY_AGENT_ENGINE__WEB_SERVICE_HOSTNAME:
            return getWebServiceHostname();
        case StrategyAgentEnginePackage.STRATEGY_AGENT_ENGINE__WEB_SERVICE_PORT:
            return getWebServicePort();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
        case StrategyAgentEnginePackage.STRATEGY_AGENT_ENGINE__JMS_URL:
            setJmsUrl((String) newValue);
            return;
        case StrategyAgentEnginePackage.STRATEGY_AGENT_ENGINE__WEB_SERVICE_HOSTNAME:
            setWebServiceHostname((String) newValue);
            return;
        case StrategyAgentEnginePackage.STRATEGY_AGENT_ENGINE__WEB_SERVICE_PORT:
            setWebServicePort((Integer) newValue);
            return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
        case StrategyAgentEnginePackage.STRATEGY_AGENT_ENGINE__JMS_URL:
            setJmsUrl(JMS_URL_EDEFAULT);
            return;
        case StrategyAgentEnginePackage.STRATEGY_AGENT_ENGINE__WEB_SERVICE_HOSTNAME:
            setWebServiceHostname(WEB_SERVICE_HOSTNAME_EDEFAULT);
            return;
        case StrategyAgentEnginePackage.STRATEGY_AGENT_ENGINE__WEB_SERVICE_PORT:
            setWebServicePort(WEB_SERVICE_PORT_EDEFAULT);
            return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
        case StrategyAgentEnginePackage.STRATEGY_AGENT_ENGINE__JMS_URL:
            return JMS_URL_EDEFAULT == null ? jmsUrl != null
                    : !JMS_URL_EDEFAULT.equals(jmsUrl);
        case StrategyAgentEnginePackage.STRATEGY_AGENT_ENGINE__WEB_SERVICE_HOSTNAME:
            return WEB_SERVICE_HOSTNAME_EDEFAULT == null ? webServiceHostname != null
                    : !WEB_SERVICE_HOSTNAME_EDEFAULT.equals(webServiceHostname);
        case StrategyAgentEnginePackage.STRATEGY_AGENT_ENGINE__WEB_SERVICE_PORT:
            return WEB_SERVICE_PORT_EDEFAULT == null ? webServicePort != null
                    : !WEB_SERVICE_PORT_EDEFAULT.equals(webServicePort);
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy())
            return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (jmsUrl: ");
        result.append(jmsUrl);
        result.append(", webServiceHostname: ");
        result.append(webServiceHostname);
        result.append(", webServicePort: ");
        result.append(webServicePort);
        result.append(')');
        return result.toString();
    }

} //StrategyAgentEngineImpl
