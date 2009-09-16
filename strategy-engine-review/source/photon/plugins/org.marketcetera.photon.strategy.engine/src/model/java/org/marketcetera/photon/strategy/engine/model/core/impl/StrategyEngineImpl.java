/**
 * $License$
 *
 * $Id$
 */
package org.marketcetera.photon.strategy.engine.model.core.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.marketcetera.photon.commons.SynchronizedProxy;
import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineConnection;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage;

import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Strategy Engine</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineImpl#getConnectionState <em>Connection State</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineImpl#getConnection <em>Connection</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineImpl#getDeployedStrategies <em>Deployed Strategies</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineImpl#isReadOnly <em>Read Only</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 * @since $Release$
 */
@ClassVersion("$Id$")
public class StrategyEngineImpl extends EObjectImpl implements StrategyEngine {
    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected volatile String name = NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDescription()
     * @generated
     * @ordered
     */
    protected static final String DESCRIPTION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDescription()
     * @generated
     * @ordered
     */
    protected volatile String description = DESCRIPTION_EDEFAULT;

    /**
     * The default value of the '{@link #getConnectionState() <em>Connection State</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getConnectionState()
     * @generated
     * @ordered
     */
    protected static final ConnectionState CONNECTION_STATE_EDEFAULT = ConnectionState.DISCONNECTED;

    /**
     * The cached value of the '{@link #getConnectionState() <em>Connection State</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getConnectionState()
     * @generated
     * @ordered
     */
    protected volatile ConnectionState connectionState = CONNECTION_STATE_EDEFAULT;

    /**
     * The cached value of the '{@link #getConnection() <em>Connection</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getConnection()
     * @generated
     * @ordered
     */
    protected volatile StrategyEngineConnection connection;

    /**
     * The cached value of the '{@link #getDeployedStrategies() <em>Deployed Strategies</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDeployedStrategies()
     * @generated
     * @ordered
     */
    protected volatile EList<DeployedStrategy> deployedStrategies;

