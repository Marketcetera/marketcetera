package org.marketcetera.datamodel;

import org.marketcetera.core.ClassVersion;

import javax.persistence.*;
import java.util.Date;

/**
 * Reprensents the Marketcetera data model table base class
 * Each table has a "createdOn" and "modifiedOn" field
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
@MappedSuperclass
@Inheritance (strategy = InheritanceType.TABLE_PER_CLASS)
/*package */ abstract class TableBase {

    @Column(name = "createdOn")
    protected final Date createdOn;
    @Column(name = "updated_on")
    protected Date updatedOn;

    public static final String MODIFIED_ON_COL_NAME = "updatedOn";
    public static final String CREATED_ON_COL_NAME = "createdOn";
    @Id
    @GeneratedValue
    @Column(nullable = false, columnDefinition = "INT(11)")
    protected Long id;

    public TableBase() {
        createdOn = new Date();
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    /* package */ void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Long getId() {
        return id;
    }

/*
    private void setId(Long id) {
        this.id = id;
    }
*/
}
