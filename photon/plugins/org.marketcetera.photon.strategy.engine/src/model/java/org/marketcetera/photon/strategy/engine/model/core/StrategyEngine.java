/**
 * $License$
 *
 * $Id$
 */
package org.marketcetera.photon.strategy.engine.model.core;

import java.util.List;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Strategy Engine</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#getName <em>Name</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#getDescription <em>Description</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#getConnectionState <em>Connection State</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#getConnection <em>Connection</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#getDeployedStrategies <em>Deployed Strategies</em>}</li>
 *   <li>{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#isReadOnly <em>Read Only</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage#getStrategyEngine()
 * @model
 * @generated
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public interface StrategyEngine extends EObject {
    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage#getStrategyEngine_Name()
     * @model required="true"
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Description</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Description</em>' attribute.
     * @see #setDescription(String)
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage#getStrategyEngine_Description()
     * @model
     * @generated
     */
    String getDescription();

    /**
     * Sets the value of the '{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#getDescription <em>Description</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Description</em>' attribute.
     * @see #getDescription()
     * @generated
     */
    void setDescription(String value);

    /**
     * Returns the value of the '<em><b>Connection State</b></em>' attribute.
     * The default value is <code>""</code>.
     * The literals are from the enumeration {@link org.marketcetera.photon.strategy.engine.model.core.ConnectionState}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Connection State</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Connection State</em>' attribute.
     * @see org.marketcetera.photon.strategy.engine.model.core.ConnectionState
     * @see #setConnectionState(ConnectionState)
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage#getStrategyEngine_ConnectionState()
     * @model default="" required="true" transient="true"
     * @generated
     */
    ConnectionState getConnectionState();

    /**
     * Sets the value of the '{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#getConnectionState <em>Connection State</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Connection State</em>' attribute.
     * @see org.marketcetera.photon.strategy.engine.model.core.ConnectionState
     * @see #getConnectionState()
     * @generated
     */
    void setConnectionState(ConnectionState value);

    /**
     * Returns the value of the '<em><b>Connection</b></em>' reference.
     * It is bidirectional and its opposite is '{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngineConnection#getEngine <em>Engine</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Connection</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Connection</em>' reference.
     * @see #setConnection(StrategyEngineConnection)
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage#getStrategyEngine_Connection()
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineConnection#getEngine
     * @model opposite="engine" transient="true"
     * @generated
     */
    StrategyEngineConnection getConnection();

    /**
     * Sets the value of the '{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#getConnection <em>Connection</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Connection</em>' reference.
     * @see #getConnection()
     * @generated
     */
    void setConnection(StrategyEngineConnection value);

    /**
     * Returns the value of the '<em><b>Deployed Strategies</b></em>' reference list.
     * The list contents are of type {@link org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy}.
     * It is bidirectional and its opposite is '{@link org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy#getEngine <em>Engine</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * <b>WARNING:</b> Do not call {@link List#clear()} or
     * {@link List#removeAll(java.util.Collection)} on the returned list if the
     * object is bound to a UI using EMF Data Binding due to due to <a
     * href="http://bugs.eclipse.org/291641">http://bugs.eclipse.org/291641</a>.
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Deployed Strategies</em>' reference list.
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage#getStrategyEngine_DeployedStrategies()
     * @see org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy#getEngine
     * @model opposite="engine" transient="true"
     * @generated
     */
    EList<DeployedStrategy> getDeployedStrategies();

    /**
     * Returns the value of the '<em><b>Read Only</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Read Only</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Read Only</em>' attribute.
     * @see #setReadOnly(boolean)
     * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage#getStrategyEngine_ReadOnly()
     * @model required="true"
     * @generated
     */
    boolean isReadOnly();

    /**
     * Sets the value of the '{@link org.marketcetera.photon.strategy.engine.model.core.StrategyEngine#isReadOnly <em>Read Only</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Read Only</em>' attribute.
     * @see #isReadOnly()
     * @generated
     */
    void setReadOnly(boolean value);

} // StrategyEngine
