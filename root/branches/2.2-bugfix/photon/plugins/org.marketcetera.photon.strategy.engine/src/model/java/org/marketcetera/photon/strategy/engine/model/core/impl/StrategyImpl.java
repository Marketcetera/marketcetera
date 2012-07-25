/**
 * $License$
 *
 * $Id$
 */
package org.marketcetera.photon.strategy.engine.model.core.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.marketcetera.photon.commons.SynchronizedProxy;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage;

import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Strategy</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.impl.StrategyImpl#getInstanceName <em>Instance Name</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.impl.StrategyImpl#getLanguage <em>Language</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.impl.StrategyImpl#getScriptPath <em>Script Path</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.impl.StrategyImpl#getClassName <em>Class Name</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.impl.StrategyImpl#isRouteOrdersToServer <em>Route Orders To Server</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.impl.StrategyImpl#getParameters <em>Parameters</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 * @since 2.1.0
 */
@ClassVersion("$Id$")
public class StrategyImpl
        extends EObjectImpl
        implements Strategy
{
    /**
     * The default value of the '{@link #getInstanceName() <em>Instance Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInstanceName()
     * @generated
     * @ordered
     */
    protected static final String INSTANCE_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getInstanceName() <em>Instance Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInstanceName()
     * @generated
     * @ordered
     */
    protected volatile String instanceName = INSTANCE_NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getLanguage() <em>Language</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLanguage()
     * @generated
     * @ordered
     */
    protected static final String LANGUAGE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getLanguage() <em>Language</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLanguage()
     * @generated
     * @ordered
     */
    protected volatile String language = LANGUAGE_EDEFAULT;

    /**
     * The default value of the '{@link #getScriptPath() <em>Script Path</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getScriptPath()
     * @generated
     * @ordered
     */
    protected static final String SCRIPT_PATH_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getScriptPath() <em>Script Path</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getScriptPath()
     * @generated
     * @ordered
     */
    protected volatile String scriptPath = SCRIPT_PATH_EDEFAULT;

    /**
     * The default value of the '{@link #getClassName() <em>Class Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getClassName()
     * @generated
     * @ordered
     */
    protected static final String CLASS_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getClassName() <em>Class Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getClassName()
     * @generated
     * @ordered
     */
    protected volatile String className = CLASS_NAME_EDEFAULT;

    /**
     * The default value of the '{@link #isRouteOrdersToServer() <em>Route Orders To Server</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isRouteOrdersToServer()
     * @generated
     * @ordered
     */
    protected static final boolean ROUTE_ORDERS_TO_SERVER_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isRouteOrdersToServer() <em>Route Orders To Server</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isRouteOrdersToServer()
     * @generated
     * @ordered
     */
    protected volatile boolean routeOrdersToServer = ROUTE_ORDERS_TO_SERVER_EDEFAULT;

    /**
     * The cached value of the '{@link #getParameters() <em>Parameters</em>}' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getParameters()
     * @generated
     * @ordered
     */
    protected volatile EMap<String, String> parameters;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected StrategyImpl()
    {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass()
    {
        return StrategyEngineCorePackage.Literals.STRATEGY;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getInstanceName()
    {
        return instanceName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setInstanceName(String newInstanceName)
    {
        String oldInstanceName = instanceName;
        instanceName = newInstanceName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this,
                                          Notification.SET,
                                          StrategyEngineCorePackage.STRATEGY__INSTANCE_NAME,
                                          oldInstanceName,
                                          instanceName));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getLanguage()
    {
        return language;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setLanguage(String newLanguage)
    {
        String oldLanguage = language;
        language = newLanguage;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this,
                                          Notification.SET,
                                          StrategyEngineCorePackage.STRATEGY__LANGUAGE,
                                          oldLanguage,
                                          language));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getScriptPath()
    {
        return scriptPath;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setScriptPath(String newScriptPath)
    {
        String oldScriptPath = scriptPath;
        scriptPath = newScriptPath;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this,
                                          Notification.SET,
                                          StrategyEngineCorePackage.STRATEGY__SCRIPT_PATH,
                                          oldScriptPath,
                                          scriptPath));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setClassName(String newClassName)
    {
        String oldClassName = className;
        className = newClassName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this,
                                          Notification.SET,
                                          StrategyEngineCorePackage.STRATEGY__CLASS_NAME,
                                          oldClassName,
                                          className));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isRouteOrdersToServer()
    {
        return routeOrdersToServer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setRouteOrdersToServer(boolean newRouteOrdersToServer)
    {
        boolean oldRouteOrdersToServer = routeOrdersToServer;
        routeOrdersToServer = newRouteOrdersToServer;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this,
                                          Notification.SET,
                                          StrategyEngineCorePackage.STRATEGY__ROUTE_ORDERS_TO_SERVER,
                                          oldRouteOrdersToServer,
                                          routeOrdersToServer));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    @SuppressWarnings("unchecked")
    public synchronized EMap<String, String> getParameters()
    {
        if (parameters == null) {
            EMap<String, String> delegate = new EcoreEMap<String, String>(StrategyEngineCorePackage.Literals.STRING_TO_STRING_MAP_ENTRY,
                                                                          StringToStringMapEntryImpl.class,
                                                                          this,
                                                                          StrategyEngineCorePackage.STRATEGY__PARAMETERS);
            parameters = (EMap<String, String>) SynchronizedProxy.proxy(delegate,
                                                                        EMap.class,
                                                                        InternalEList.class);
        }
        return parameters;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd,
                                            int featureID,
                                            NotificationChain msgs)
    {
        switch (featureID) {
            case StrategyEngineCorePackage.STRATEGY__PARAMETERS:
                return ((InternalEList<?>) getParameters()).basicRemove(otherEnd,
                                                                        msgs);
        }
        return super.eInverseRemove(otherEnd,
                                    featureID,
                                    msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID,
                       boolean resolve,
                       boolean coreType)
    {
        switch (featureID) {
            case StrategyEngineCorePackage.STRATEGY__INSTANCE_NAME:
                return getInstanceName();
            case StrategyEngineCorePackage.STRATEGY__LANGUAGE:
                return getLanguage();
            case StrategyEngineCorePackage.STRATEGY__SCRIPT_PATH:
                return getScriptPath();
            case StrategyEngineCorePackage.STRATEGY__CLASS_NAME:
                return getClassName();
            case StrategyEngineCorePackage.STRATEGY__ROUTE_ORDERS_TO_SERVER:
                return isRouteOrdersToServer();
            case StrategyEngineCorePackage.STRATEGY__PARAMETERS:
                if (coreType)
                    return getParameters();
                else
                    return getParameters().map();
        }
        return super.eGet(featureID,
                          resolve,
                          coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID,
                     Object newValue)
    {
        switch (featureID) {
            case StrategyEngineCorePackage.STRATEGY__INSTANCE_NAME:
                setInstanceName((String) newValue);
                return;
            case StrategyEngineCorePackage.STRATEGY__LANGUAGE:
                setLanguage((String) newValue);
                return;
            case StrategyEngineCorePackage.STRATEGY__SCRIPT_PATH:
                setScriptPath((String) newValue);
                return;
            case StrategyEngineCorePackage.STRATEGY__CLASS_NAME:
                setClassName((String) newValue);
                return;
            case StrategyEngineCorePackage.STRATEGY__ROUTE_ORDERS_TO_SERVER:
                setRouteOrdersToServer((Boolean) newValue);
                return;
            case StrategyEngineCorePackage.STRATEGY__PARAMETERS:
                ((EStructuralFeature.Setting) getParameters()).set(newValue);
                return;
        }
        super.eSet(featureID,
                   newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID)
    {
        switch (featureID) {
            case StrategyEngineCorePackage.STRATEGY__INSTANCE_NAME:
                setInstanceName(INSTANCE_NAME_EDEFAULT);
                return;
            case StrategyEngineCorePackage.STRATEGY__LANGUAGE:
                setLanguage(LANGUAGE_EDEFAULT);
                return;
            case StrategyEngineCorePackage.STRATEGY__SCRIPT_PATH:
                setScriptPath(SCRIPT_PATH_EDEFAULT);
                return;
            case StrategyEngineCorePackage.STRATEGY__CLASS_NAME:
                setClassName(CLASS_NAME_EDEFAULT);
                return;
            case StrategyEngineCorePackage.STRATEGY__ROUTE_ORDERS_TO_SERVER:
                setRouteOrdersToServer(ROUTE_ORDERS_TO_SERVER_EDEFAULT);
                return;
            case StrategyEngineCorePackage.STRATEGY__PARAMETERS:
                getParameters().clear();
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
    public boolean eIsSet(int featureID)
    {
        switch (featureID) {
            case StrategyEngineCorePackage.STRATEGY__INSTANCE_NAME:
                return INSTANCE_NAME_EDEFAULT == null ? instanceName != null : !INSTANCE_NAME_EDEFAULT
                        .equals(instanceName);
            case StrategyEngineCorePackage.STRATEGY__LANGUAGE:
                return LANGUAGE_EDEFAULT == null ? language != null : !LANGUAGE_EDEFAULT.equals(language);
            case StrategyEngineCorePackage.STRATEGY__SCRIPT_PATH:
                return SCRIPT_PATH_EDEFAULT == null ? scriptPath != null : !SCRIPT_PATH_EDEFAULT.equals(scriptPath);
            case StrategyEngineCorePackage.STRATEGY__CLASS_NAME:
                return CLASS_NAME_EDEFAULT == null ? className != null : !CLASS_NAME_EDEFAULT.equals(className);
            case StrategyEngineCorePackage.STRATEGY__ROUTE_ORDERS_TO_SERVER:
                return routeOrdersToServer != ROUTE_ORDERS_TO_SERVER_EDEFAULT;
            case StrategyEngineCorePackage.STRATEGY__PARAMETERS:
                return parameters != null && !parameters.isEmpty();
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString()
    {
        if (eIsProxy())
            return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (instanceName: ");
        result.append(instanceName);
        result.append(", language: ");
        result.append(language);
        result.append(", scriptPath: ");
        result.append(scriptPath);
        result.append(", className: ");
        result.append(className);
        result.append(", routeOrdersToServer: ");
        result.append(routeOrdersToServer);
        result.append(')');
        return result.toString();
    }

} //StrategyImpl