    /**
     * The default value of the '{@link #isReadOnly() <em>Read Only</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isReadOnly()
     * @generated
     * @ordered
     */
    protected static final boolean READ_ONLY_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isReadOnly() <em>Read Only</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isReadOnly()
     * @generated
     * @ordered
     */
    protected volatile boolean readOnly = READ_ONLY_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected StrategyEngineImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return StrategyEngineCorePackage.Literals.STRATEGY_ENGINE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setName(String newName) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    StrategyEngineCorePackage.STRATEGY_ENGINE__NAME, oldName,
                    name));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getDescription() {
        return description;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDescription(String newDescription) {
        String oldDescription = description;
        description = newDescription;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    StrategyEngineCorePackage.STRATEGY_ENGINE__DESCRIPTION,
                    oldDescription, description));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ConnectionState getConnectionState() {
        return connectionState;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setConnectionState(ConnectionState newConnectionState) {
        ConnectionState oldConnectionState = connectionState;
        connectionState = newConnectionState == null ? CONNECTION_STATE_EDEFAULT
                : newConnectionState;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this,
                    Notification.SET,
                    StrategyEngineCorePackage.STRATEGY_ENGINE__CONNECTION_STATE,
                    oldConnectionState, connectionState));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public synchronized StrategyEngineConnection getConnection() {
        if (connection != null && connection.eIsProxy()) {
            InternalEObject oldConnection = (InternalEObject) connection;
            connection = (StrategyEngineConnection) eResolveProxy(oldConnection);
            if (connection != oldConnection) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(
                            this,
                            Notification.RESOLVE,
                            StrategyEngineCorePackage.STRATEGY_ENGINE__CONNECTION,
                            oldConnection, connection));
            }
        }
        return connection;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public StrategyEngineConnection basicGetConnection() {
        return connection;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetConnection(
            StrategyEngineConnection newConnection, NotificationChain msgs) {
        StrategyEngineConnection oldConnection = connection;
        connection = newConnection;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this,
                    Notification.SET,
                    StrategyEngineCorePackage.STRATEGY_ENGINE__CONNECTION,
                    oldConnection, newConnection);
            if (msgs == null)
                msgs = notification;
            else
                msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setConnection(StrategyEngineConnection newConnection) {
        if (newConnection != connection) {
            NotificationChain msgs = null;
            if (connection != null)
                msgs = ((InternalEObject) connection)
                        .eInverseRemove(
                                this,
                                StrategyEngineCorePackage.STRATEGY_ENGINE_CONNECTION__ENGINE,
                                StrategyEngineConnection.class, msgs);
            if (newConnection != null)
                msgs = ((InternalEObject) newConnection)
                        .eInverseAdd(
                                this,
                                StrategyEngineCorePackage.STRATEGY_ENGINE_CONNECTION__ENGINE,
                                StrategyEngineConnection.class, msgs);
            msgs = basicSetConnection(newConnection, msgs);
            if (msgs != null)
                msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    StrategyEngineCorePackage.STRATEGY_ENGINE__CONNECTION,
                    newConnection, newConnection));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    @SuppressWarnings("unchecked")
    public synchronized EList<DeployedStrategy> getDeployedStrategies() {
        if (deployedStrategies == null) {
            EList<DeployedStrategy> delegate = new EObjectWithInverseResolvingEList<DeployedStrategy>(
                    DeployedStrategy.class,
                    this,
                    StrategyEngineCorePackage.STRATEGY_ENGINE__DEPLOYED_STRATEGIES,
                    StrategyEngineCorePackage.DEPLOYED_STRATEGY__ENGINE);
            deployedStrategies = (EList<DeployedStrategy>) SynchronizedProxy
                    .proxy(delegate, InternalEList.class);
        }
        return deployedStrategies;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setReadOnly(boolean newReadOnly) {
        boolean oldReadOnly = readOnly;
        readOnly = newReadOnly;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    StrategyEngineCorePackage.STRATEGY_ENGINE__READ_ONLY,
                    oldReadOnly, readOnly));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd,
            int featureID, NotificationChain msgs) {
        switch (featureID) {
        case StrategyEngineCorePackage.STRATEGY_ENGINE__CONNECTION:
            if (connection != null)
                msgs = ((InternalEObject) connection)
                        .eInverseRemove(
                                this,
                                StrategyEngineCorePackage.STRATEGY_ENGINE_CONNECTION__ENGINE,
                                StrategyEngineConnection.class, msgs);
            return basicSetConnection((StrategyEngineConnection) otherEnd, msgs);
        case StrategyEngineCorePackage.STRATEGY_ENGINE__DEPLOYED_STRATEGIES:
            return ((InternalEList<InternalEObject>) (InternalEList<?>) getDeployedStrategies())
                    .basicAdd(otherEnd, msgs);
        }
        return super.eInverseAdd(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd,
            int featureID, NotificationChain msgs) {
        switch (featureID) {
        case StrategyEngineCorePackage.STRATEGY_ENGINE__CONNECTION:
            return basicSetConnection(null, msgs);
        case StrategyEngineCorePackage.STRATEGY_ENGINE__DEPLOYED_STRATEGIES:
            return ((InternalEList<?>) getDeployedStrategies()).basicRemove(
                    otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case StrategyEngineCorePackage.STRATEGY_ENGINE__NAME:
            return getName();
        case StrategyEngineCorePackage.STRATEGY_ENGINE__DESCRIPTION:
            return getDescription();
        case StrategyEngineCorePackage.STRATEGY_ENGINE__CONNECTION_STATE:
            return getConnectionState();
        case StrategyEngineCorePackage.STRATEGY_ENGINE__CONNECTION:
            if (resolve)
                return getConnection();
            return basicGetConnection();
        case StrategyEngineCorePackage.STRATEGY_ENGINE__DEPLOYED_STRATEGIES:
            return getDeployedStrategies();
        case StrategyEngineCorePackage.STRATEGY_ENGINE__READ_ONLY:
            return isReadOnly();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
        case StrategyEngineCorePackage.STRATEGY_ENGINE__NAME:
            setName((String) newValue);
            return;
        case StrategyEngineCorePackage.STRATEGY_ENGINE__DESCRIPTION:
            setDescription((String) newValue);
            return;
        case StrategyEngineCorePackage.STRATEGY_ENGINE__CONNECTION_STATE:
            setConnectionState((ConnectionState) newValue);
            return;
        case StrategyEngineCorePackage.STRATEGY_ENGINE__CONNECTION:
            setConnection((StrategyEngineConnection) newValue);
            return;
        case StrategyEngineCorePackage.STRATEGY_ENGINE__DEPLOYED_STRATEGIES:
            getDeployedStrategies().clear();
            getDeployedStrategies().addAll(
                    (Collection<? extends DeployedStrategy>) newValue);
            return;
        case StrategyEngineCorePackage.STRATEGY_ENGINE__READ_ONLY:
            setReadOnly((Boolean) newValue);
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
        case StrategyEngineCorePackage.STRATEGY_ENGINE__NAME:
            setName(NAME_EDEFAULT);
            return;
        case StrategyEngineCorePackage.STRATEGY_ENGINE__DESCRIPTION:
            setDescription(DESCRIPTION_EDEFAULT);
            return;
        case StrategyEngineCorePackage.STRATEGY_ENGINE__CONNECTION_STATE:
            setConnectionState(CONNECTION_STATE_EDEFAULT);
            return;
        case StrategyEngineCorePackage.STRATEGY_ENGINE__CONNECTION:
            setConnection((StrategyEngineConnection) null);
            return;
        case StrategyEngineCorePackage.STRATEGY_ENGINE__DEPLOYED_STRATEGIES:
            getDeployedStrategies().clear();
            return;
        case StrategyEngineCorePackage.STRATEGY_ENGINE__READ_ONLY:
            setReadOnly(READ_ONLY_EDEFAULT);
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
        case StrategyEngineCorePackage.STRATEGY_ENGINE__NAME:
            return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT
                    .equals(name);
        case StrategyEngineCorePackage.STRATEGY_ENGINE__DESCRIPTION:
            return DESCRIPTION_EDEFAULT == null ? description != null
                    : !DESCRIPTION_EDEFAULT.equals(description);
        case StrategyEngineCorePackage.STRATEGY_ENGINE__CONNECTION_STATE:
            return connectionState != CONNECTION_STATE_EDEFAULT;
        case StrategyEngineCorePackage.STRATEGY_ENGINE__CONNECTION:
            return connection != null;
        case StrategyEngineCorePackage.STRATEGY_ENGINE__DEPLOYED_STRATEGIES:
            return deployedStrategies != null && !deployedStrategies.isEmpty();
        case StrategyEngineCorePackage.STRATEGY_ENGINE__READ_ONLY:
            return readOnly != READ_ONLY_EDEFAULT;
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
        result.append(" (name: ");
        result.append(name);
        result.append(", description: ");
        result.append(description);
        result.append(", connectionState: ");
        result.append(connectionState);
        result.append(", readOnly: ");
        result.append(readOnly);
        result.append(')');
        return result.toString();
    }

} //StrategyEngineImpl
