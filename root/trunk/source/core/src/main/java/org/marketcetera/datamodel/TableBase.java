package org.marketcetera.datamodel;

import org.marketcetera.core.ClassVersion;

import javax.persistence.*;
import java.util.Date;

/**
 * Reprensents the Marketcetera data model table base class
 * Each table has a "created_on" and "modifiedOn" field
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
@MappedSuperclass
@Inheritance (strategy = InheritanceType.TABLE_PER_CLASS)
/*package */ abstract class TableBase {

    @Column(name = "created_on")
    protected final Date created_on;
    @Column(name = "updated_on")
    protected Date updated_on;

    public static final String MODIFIED_ON_COL_NAME = "updated_on";
    public static final String CREATED_ON_COL_NAME = "created_on";
    @Id
    @GeneratedValue
    @Column(nullable = false, columnDefinition = "INT(11)")
    protected Long id;

    public TableBase() {
        created_on = new Date();
    }

    public Date getCreated_on() {
        return created_on;
    }

    public Date getUpdatedOn() {
        return updated_on;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updated_on = updatedOn;
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
