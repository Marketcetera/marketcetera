/**
 * $License$
 *
 * $Id$
 */
package org.marketcetera.photon.strategy.engine.model.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Connection State</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage#getConnectionState()
 * @model
 * @generated
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public enum ConnectionState implements Enumerator {
    /**
     * The '<em><b>Disconnected</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #DISCONNECTED_VALUE
     * @generated
     * @ordered
     */
    DISCONNECTED(0, "Disconnected", "Disconnected"),

    /**
     * The '<em><b>Connected</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #CONNECTED_VALUE
     * @generated
     * @ordered
     */
    CONNECTED(1, "Connected", "Connected");

    /**
     * The '<em><b>Disconnected</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>Disconnected</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #DISCONNECTED
     * @model name="Disconnected"
     * @generated
     * @ordered
     */
    public static final int DISCONNECTED_VALUE = 0;

    /**
     * The '<em><b>Connected</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>Connected</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #CONNECTED
     * @model name="Connected"
     * @generated
     * @ordered
     */
    public static final int CONNECTED_VALUE = 1;

    /**
     * An array of all the '<em><b>Connection State</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final ConnectionState[] VALUES_ARRAY = new ConnectionState[] {
            DISCONNECTED, CONNECTED, };

    /**
     * A public read-only list of all the '<em><b>Connection State</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List<ConnectionState> VALUES = Collections
            .unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Connection State</b></em>' literal with the specified literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ConnectionState get(String literal) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            ConnectionState result = VALUES_ARRAY[i];
            if (result.toString().equals(literal)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Connection State</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ConnectionState getByName(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            ConnectionState result = VALUES_ARRAY[i];
            if (result.getName().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Connection State</b></em>' literal with the specified integer value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ConnectionState get(int value) {
        switch (value) {
        case DISCONNECTED_VALUE:
            return DISCONNECTED;
        case CONNECTED_VALUE:
            return CONNECTED;
        }
        return null;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final int value;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final String name;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final String literal;

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private ConnectionState(int value, String name, String literal) {
        this.value = value;
        this.name = name;
        this.literal = literal;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getValue() {
        return value;
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
    public String getLiteral() {
        return literal;
    }

    /**
     * Returns the literal value of the enumerator, which is its string representation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        return literal;
    }

} //ConnectionState
