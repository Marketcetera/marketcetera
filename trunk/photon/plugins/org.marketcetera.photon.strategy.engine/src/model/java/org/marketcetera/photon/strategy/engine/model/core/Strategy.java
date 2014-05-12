/**
 * $License$
 *
 * $Id$
 */
package org.marketcetera.photon.strategy.engine.model.core;

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EObject;

import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Strategy</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.Strategy#getInstanceName <em>Instance Name</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.Strategy#getLanguage <em>Language</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.Strategy#getScriptPath <em>Script Path</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.Strategy#getClassName <em>Class Name</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.Strategy#isRouteOrdersToServer <em>Route Orders To Server</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.Strategy#getParameters <em>Parameters</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage#getStrategy()
 * @model
 * @generated
 * @since 2.1.0
 */
@ClassVersion("$Id$")
public interface Strategy
        extends EObject
{
    /**
     * Returns the value of the '<em><b>Instance Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Instance Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Instance Name</em>' attribute.
     * @see #setInstanceName(String)
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage#getStrategy_InstanceName()
     * @model id="true" required="true"
     * @generated
     */
    String getInstanceName();

    /**
     * Sets the value of the '{@link org.marketcetera.photon.strategy.engine.model.core.Strategy#getInstanceName <em>Instance Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Instance Name</em>' attribute.
     * @see #getInstanceName()
     * @generated
     */
    void setInstanceName(String value);

    /**
     * Returns the value of the '<em><b>Language</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Language</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Language</em>' attribute.
     * @see #setLanguage(String)
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage#getStrategy_Language()
     * @model required="true"
     * @generated
     */
    String getLanguage();

    /**
     * Sets the value of the '{@link org.marketcetera.photon.strategy.engine.model.core.Strategy#getLanguage <em>Language</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Language</em>' attribute.
     * @see #getLanguage()
     * @generated
     */
    void setLanguage(String value);

    /**
     * Returns the value of the '<em><b>Script Path</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Script Path</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Script Path</em>' attribute.
     * @see #setScriptPath(String)
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage#getStrategy_ScriptPath()
     * @model
     * @generated
     */
    String getScriptPath();

    /**
     * Sets the value of the '{@link org.marketcetera.photon.strategy.engine.model.core.Strategy#getScriptPath <em>Script Path</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Script Path</em>' attribute.
     * @see #getScriptPath()
     * @generated
     */
    void setScriptPath(String value);

    /**
     * Returns the value of the '<em><b>Class Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Class Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Class Name</em>' attribute.
     * @see #setClassName(String)
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage#getStrategy_ClassName()
     * @model required="true"
     * @generated
     */
    String getClassName();

    /**
     * Sets the value of the '{@link org.marketcetera.photon.strategy.engine.model.core.Strategy#getClassName <em>Class Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Class Name</em>' attribute.
     * @see #getClassName()
     * @generated
     */
    void setClassName(String value);

    /**
     * Returns the value of the '<em><b>Route Orders To Server</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Route Orders To Server</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Route Orders To Server</em>' attribute.
     * @see #setRouteOrdersToServer(boolean)
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage#getStrategy_RouteOrdersToServer()
     * @model required="true"
     * @generated
     */
    boolean isRouteOrdersToServer();

    /**
     * Sets the value of the '{@link org.marketcetera.photon.strategy.engine.model.core.Strategy#isRouteOrdersToServer <em>Route Orders To Server</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Route Orders To Server</em>' attribute.
     * @see #isRouteOrdersToServer()
     * @generated
     */
    void setRouteOrdersToServer(boolean value);

    /**
     * Returns the value of the '<em><b>Parameters</b></em>' map.
     * The key is of type {@link java.lang.String},
     * and the value is of type {@link java.lang.String},
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Parameters</em>' map isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Parameters</em>' map.
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage#getStrategy_Parameters()
     * @model mapType="org.marketcetera.photon.strategy.engine.model.core.StringToStringMapEntry<org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString>"
     * @generated
     */
    EMap<String, String> getParameters();

} // Strategy
