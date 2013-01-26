package org.marketcetera.persist;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Entity
@Access(AccessType.FIELD)
@XmlAccessorType(XmlAccessType.NONE)
@Table(name="fruit",uniqueConstraints={@UniqueConstraint(columnNames={"name"})})
@NamedQueries({
    @NamedQuery(name="Fruit.findAll",query="select f from Fruit f"),
    @NamedQuery(name="Fruit.count",query="select count(f) from Fruit f") })
public class Fruit
        extends NDEntityBase
{
    /**
     * Create a new Fruit instance.
     *
     * @param inName
     * @param inDescription
     * @param inType
     */
    public Fruit(String inName,
                 String inDescription,
                 Type inType)
    {
        super(inName,
              inDescription);
        type = inType;
    }
    /**
     * Get the type value.
     *
     * @return a <code>Type</code> value
     */
    public Type getType()
    {
        return type;
    }
    /**
     * Sets the type value.
     *
     * @param inType a <code>Type</code> value
     */
    public void setType(Type inType)
    {
        type = inType;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return new ReflectionToStringBuilder(this).toString();
    }
    /**
     * Create a new Fruit instance.
     */
    @SuppressWarnings("unused")
    private Fruit() {}
    public enum Type {
        APPLE,
        PEAR,
        BANANA;
    }
    @XmlAttribute(required=true)
    @Column(name="type",nullable=false)
    private Type type;
    private static final long serialVersionUID = -3000598761642915963L;
}
