package org.marketcetera.datamodel;

import org.marketcetera.core.ClassVersion;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import java.util.Date;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
@Entity
@Table(name = "journals")
public class Journal extends TableBase {
    private String description;

    @Column(name = "post_date")
    private Date postDate;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }
}
