package org.marketcetera.persist;

import static org.marketcetera.persist.Messages.NAME_ATTRIBUTE_INVALID;
import static org.marketcetera.persist.Messages.NAME_ATTRIBUTE_TOO_LONG;
import static org.marketcetera.persist.Messages.UNSPECIFIED_NAME_ATTRIBUTE;

import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Base class for entities that have a name and description.
 * This class provides support for the name and description
 * properties.
 * <p>
 * Supporting query classes are also provided to easily add
 * query support for the subclasses.
 * 
 * <p>
 * {@link org.marketcetera.persist.SingleNDEntityQuery} provides
 * support for queries that fetch single instance given the entity
 * name. Note that this assumes the names of the entities are unique
 * amongst all its instances. To ensure that, subclasses that make
 * use of this feature should declare a unique constraint on the
 * name attribute.
 * <p>
 * {@link org.marketcetera.persist.MultiNDQuery} provides support
 * for queries that fetch multiple instances of subclasses of this
 * class. The query class provides filters to filter the query
 * results by name and description filters. It also provides
 * orders to order the results by name or description.
 *
 */
@MappedSuperclass
@ClassVersion("$Id$")
public abstract class NDEntityBase
        extends EntityBase
        implements SummaryNDEntityBase
{
    /**
     * The name of this entity
     * @return the name of this entity
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this entity
     * @param name the name of this entity
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The description of this entity.
     * @return The description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description for this entity.
     * @param description the description for this entity.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return super.toString() + "NDEntityBase{" + //$NON-NLS-1$
                "name='" + name + '\'' + //$NON-NLS-1$ $NON-NLS-2$
                ", description='" + description + '\'' + //$NON-NLS-1$
                '}';
    }

    /**
     * Validates if this entity can be saved. This method
     *
     * @throws ValidationException if the validation failed.
     * @throws PersistenceException if a problem was encountered
     * when carrying out validation.
     */
    @PreUpdate
    @PrePersist
    public void validate() throws PersistenceException {
        if(getName() == null || getName().trim().length() < 1) {
            throw new ValidationException(UNSPECIFIED_NAME_ATTRIBUTE);
        }
        if(getName().length() > 255) {
            throw new ValidationException(new I18NBoundMessage1P(
                    NAME_ATTRIBUTE_TOO_LONG,getName()));
        }
        //Verify that the name can be saved
        if(!namePattern.matcher(getName()).matches()) {
            throw new ValidationException(new I18NBoundMessage2P(
                    NAME_ATTRIBUTE_INVALID,getName(),
                    namePattern.toString()));
        }
    }
    /**
     * The pattern for validating name attribute values
     */
    static final Pattern namePattern = Pattern.compile("^[\\p{L}\\p{N}- ]{1,255}$"); //$NON-NLS-1$
    /**
     * 
     */
    @Column(name="name",nullable=false)
    private String name;
    /**
     * 
     */
    @Column(name="description",nullable=true)
    private String description;
    private static final long serialVersionUID = 5752545714007455960L;
}
