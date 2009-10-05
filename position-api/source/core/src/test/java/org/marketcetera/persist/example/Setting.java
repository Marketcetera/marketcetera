package org.marketcetera.persist.example;

import org.marketcetera.core.ClassVersion;

import javax.persistence.*;
import java.io.Serializable;

/* $License$ */
/**
 * A setting is a name value pair. The name can not be null.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
@Entity
@Table(name = "test_user_settings", //$NON-NLS-1$
        uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name","owner_id"})}) //$NON-NLS-1$ //$NON-NLS-2$
class Setting implements Serializable {
    private static final long serialVersionUID = 8105197426025853007L;

    /**
     * Creates an instance
     *
     * @param name the setting name, cannot be null.
     * @param value the setting value.
     * @param owner the setting owner.
     */
    public Setting(String name, String value, User owner) {
        setName(name);
        setValue(value);
        setOwner(owner);
    }

    public Setting() {
    }

    /**
     * The setting name.
     *
     * @return the setting name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the setting name
     *
     * @param name the setting name
     */
    private void setName(String name) {
        if(name == null) {
            throw new NullPointerException();
        }
        this.name = name;
    }

    /**
     * the setting value.
     *
     * @return the setting value
     */
    public String getValue() {
        return value;
    }

    /**
     * set the setting value.
     * 
     * @param value the setting value.
     */
    void setValue(String value) {
        this.value = value;
    }
    @ManyToOne
    private User getOwner() {
        return owner;
    }

    private void setOwner(User owner) {
        this.owner = owner;
    }
    @Id
    @GeneratedValue
    private long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }

    /**
     * The attribute owner used in JPQL queries
     */
    static final String ATTRIBUTE_OWNER = "owner"; //$NON-NLS-1$
    /**
     * The entity name used in JPQL queries
     */
    static final String ENTITY_NAME = "Setting"; //$NON-NLS-1$
    private String name;
    private String value;
    private User owner;
    private long id;
}
